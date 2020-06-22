package test1

import akka.actor.{Actor,ActorSystem,Props}

//必须继承自Actor
class helloActor extends Actor{
   //Actor中的回调方法,用于获取消息
  //receive Receive 对象
  override def receive:Receive={
    //模式匹配 提取器
    //接收消息并处理
    case "hello"=>println("hello too")
    case "bye"=>println("bye too")
    case "stop"=>{
      
      //context 系统上下文
      context.stop(self)
      context.system.terminate()//关闭ActorSystem
    }
  }
}

object helloActor {
   //ActorSystem
  private val system=ActorSystem("actor_system_object")
  
  //获得actorRef对象
  private val helloActorRef=system.actorOf(Props[helloActor],"helloActor")
 // println(helloActorRef.toString())   Actor[akka://actor_system_object/user/helloActor#-1499854657]
  def main(args: Array[String]): Unit = {
    //给自己发送消息
    helloActorRef ! "hello"
    helloActorRef ! "bye"
    helloActorRef ! "stop"
  }
}