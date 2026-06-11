# Konvi

A Kotlin web framework for safe and productive backend development.

## Quick Start

```kotlin
@Route
class UserController @Inject constructor() {
    suspend fun getAll(call: RoutingCall) {
        call.respond(listOf("Alice", "Bob"))
    }
}

fun AppComponent.api() = router {
    get("/users", userController::getAll)
    
    get("/users/me", userController::me, authMiddleware::basic)
}

fun main() = startKonvi(AppComponent::api)
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
```
