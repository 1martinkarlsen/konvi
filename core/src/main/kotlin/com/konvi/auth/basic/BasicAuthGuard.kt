package com.konvi.auth.basic

import com.konvi.auth.AuthGuard
import com.konvi.config.AuthConfig
import com.konvi.exception.UnauthorizedException
import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import io.ktor.server.auth.basicAuthenticationCredentials
import io.ktor.server.response.header
import me.tatarka.inject.annotations.Inject

class BasicAuthGuard @Inject constructor(
    private val basicAuthenticator: BasicAuthenticator,
    private val authConfig: AuthConfig
) : AuthGuard {
    override suspend fun authenticate(call: ApplicationCall) {
        val credentials = call.request.basicAuthenticationCredentials()
        val principal = credentials?.let { basicAuthenticator.authenticate(it.name, it.password) }

        if (principal != null) {
            call.authentication.principal(principal)
        } else {
            call.response.header(HttpHeaders.WWWAuthenticate, "Basic realm=\"${authConfig.basic.realm}\"")
            throw UnauthorizedException()
        }
    }
}
