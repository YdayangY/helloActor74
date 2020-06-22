package test4_spark

/**
 * worker->master
 */
//worker向master注册自己
case class RegisterWorkerInfo(id:String,core:Int,ram:Int)

//worker给mater发送心跳消息
case class HeartBeat(id:String)  //发送给服务器的,用serverActorRef ->服务器地址，ip,端口，服务名

//worker发送给自己的消息，告诉自己说要开始周期性的向master发送心跳消息
case object SendHeartBeat //发送给自己所以用自己的actorRef

/**
 * master -> worker
 */
//master向worker发送注册成功的消息
case object RegisteredWorkerInfo

//master发送给自己发送一个检查超时worker的信息，并启动一个调度器 scheduler，周期跟新检测删除超时worker
case object CheckTimeOutWorker

//master发送给自己的消息，删除超时的workers
case object RemoveTimeOutWorker

//存储worker信息的类

class WorkerInfo(val id:String,core:Int,ram:Int) {
  var lastHeartBeatTime: Long=_ //上一次心跳记录时间
  override def toString: String=id+"\tcpu:"+core+",mem:"+ram
}