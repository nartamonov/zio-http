package zhttp.http

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.FullHttpRequest
import zio.Task

import java.net.InetAddress

trait HttpConvertor[-X, +A] {
  def convert(request: X): A
}


object HttpConvertor {
  implicit object JReqToRequest extends HttpConvertor[FullHttpRequest, Request] {
    override def convert(jReq: FullHttpRequest): Request = {
      new Request {
        override def method: Method = Method.fromHttpMethod(jReq.method())

        override def url: URL = URL.fromString(jReq.uri()).getOrElse(null)

        override def getHeaders: Headers = Headers.make(jReq.headers())

        override private[zhttp] def getBodyAsByteBuf: Task[ByteBuf] = Task(jReq.content())

        override def remoteAddress: Option[InetAddress] = None
      }
    }
  }
  // not implicit
  private val empty: HttpConvertor[Any, Nothing] = new HttpConvertor[Any, Nothing] {
    override def convert(a: Any): Nothing = a.asInstanceOf
  }

  implicit def identity[A]: HttpConvertor[A, A] = empty // Create empty once for performance.


}
