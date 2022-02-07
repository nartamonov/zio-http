# Middleware

Middlewares are transformations that one can apply on any Http to produce a new one. 
They can modify requests and responses and also transform them into more concrete domain entities.

 You can think of middlewares as a functions —

```scala
type Middleware[R, E, AIn, BIn, AOut, BOut] = Http[R, E, AIn, BIn] => Http[R, E, AOut, BOut]
```
where
`AIn` and `BIn`: type params represent the type params of the input Http. 
`AOut` and `BOut`:  type params represent the type params of the output Http.

## What is a middleware

- A middleware is a wrapper around a service that provides a means of manipulating the Request sent to service, and/or the Response returned by the service. 
- In some cases, such as Authentication, middleware may even prevent the service from being called.

- Middleware is simply a function that takes one Service as a parameter and returns another Service. 
- In addition to the Service, the middleware function can take any additional parameters it needs to perform its task. 
- Let's look at a simple example.

## Typical usage of a middleware

## Middleware DSL
### Create Middleware
### Combining
### Transforming
### Conditional Application
### Intercept and Contramap

## More Examples

