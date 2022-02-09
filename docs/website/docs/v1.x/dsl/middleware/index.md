# Middleware

Middlewares are transformations that one can apply on any Http to produce a new one. 
They can modify requests and responses and also transform them into more concrete domain entities.

 Middlewares can also be thought of as functions —

```scala
type Middleware[R, E, AIn, BIn, AOut, BOut] = Http[R, E, AIn, BIn] => Http[R, E, AOut, BOut]
```

* `AIn` and `BIn` are type params of the input Http. 
* `AOut` and `BOut` are type params of the output Http.

## What is a middleware

- A middleware is a wrapper around ```HTTP``` that provides a means of manipulating the Request sent to service, and/or the Response returned by the service. 
- In some cases, such as Authentication, middleware may even prevent the service from being called.
- Middleware is simply a function that takes one ```Http``` as a parameter and returns another ```Http```.  

## Example of a middleware

1. Imports required by the customised server.
    ```scala
    import zhttp.http._
    import zhttp.http.middleware.HttpMiddleware
    import zhttp.service.Server
    import zio.clock.{Clock, currentTime}
    import zio.console.Console
    import zio.duration._
    import zio.{App, ExitCode, URIO, ZIO}
    
    import java.io.IOException
    import java.util.concurrent.TimeUnit
    ```
2. Build a regular app
     ```scala
     val app: HttpApp[Clock, Nothing] = Http.collectZIO[Request] {
       // this will return result instantly
       case Method.GET -> !! / "text"         => ZIO.succeed(Response.text("Hello World!"))
       // this will return result after 5 seconds, so with 3 seconds timeout it will fail
       case Method.GET -> !! / "long-running" => ZIO.succeed(Response.text("Hello World!")).delay(5 seconds)
     }
     ```
3. And then use ```++``` to combine multiple middleware functions
    ```scala
    val middlewares: HttpMiddleware[Console with Clock, IOException] =
       // print debug info about request and response
       Middleware.debug ++
       // add static header
       Middleware.addHeader("X-Environment", "Dev") ++   
    ```
4. Run it like any app
   ```scala
   override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
       Server.start(8090, (app @@ middlewares)).exitCode
   ```
## Middleware DSL
### Create Middleware
- From example above we have shown, how to create different types of middlewares.

```scala
  Middleware.debug
  Middleware.addHeader("X-Environment", "Dev")
```

- Using ```codec``` for specified encoder and decoder

```scala
   val increment = Middleware.codec[Int, Int](decoder = a => Right(a + 1), encoder = b => Right(b + 1))
```

- Using ```codecZIO``` for specified effectful encoder and decoder

```scala
  
```
- Using ```collect``` middleware using specified function

```scala
  
```
- Using ```collectZIO``` middleware using specified effect function

```scala
  
```
- Using ```fromHttp``` with a specified HttpApp

```scala
  
```
- Using ```codecZIO``` for specified effectful encoder and decoder

```scala
  
```
### Combining of middlewares
Middlewares can be combined 

```scala
  // print debug info about request and response
  Middleware.debug ++
  // add static header
  Middleware.addHeader("X-Environment", "Dev") ++
```
### Transforming application of middlewares
We can use ```flatMap``` or ```map``` for transforming outputs

```scala
  val increment = Middleware.codec[Int, Int](decoder = a => Right(a + 1), encoder = b => Right(b + 1))

  val mid = increment.flatMap(i => Middleware.succeed(i + 1))
  val app = Http.identity[Int] @@ mid

  val mid = increment.mapZIO(i => UIO(i + 1))
  val app = Http.identity[Int] @@ mid

```
### Conditional application of middlewares
Logical operator to decide which middleware to select based on the predicate.

- Using ```ifThenElse``` with a specified HttpApp

```scala
  
```
- Using ```ifThenElseZIO``` for specified effectful encoder and decoder

```scala
  
```
- Using ```when``` with a specified HttpApp

```scala
  
```
- Using ```whenZIO``` for specified effectful encoder and decoder

```scala
  
```

### Intercept and Contramap
Preprocesses the incoming value for the outgoing Http.
- ```contraMap``` Preprocesses the incoming value for the outgoing Http.
```scala
  
```
- ```contraMapZIO``` Preprocesses the incoming value using a ZIO, for the outgoing Http.
```scala
  
```
- ```intercept``` Creates a new middleware using transformation functions
```scala
  
```
- ```interceptZIO``` Creates a new middleware using effect-ful transformation functions
```scala
  
```

## More Examples
## Out of the box Middlewares
### Basic Auth
### CORS
### CSRF

