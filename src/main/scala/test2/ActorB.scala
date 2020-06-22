package test2

import akka.actor.{Actor,ActorRef}

class ActorB(val a:ActorRef) extends Actor {
  override def receive: Receive={
    case "start" => {
      println("i am actorB,i am ready")
      //start
      a! "are you ok " //给a发一个消息
      
    }
    case "i am ok too.."=>{
      println("good good")
      Thread.sleep(1000)
      a ! "are you ok "
    }
  }
}