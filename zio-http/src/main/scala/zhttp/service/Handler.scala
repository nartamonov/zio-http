package zhttp.service

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import zhttp.http._
import zhttp.service.server.WebSocketUpgrade
import zhttp.service.server.content.handlers.UnsafeRequestHandler.{UnsafeChannel, UnsafeContent}
import zio.{UIO, ZIO}

import java.net.{InetAddress, InetSocketAddress}

@Sharable
private[zhttp] final case class Handler[R](
  app: HttpApp[R, Throwable],
  runtime: HttpRuntime[R],
  config: Server.Config[R, Throwable],
) extends SimpleChannelInboundHandler[Any](false)
    with WebSocketUpgrade[R] { self =>

  type Ctx = ChannelHandlerContext

  override def handlerAdded(ctx: Ctx): Unit = {
    ctx.channel().config().setAutoRead(false): Unit
    ctx.read(): Unit
  }

  override def channelRead0(ctx: Ctx, msg: Any): Unit = {
    implicit val iCtx: ChannelHandlerContext = ctx
    msg match {
      case jReq: HttpRequest =>
        def unsafeBody(
          callback: (
            UnsafeChannel,
            UnsafeContent,
          ) => Unit,
        ): Unit = {
          val httpContentHandler = ctx.pipeline().get(HTTP_CONTENT_HANDLER)
          if (httpContentHandler == null) {
            ctx
              .pipeline()
              .addAfter(HTTP_REQUEST_HANDLER, HTTP_CONTENT_HANDLER, new RequestBodyHandler(callback, config)): Unit
          } else {
            httpContentHandler.asInstanceOf[RequestBodyHandler[R]].callback = callback
          }
        }
        lazy val remoteAddress: Option[InetAddress] = {
          ctx.channel().remoteAddress() match {
            case m: InetSocketAddress => Some(m.getAddress)
            case _                    => None
          }
        }
        val request                                 = Request(
          Method.fromHttpMethod(jReq.method()),
          URL.fromString(jReq.uri()).getOrElse(null),
          Headers.make(jReq.headers()),
          remoteAddress,
          data = HttpData.Incoming(unsafeBody),
        )

        unsafeRun(
          jReq,
          app,
          request,
        )

      case msg: LastHttpContent =>
        if (ctx.pipeline().get(HTTP_CONTENT_HANDLER) != null) {
          ctx.fireChannelRead(msg): Unit
        }

      case msg: HttpContent =>
        if (ctx.pipeline().get(HTTP_CONTENT_HANDLER) != null) {
          ctx.fireChannelRead(msg): Unit
        }

      case _ => ctx.fireChannelRead(Response.status(Status.NOT_ACCEPTABLE)): Unit
    }

  }

  /**
   * Executes http apps
   */
  private def unsafeRun[A](
    jReq: HttpRequest,
    http: Http[R, Throwable, A, Response],
    a: A,
  )(implicit ctx: Ctx): Unit = {
    http.execute(a) match {
      case HExit.Effect(resM) =>
        unsafeRunZIO {
          resM.foldM(
            {
              case Some(cause) =>
                UIO {
                  ctx.fireChannelRead(
                    Response.fromHttpError(HttpError.InternalServerError(cause = Some(cause))),
                  )
                }
              case None        =>
                UIO {
                  ctx.fireChannelRead(Response.status(Status.NOT_FOUND))
                }
            },
            res =>
              if (self.isWebSocket(res)) UIO(self.upgradeToWebSocket(ctx, jReq, res))
              else {
                for {
                  _ <- UIO {
                    ctx.fireChannelRead(res)
                  }
                } yield ()
              },
          )
        }

      case HExit.Success(res) =>
        if (self.isWebSocket(res)) {
          self.upgradeToWebSocket(ctx, jReq, res)
        } else {
          ctx.fireChannelRead(res): Unit
        }

      case HExit.Failure(e) =>
        ctx.fireChannelRead(e): Unit
      case HExit.Empty      =>
        ctx.fireChannelRead(Response.status(Status.NOT_FOUND)): Unit
    }

  }

  /**
   * Executes program
   */
  private def unsafeRunZIO(program: ZIO[R, Throwable, Any])(implicit ctx: Ctx): Unit =
    runtime.unsafeRun(ctx) {
      program
    }

}
