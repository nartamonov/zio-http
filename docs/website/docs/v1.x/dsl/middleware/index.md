# Middleware

Middlewares are essentially transformations that one can apply on any Http to produce a new one. They can modify
requests and responses and also transform them into more concrete domain entities.

 You can think of middlewares as a functions —

```scala
type Middleware[R, E, AIn, BIn, AOut, BOut] = Http[R, E, AIn, BIn] => Http[R, E, AOut, BOut]
```
The `AIn` and `BIn` type params represent the type params of the input Http. The `AOut` and `BOut` type params
represent the type params of the output Http.

## What is a middleware

## Typical usage of a middleware

## Middleware DSL
### Create Middleware
### Combining
### Transforming
### Conditional Application
### Intercept and Contramap

## More Examples

