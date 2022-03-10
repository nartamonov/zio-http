# Work in progress

## Notes on the usage of `CharSequence`s

zio-http extensively uses `java.lang.CharSequence`s to improve performance and compatibility with [zio-json](https://zio.github.io/zio-json/). Many methods do not produce plain `String`s but instead return `CharSequence`s. For example:

```scala
> Response().contentType
val res0: Option[CharSequence] = None
```

**One of the caveats you should remember is that two objects that implements `CharSequence` are not comparable in general case.** Interface `CharSequence` does not refine the general contracts of the `equals` and `hashCode` methods. Each object may be implemented by a different class, and there is no guarantee that each class will be capable of testing its instances for equality with those of the other.

Consider the following test:

```scala
testM("") {
  val app = Http.collectHttp[Request] {
    case _ => Http.text("OK")
  }

  val r = Request(method = Method.GET, url = URL(Path("/a/b/c")))
  assertM(app(r).map(_.contentType))(isSome(equalTo(HeaderValues.textPlain)))
}
```

It fails with the cryptic message:

```
Some(text/plain) did not satisfy isSome(equalTo(text/plain))
```

That's because the method `Response.contentType` actually produces `String` but `HeaderValues.textPlain` is an instance of optimized `io.netty.util.AsciiString`. They are not comparable to each other.

To mitigate such obstacles zio-http provides special 