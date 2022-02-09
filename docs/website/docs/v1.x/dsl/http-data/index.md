# HttpData
`HttpData` is a domain to model the content that is to be written in HttpChannel. `HttpRequest` and `HttpResponse` store content as `ByteBuf`. `HttpData` helps you decode and encode this content into simpler, easier to use data types while creating a Request or Response for ZIO HTTP.
## Server-side usage of `HttpData`
On the server-side, `ZIO-HTTP` is adding `HttpData.Empty` as the pre-defined data to the `Response`. You may replace it with another `HttpData`.

To attach data to a response you can use the `Response` constructors.
```scala
  val res: Response = Response( data = HttpData.fromString("Some String"))
```
## Client-side usage of `HttpData`
On the client-side, `ZIO-HTTP` is adding `HttpData.Empty` as the pre-defined data to the `Request`. You may replace it with another `HttpData`.

To attach data to a request you can use the `Response` constructors.
```scala
  val req: Request = Request(data = HttpData.fromString("Some String"))
```
## Creating an HttpData
`HttpData` can be created using a lot of constructors.
### Creating an empty HttpData
HttpData.Empty is the default data in a ZIO HTTP Request and Response. To create an empty HttpData you can use Http.empty
```scala
  val emptyHttpData: HttpData = HttpData.empty
```
### Creating an HttpData from a `String`
To create an `HttpData` that encodes a String you can use `HttpData.fromString`.
```scala
  val textHttpData: HttpData = HttpData.fromString("any string", CharsetUtil.UTF_8)
```
### Creating an HttpData from a `ByteBuf`
To create an `HttpData` that encodes a ByteBuf you can use `HttpData.fromByteBuf`.
```scala
  val binaryByteBufHttpData: HttpData = HttpData.fromByteBuf(Unpooled.copiedBuffer("Some string", CharsetUtil.UTF_8))
```
### Creating an HttpData from `Chunk of Bytes`
To create an `HttpData` that encodes chunk of bytes you can use `HttpData.fromChunk`.
```scala
  val chunkHttpData: HttpData = HttpData.fromChunk(Chunk.fromArray("Some Sting".getBytes(CharsetUtil.UTF_8)))
```
### Creating an HttpData from a `Stream`
To create an `HttpData` that encodes a Stream you can use `HttpData.fromStream`.
- Using a Stream of Bytes
```scala
  val streamHttpData: HttpData = HttpData.fromStream(ZStream.fromChunk(Chunk.fromArray("Some String".getBytes(HTTP_CHARSET))))
```
- Using a Stream of String
```scala
  val streamHttpData: HttpData = HttpData.fromStream(ZStream("a", "b", "c"), CharsetUtil.UTF_8)
```
### Creating an HttpData from a `File`
To create an `HttpData` that encodes a File you can use `HttpData.fromFile`.
```scala
  val fileHttpData: HttpData = HttpData.fromFile(new io.File(getClass.getResource("/fileName.txt").getPath))
```
## Converting `HttpData` to `ByteBuf`
To convert an `HttpData` to `ButrBuf`  you can call the `toButeBuf` method on it, which returns a `Task[ByteBuf]`.
```scala
  val textHttpData: HttpData = HttpData.fromString("any string", CharsetUtil.UTF_8)
  val textByteBuf: Task[ByteBuf] = textHttpData.toByteBuf
```
