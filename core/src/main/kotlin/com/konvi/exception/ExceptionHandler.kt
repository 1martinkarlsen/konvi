package com.konvi.exception

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

internal fun Application.configureExceptions() {
    install(StatusPages) {
        exception<HttpException> { call, cause ->
            call.respond(
                status = cause.status,
                message = ErrorResponse(
                    status = cause.status.value,
                    message = cause.message ?: cause.status.description
                )
            )
        }
        exception<Throwable> { call, _ ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(status = 500, message = "Internal Server Error")
            )
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status = status,
                message = ErrorResponse(status = status.value, message = "Not Found")
            )
        }
    }
}
