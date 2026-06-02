package com.example.routing.middleware

import com.konvi.routing.Middleware
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import me.tatarka.inject.annotations.Inject

@Middleware
class LogMiddleware @Inject constructor() {


    suspend fun test(call: ApplicationCall) {
        println("${call.request.httpMethod.value} ${call.request.uri}")
    }
}
