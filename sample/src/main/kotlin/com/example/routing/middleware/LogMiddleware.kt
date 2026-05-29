package com.example.routing.middleware

import com.konvi.route.Middleware
import io.ktor.server.routing.RoutingCall
import me.tatarka.inject.annotations.Inject

@Middleware
class LogMiddleware @Inject constructor() {


    suspend fun test(call: RoutingCall) {
        println("HEJ MED DIG")
    }
}
