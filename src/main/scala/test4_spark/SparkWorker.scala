package test4_spark

import java.util.UUID
import akka.actor.{Actor,ActorSelection,ActorSystem,Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._ //导入时间单位

class SparkWorker(masterUrl:String) extends Actor{
  //master的actorRef
  var masterProxy:ActorSelection=_
  val workId=UUID.randomUUID().toString //客户端的id
  
  override def preStart():Unit={
    masterProxy =context.actorSelection(masterUrl)
    //println(masterProxy.pathString)
  }
  
  override def receive: Receive={
    case "started"=>{//自己已就绪
      //向master注册自己的信息,id,core,ram
      //TODO:获取自己的cpu，内存
      println("客户端"+workId+"启动...")
      masterProxy ! RegisterWorkerInfo(workId,4,32*1024)//向Master发送注册信息
      }
    
    case RegisteredWorkerInfo =>{//master通过响应发送给自己的注册成功消息，接收到这个消息后，则定时发心跳给服务器
      //worker启动一个定时器,定时向master发送心跳
      import context.dispatcher //因为schedule中需要参数  implicit executor:ExecutionContext,sender:ActorRef=Actor.noSender
      //context上下文指的是当前的actor
      //scheduler定时调度器对象 ->
      /*
       * final def schedule(
       * 	initialDelay :FiniteDuration,
       *  interval: FiniteDuration,
       *  receiver: ActorRef,
       *  message: Any)
       *  (implicit executor:ExecutionContext,sender:ActorRef=Actor.noSender):Cancellable=schedule(initialDelay,interval,new Runnable)
       * )
       * */
     context.system.scheduler.schedule(0 millis,1500 millis,self,SendHeartBeat)
    }
    case SendHeartBeat=>{
      //开始向master发送心跳
      println(s"---$workId 发送心跳----")
      masterProxy ! HeartBeat(workId)//此时Master会收到心跳消息
    }
   }
}


object SparkWorker{
  def main(args: Array[String]): Unit = {
    var host="localhost"
    var port="10001"
    var workName="spark_worker"
    var masterURL="akka.tcp://sparkMaster@localhost:10000/user/master1"
    
    //检验命令行参数
    if(args.length==4){
      //println(
      //  |请输入参数<host><port><workName><masterURL>
      //    "".stripMargin)
      //sys.exit() 退出程序
      host=args(0)
      port=args(1)
      workName=args(2)
      masterURL=args(3)
      
    }
    
    val config=ConfigFactory.parseString(
        s"""
            |akka.actor.provider="akka.remote.RemoteActorRefProvider"
            |akka.remote.netty.tcp.hostname=$host
            |akka.remote.netty.tcp.port=$port
            """.stripMargin)
    val actorSystem=ActorSystem("sparkWorker",config)
    //创建自己的actorRef
    val workerActorRef=actorSystem.actorOf(Props(new SparkWorker(masterURL)),workName)
    //给自己发送一个启动消息,标识自己已经就绪
    workerActorRef !"started"
  }
}