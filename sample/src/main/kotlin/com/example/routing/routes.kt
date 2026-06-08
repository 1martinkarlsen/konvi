package com.example.routing

import com.konvi.generated.AppComponent
import com.konvi.routing.router
import com.konvi.template.view
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.get

fun AppComponent.api() = router {
    get("/") {
        view("index")
    }
    get("/users", userController::getAll, logMiddleware::test, authMiddleware::basic)
    get("/users/{id}", userController::find)

    // Basic auth
    get("/me", userController::me, authMiddleware::basic)

    // JWT auth: obtain a token at /login, then call /profile with `Authorization: Bearer <token>`
    post("/login", authController::login)
    get("/profile", userController::me, authMiddleware::jwt)

    group("/admin", logMiddleware::test, authMiddleware::basic) {
        get("/dashboard") {
            println("DASHBOARD")
            view("index")
        }
    }

    routing {
        authenticate("auth-basic") {
            get("/test") {  }
        }

        get("/test/{id}") {  }
    }
}
