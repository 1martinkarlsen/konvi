package com.example.routing

import com.konvi.generated.Routes
import com.konvi.routing.router
import com.konvi.template.view

fun Routes.api() = router {
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
}
