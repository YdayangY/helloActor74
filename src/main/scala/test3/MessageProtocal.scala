package test3

//当一个类被定义为case类后，scala会自动创建一个伴生对象帮你实习一系列的方法
//1.实现了apply方法,就不用自己new关键字创建对象
//2.实现了unapply方法,通过模式匹配获取类属性，是Scala中抽取器的实现和模式匹配的关键方法
//3.实现了类构参数的getter方法(构造参数默认被声明为val)，构造参数声明为var时，将帮你实现setter 和 getter
//4.默认实现toString equals copy hasecode等

//消息协议的样例类
//1.服务端发送给客户端的协议
case class ServerMessage (msg:String) //apply("xxx")

//客户端发送给服务器的协议
case class ClientMessage (msg:String)