package test2

import akka.actor.Actor

class ActorA extends Actor{
  override def receive: Receive ={
    case "start" => println("i am a,I am ready")  //自己发给自己
    case "are you ok "=>{  //这是接收另一个actor b发来的消息
      println("收到了 are you ok")
      //利用 sender来获取发送者B
      //sender() are you ok 消息的发送者
      sender() !"i am ok too.."
    }
  }
}