package com.konvi.routing.middleware

import com.konvi.routing.Middleware
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authentication
import io.ktor.server.auth.basicAuthenticationCredentials
import io.ktor.server.response.header
import io.ktor.server.response.respond
import me.tatarka.inject.annotations.Inject

@Middleware
class AuthMiddleware @Inject constructor(

) {

    suspend fun basic(call: ApplicationCall) {
        val credentials = call.request.basicAuthenticationCredentials()

        if (credentials != null && validateBasic(credentials.name, credentials.password)) {
            call.authentication.principal(UserIdPrincipal(credentials.name))
        } else {
            call.response.header("WWW-Authenticate", "Basic realm=\"Konvi\"")
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    private suspend fun validateBasic(username: String, password: String): Boolean {
        return false
    }
}