package com.konvi.routing.middleware

import com.konvi.auth.basic.BasicAuthenticator
import com.konvi.auth.jwt.JwtAuthenticator
import com.konvi.auth.jwt.JwtClaims
import com.konvi.auth.jwt.JwtService
import com.konvi.config.AuthConfig
import com.konvi.exception.UnauthorizedException
import com.konvi.routing.Middleware
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import io.ktor.server.auth.basicAuthenticationCredentials
import io.ktor.server.auth.parseAuthorizationHeader
import io.ktor.server.response.header
import me.tatarka.inject.annotations.Inject

@Middleware
class AuthMiddleware @Inject constructor(
    private val basicAuthenticator: BasicAuthenticator,
    private val jwtAuthenticator: JwtAuthenticator,
    private val jwtService: JwtService,
    private val authConfig: AuthConfig
) {

    suspend fun basic(call: ApplicationCall) {
        val credentials = call.request.basicAuthenticationCredentials()
        val principal = credentials?.let { basicAuthenticator.authenticate(it.name, it.password) }

        if (principal != null) {
            call.authentication.principal(principal)
        } else {
            call.response.header(HttpHeaders.WWWAuthenticate, "Basic realm=\"${authConfig.basic.realm}\"")
            throw UnauthorizedException()
        }
    }

    suspend fun jwt(call: ApplicationCall) {
        val token = call.bearerToken()
        val decoded = token?.let { jwtService.verify(it) }
        val principal = decoded?.let { jwtAuthenticator.authenticate(JwtClaims(it)) }

        if (principal != null) {
            call.authentication.principal(principal)
        } else {
            // RFC 6750: omit the error code when no token was supplied; flag invalid_token otherwise.
            val realm = "Bearer realm=\"${authConfig.jwt.realm}\""
            val challenge = if (token == null) realm else "$realm, error=\"invalid_token\""
            call.response.header(HttpHeaders.WWWAuthenticate, challenge)
            throw UnauthorizedException()
        }
    }

    private fun ApplicationCall.bearerToken(): String? =
        (request.parseAuthorizationHeader() as? HttpAuthHeader.Single)
            ?.takeIf { it.authScheme.equals("Bearer", ignoreCase = true) }
            ?.blob
}
