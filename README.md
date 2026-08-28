# Konvi

Konvi is an opinionated framework for building safe web applications.
The framework is built on top of Ktor, with the main purpose of having fail-fast approach.

## Status
Still in development. Working towards an alpha version.

## Getting started

### Create application
To create a new project, you can use the konvi cli
```
konvi new
```
The command generates a simple hello world Konvi application.

### Run application
To run the application, from cmd
```
./gradlew run
```

## How it works
### Initialize framework
To start the Konvi framework, your main function has to call `startKonvi()`.
The function takes `routes: T.() -> KonviRouter` as parameter and starts the framework.

### Dependency injection
Dependency injection is one of the core concepts of Konvi.
We're using Kotlin-Inject to build the dependency graph at compile time.
The 2 main components are `KonviComponent` and the generated `AppComponent`. These two are responsible for all dependencies
in the application.

For developing websites, you shouldn't have work with these components.
The dependencies that you do care about, are often delegated to scopes like `RouteScope`.

### Routing

Konvi routing takes a different approach than Ktor.
In Konvi we're combining routing functions with routing classes.
The reason for this, is that we want to easily provide dependencies 
to route functions through the classes.
By doing this, route functions become more simple.

#### Routing classes
Classes annotated with `@Route` are added to the dependency graph and
will be provided through `RouteScope`.
Look at this, as an example

```kotlin
@Route
class UserController @Inject constructor() {
    suspend fun getAll(call: RoutingCall) {
        call.respond(listOf("Alice", "Bob"))
    }
}

fun RouteScope.api() = router {
    get("/users", userController::getAll)
}
```

To create a routing class, annotate the class with `@Route` annotation.
This will make the class available in `RouteScope` which makes it available for your router.
It's important to annotate the class constructor with `@Inject` annotation. This is used by Kotlin-Inject, so you can
inject other dependencies into your routing class.

#### Middlewares
Middlewares are classes that are used for catching a routing call before it hits 
your routing function.
A difference from a middleware function and a routing function is that the middleware
function gets `ApplicationCall` whereas the routing function gets `RoutingCall`.

You can create a middleware class by annotating the class with `@Middleware` annotation.
Middlewares are also added to the dependency graph and will, like Route classes,
be provided through the `RouteScope`.
```kotlin
@Middleware
class LogMiddleware @Inject constructor() {
    suspend fun printLog(call: ApplicationCall) {
        println("${call.request.httpMethod.value} ${call.request.uri}")
    }
}

@Route
class UserController @Inject constructor() {
    suspend fun getAll(call: RoutingCall) {
        call.respond(listOf("Alice", "Bob"))
    }
}

fun RouteScope.api() = router {
    get("/users", userController::getAll, logMiddleware::printLog)
}
```


## Configuration

Create an `application.yaml` in your resources folder:

```yaml
konvi:
  port: ${PORT:8080}
  database:
    url: ${DB_URL:jdbc:h2:mem:test;DB_CLOSE_DELAY=-1}
    driver: ${DB_DRIVER:org.h2.Driver}
    username: ${DB_USERNAME:sa}
    password: ${DB_PASSWORD:}
  auth:
    basic:
      realm: Konvi
    jwt:
      secret: ${JWT_SECRET:change-me}
      issuer: konvi
      audience: konvi
      realm: Konvi
      expiresInSeconds: 3600
  cors:
    allowedOrigins:
      - "https://example.com"
    allowedHosts:
      - "https://example.com"
```
