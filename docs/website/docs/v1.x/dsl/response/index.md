# Response

**ZIO HTTP** `Response` is designed to encode HTTP Response.
It supports all HTTP status codes and headers along with custom methods and headers (as defined in [RFC2616](https://datatracker.ietf.org/doc/html/rfc2616) )

## Creating a Response

`Response` can be created with `status`, `headers` and `data`.  

The below snippet creates a response with default params, `status` as `Status.OK`, `headers` as `Headers.empty`, `data` as `HttpData.Empty`.
```scala
 val request: Request = Request()
```
### Empty Response

- `ok` creates an empty response with status code 200
```scala
 val res: Response = Response.ok
```

- `status` creates an empty response with provided status code.
```scala
 val res: Response = Response.status(Status.CONTINUE)
```

### Response with Content-Type

- `text` creates a response with content-type set to text/plain
```scala
 val res: Response = Response.text("hey")
```
- `json` creates a response with content-type set to application/json
```scala
 val res: Response = Response.json("""{"greetings": "Hello World!"}""")
```
- `html` creates a response with content-type set to text/html
```scala
 val res: Response = Response.html(Html.fromString("html text"))
```

### Response from HttpError

`fromHttpError` creates a response with provided `HttpError`
```scala
 val res: Response = Response.fromHttpError(HttpError.BadRequest())
```

### Response from Socket

`fromSocket` creates an effectful response from `Socket` provided
```scala
 val socket: Socket[Any, Nothing, Any, WebSocketFrame] = Socket.succeed(WebSocketFrame.text("Greetings!"))
 val res: ZIO[Any, Nothing, Response] = Response.fromSocket(socket)
```

### Response from SocketApp

`fromSocketApp` creates an effectful response from `SocketApp` provided
```scala
 val socket= Socket.succeed(WebSocketFrame.text("Greetings!"))
 val socketApp = SocketApp(socket)
 val res: ZIO[Any, Nothing, Response] = Response.fromSocketApp(socketApp)
```

## Adding Cookie to Response

`addCookie` adds cookies in the headers of the response.
```scala
 val cookie = Cookie("key", "value").withMaxAge(5 days)
 val res = Response.ok.addCookie(cookie.withPath(!! / "cookie").withHttpOnly)
```