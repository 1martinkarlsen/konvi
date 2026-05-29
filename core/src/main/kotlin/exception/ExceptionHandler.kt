package com.konvi.exception

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

internal fun Application.configureExceptions() {
    install(StatusPages) {
        exception<HttpException> { call, cause ->
            call.respond(cause.status, ErrorResponse(cause.status.value, cause.message ?: cause.status.description))
        }
        exception<Throwable> { call, _ ->
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse(500, "Internal Server Error"))
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(status, ErrorResponse(status.value, "Not Found"))
        }
    }
}
