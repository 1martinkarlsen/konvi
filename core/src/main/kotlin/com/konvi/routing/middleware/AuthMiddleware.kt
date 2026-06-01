package com.konvi.routing.middleware

import com.konvi.routing.Middleware
import io.ktor.server.routing.RoutingCall
import me.tatarka.inject.annotations.Inject

@Middleware
class AuthMiddleware @Inject constructor(

) {

    suspend fun basic(call: RoutingCall) {

    }
}