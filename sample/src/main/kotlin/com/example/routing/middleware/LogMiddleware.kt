package com.example.routing.middleware

import com.konvi.routing.Middleware
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.routing.RoutingCall
import me.tatarka.inject.annotations.Inject

@Middleware
class LogMiddleware @Inject constructor() {


    suspend fun test(call: RoutingCall) {
        println("${call.request.httpMethod.value} ${call.request.uri}")
    }
}
