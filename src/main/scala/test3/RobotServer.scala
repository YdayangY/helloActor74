package test3
import akka.actor.{Actor,ActorSystem,Props}
import java.util.Date
import com.typesafe.config.ConfigFactory
import java.text.SimpleDateFormat

class RobotServer extends Actor{
  override def receive: Receive={
    case "start"=>println("服务器已经启动")
    case ClientMessage(msg)=>{
      println(s"接收到客户端消息$msg")
      msg match{
        case "hello"=>sender() ! ServerMessage("hello")  //sender()这条消息的客户端  ！发 ServerMessage("hello")响应
        case "what is your name"=>sender() ! ServerMessage("i am robot")
        case "time"=>{
          val d=new Date
          sender() ! ServerMessage(d.toString)
          
        }
        case "date"=>{
          val df=new SimpleDateFormat("yyyy-MM-dd")
          val d=df.format(new Date)
          sender() !ServerMessage(d)
        }
        case _=>sender() ! ServerMessage("are you kidding me?")
      }
    }
  }
}

object RobotServer extends App {
 val host:String ="127.0.0.1"
 val port: Int=10000
 val config=ConfigFactory.parseString(
 s"""
      |akka.actor.provider="akka.remote.RemoteActorRefProvider"
      |akka.remote.netty.tcp.hostname=$host
      |akka.remote.netty.tcp.port=$port
      |""".stripMargin)
      
 //指定Ip和端口
 private val actorSystem=ActorSystem("robot_server",config)
 //这里创建了一个名字为robot_server的服务端Actor引用，这里是可以创建多个名字不同的即可
 //客户端通过:serverActorRef=context.actorSelection(akka.tcp://robot_server@127.0.0.1:10000/user/robot_server)
 //指定一个与服务器端实例的引用
 private val robotServerRef=actorSystem.actorOf(Props[RobotServer],"robot_server")
 robotServerRef ! "start"
}