package com.konvi.routing.middleware

import com.konvi.auth.basic.BasicAuthGuard
import com.konvi.auth.jwt.JwtAuthGuard
import com.konvi.routing.Middleware
import io.ktor.server.application.ApplicationCall
import me.tatarka.inject.annotations.Inject

@Middleware
class AuthMiddleware @Inject constructor(
    private val basicAuthGuard: BasicAuthGuard,
    private val jwtAuthGuard: JwtAuthGuard
) {

    suspend fun basic(call: ApplicationCall) {
        basicAuthGuard.authenticate(call)
    }

    suspend fun jwt(call: ApplicationCall) {
        jwtAuthGuard.authenticate(call)
    }
}
