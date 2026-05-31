package com.konvi.http

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

suspend fun RoutingCall.notFound() =
    respond(HttpStatusCode.NotFound, ErrorResponse(HttpStatusCode.NotFound.value, HttpStatusCode.NotFound.description))

suspend fun RoutingCall.badRequest(message: String = HttpStatusCode.BadRequest.description) =
    respond(HttpStatusCode.BadRequest, ErrorResponse(HttpStatusCode.BadRequest.value, message))

suspend fun RoutingCall.unauthorized(message: String = HttpStatusCode.Unauthorized.description) =
    respond(HttpStatusCode.Unauthorized, ErrorResponse(HttpStatusCode.Unauthorized.value, message))

suspend fun RoutingCall.forbidden(message: String = HttpStatusCode.Forbidden.description) =
    respond(HttpStatusCode.Forbidden, ErrorResponse(HttpStatusCode.Forbidden.value, message))
