package com.konvi.auth

import com.konvi.exception.UnauthorizedException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal

/** The authenticated principal for this call, or null if the request is not authenticated. */
inline fun <reified T : Any> ApplicationCall.user(): T? = principal<T>()

/** The authenticated principal for this call, or throws [UnauthorizedException] (401) if absent. */
inline fun <reified T : Any> ApplicationCall.requireUser(): T =
    principal<T>() ?: throw UnauthorizedException()
