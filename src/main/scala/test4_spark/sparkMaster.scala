package test4_spark

import akka.actor.{Actor,ActorSystem,Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._ //导入时间单位

class sparkMaster extends Actor{
  //存储worker的信息  id 客户端信息
  val id2WorkerInfo =collection.mutable.HashMap[String,WorkerInfo]()
  override def receive:Receive={
    //收到worker注册过来的信息,提取
    case RegisterWorkerInfo(wkId,core,ram)=>{
      //将worker的信息存储起来,存储到hashMap
      if( !id2WorkerInfo.contains(wkId)){
        val workerInfo=new WorkerInfo(wkId,core,ram)
        id2WorkerInfo +=((wkId,workerInfo))
        //master存储完worker注册的数据之后，要告诉worker说你已经注册成功了
        //sender()当前接收的这条消息的发送者
        sender() !RegisteredWorkerInfo //此时worker会收到注册成功的消息
      }
    }
    
    //接收心跳消息
    case HeartBeat(wkId)=>{
      //master收到worker的心跳消息之后,更新worker的上一次心跳时间
      val workerInfo=id2WorkerInfo(wkId)
    
      //更改心跳时间
      val currentTime=System.currentTimeMillis()
      workerInfo.lastHeartBeatTime=currentTime
    }
    
    //检查客户端状态
    case CheckTimeOutWorker=>{
      import context.dispatcher //使用调度器时必须导入dispatcher
      context.system.scheduler.schedule(0 millis,6000 millis,self,RemoveTimeOutWorker)
    }
    
    case RemoveTimeOutWorker=>{
      //将hashMap中的所有value都拿出来，查看当前时间和上一次心跳时间的差 3000
      val workerInfos=id2WorkerInfo.values  //返回Iterable
      val currentTime=System.currentTimeMillis()
      
      //过滤超时的worker
      //def filter(p:A => Boolean):Repr
      workerInfos.filter(wkInfo=>currentTime-wkInfo.lastHeartBeatTime>3000).foreach(wk=>id2WorkerInfo.remove(wk.id))
      
      println(s"---还剩${id2WorkerInfo.size}存活的worker")
      workerInfos.foreach(println)
      println("========================================")
    }
  }
}

object sparkMaster {
    private var name=""
    private val age=100
    
    def main(args: Array[String]): Unit = {
      var host="localhost"
      var port="10000"
      var masterName="master1"
      
      //检验参数
      if(args.length==3){
        //println(
        //"""
        //    |请输入参数:<host><port><masterName>
        //  """.stripMargin)
        //sys.exit()
        host=args(0)
        port=args(1)
        masterName=args(2)
      }
      val config=ConfigFactory.parseString(
        s"""
            |akka.actor.provider="akka.remote.RemoteActorRefProvider"
            |akka.remote.netty.tcp.hostname=$host
            |akka.remote.netty.tcp.port=$port
            """.stripMargin)
      val actorSystem=ActorSystem("sparkMaster",config)
      val masterActorRef=actorSystem.actorOf(Props[sparkMaster],masterName)
      
      //服务器给自发送一个消息，去启动一个调度器，定期检测HashMap中超时的worker
      masterActorRef ! CheckTimeOutWorker
    }
}