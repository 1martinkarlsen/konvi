package com.konvi.auth

import io.ktor.server.application.ApplicationCall

internal interface AuthGuard {
    suspend fun authenticate(call: ApplicationCall)
}