package com.konvi.http

import com.konvi.config.CorsConfig
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.ratelimit.RateLimit
import kotlin.time.Duration.Companion.seconds

internal fun Application.configureHttp(corsConfig: CorsConfig) {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)

        allowOrigins { it in corsConfig.allowedOrigins }
    }

    install(RateLimit) {
        global {
            rateLimiter(
                limit = 100,
                refillPeriod = 60.seconds
            )
        }
    }
}
