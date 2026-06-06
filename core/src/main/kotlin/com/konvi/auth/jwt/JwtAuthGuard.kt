package com.konvi.auth.jwt

import com.konvi.auth.AuthGuard
import com.konvi.config.AuthConfig
import com.konvi.exception.UnauthorizedException
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import io.ktor.server.auth.parseAuthorizationHeader
import io.ktor.server.response.header
import me.tatarka.inject.annotations.Inject

class JwtAuthGuard @Inject constructor(
    private val jwtAuthenticator: JwtAuthenticator,
    private val jwtService: JwtService,
    private val authConfig: AuthConfig
) : AuthGuard {

    override suspend fun authenticate(call: ApplicationCall) {
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