package com.konvi.exception

import com.konvi.http.ErrorResponse
import com.konvi.logging.logger
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respond

private val log = logger<Application>()

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
        exception<Throwable> { call, cause ->
            log.error("Unhandled exception processing ${call.request.httpMethod.value} ${call.request.path()}", cause)
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(
                    status = HttpStatusCode.InternalServerError.value,
                    message = HttpStatusCode.InternalServerError.description
                )
            )
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status = status,
                message = ErrorResponse(
                    status = status.value,
                    message = status.description
                )
            )
        }
    }
}
