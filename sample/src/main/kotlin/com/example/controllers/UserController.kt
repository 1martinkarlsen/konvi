package com.example.controllers

import com.example.services.UserService
import com.konvi.routing.Route
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import me.tatarka.inject.annotations.Inject

@Route
class UserController @Inject constructor(private val userService: UserService) {

    suspend fun getAll(call: RoutingCall) {
        println("ROUTE CONTROLLER")
        call.respond(userService.getAll())
    }

    suspend fun find(call: RoutingCall) {
        val id = call.parameters["id"]?.toIntOrNull()
        val user = if (id != null) userService.find(id) else null
        if (user != null) call.respond(user)
        else call.respond(HttpStatusCode.NotFound)
    }
}
