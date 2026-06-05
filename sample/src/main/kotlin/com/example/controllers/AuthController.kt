package com.example.controllers

import com.example.repositories.UserRepository
import com.konvi.auth.jwt.JwtService
import com.konvi.http.unauthorized
import com.konvi.routing.Route
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class TokenResponse(val token: String)

@Route
class AuthController @Inject constructor(
    private val users: UserRepository,
    private val jwt: JwtService
) {

    suspend fun login(call: RoutingCall) {
        val request = call.receive<LoginRequest>()
        val user = users.findByCredentials(request.username, request.password)
            ?: return call.unauthorized("Invalid credentials")

        val token = jwt.issue(subject = user.id.toString(), claims = mapOf("name" to user.name))
        call.respond(TokenResponse(token))
    }
}
