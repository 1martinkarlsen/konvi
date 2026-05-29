package com.konvi.route

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall

suspend fun RoutingCall.created(body: Any, location: String? = null) {
    location?.let { response.header(HttpHeaders.Location, it) }
    respond(HttpStatusCode.Created, body)
}

suspend fun RoutingCall.noContent() = respond(HttpStatusCode.NoContent)

suspend fun RoutingCall.notFound() = respond(HttpStatusCode.NotFound)

suspend fun RoutingCall.badRequest(body: Any) = respond(HttpStatusCode.BadRequest, body)
