package com.example.controllers

import com.example.models.User
import com.example.services.UserService
import com.konvi.auth.requireUser
import com.konvi.routing.Route
import com.konvi.routing.pathParameter
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import me.tatarka.inject.annotations.Inject

@Route
class UserController @Inject constructor(private val userService: UserService) {

    suspend fun getAll(call: RoutingCall) {
        call.respond(userService.getAll())
    }

    suspend fun me(call: RoutingCall) {
        call.respond(call.requireUser<User>())
    }

    suspend fun find(call: RoutingCall) {
        val id = call.pathParameter<Int>("id")
        val user = userService.find(id)
        if (user != null) call.respond(user)
        else call.respond(HttpStatusCode.NotFound)
    }
}
