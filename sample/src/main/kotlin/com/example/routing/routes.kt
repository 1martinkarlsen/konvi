package com.example.routing

import com.konvi.generated.Routes
import com.konvi.route.router
import com.konvi.template.view

fun Routes.api() = router {
    get("/") {
        view("index")
    }
    get("/users", userController::getAll, logMiddleware::test)
    get("/users/{id}", userController::find)

    group("/admin", logMiddleware::test) {
        get("/dashboard") {
            println("DASHBOARD")
            view("index")
        }
    }
}
