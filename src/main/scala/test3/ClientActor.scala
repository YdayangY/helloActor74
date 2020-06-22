package test3

import akka.actor.{Actor,ActorRef,ActorSelection,ActorSystem,Props}
import com.typesafe.config.ConfigFactory
import scala.io.StdIn

class ClientActor extends Actor{
  var serverActorRef: ActorSelection=_//创建一个ActorSelection的空对象
  //容器中的生命周期  preStart()在Actor启动前运行这个方法
  override def preStart(): Unit={
    println("preStart()")
    //可以启动服务器,查看服务器的ip和端口 akka.tcp://robot_server@127.0.0.1:10000
    // /user/robot_server指服务端，robotServerRef= actorSystem.actorOf(Props[RobotServer],"robot_server")
    serverActorRef=context.actorSelection("akka.tcp://robot_server@127.0.0.1:10000/user/robot_server")
  }
  
  override def receive:Receive={
    //接到服务器的消息,只要是String类型，直接打印
    case "start"=>println("客户端已经启动")//如果是"start"字符串
    case msg:String=>{
      println("client端要发送的消息:"+msg)
      serverActorRef ! ClientMessage(msg) //通过服务端的actor的引用发送一个请求对象
    }
    case ServerMessage(msg)=>println( s"收到服务器端的消息$msg")
  }
  
}


object ClientActor extends App {
  val host:String="127.0.0.1"
  val port:Int=20000
  val config=ConfigFactory.parseString(
      s"""
          |akka.actor.provider="akka.remote.RemoteActorRefProvider"
          |akka.remote.netty.tcp.hostname=$host
          |akka.remote.netty.tcp.port=$port
          |""".stripMargin)
  private val clientSystem=ActorSystem("client",config)//创建as‘
	private val actorRef=clientSystem.actorOf(Props[ClientActor],"client_actor") //创建actor代理
	actorRef!"start"
	
	//循环请求用户输入
	while(true){
	  println("请输入请求")
	  val request=StdIn.readLine()  //Scanner
	  actorRef ! request
	}
}
  