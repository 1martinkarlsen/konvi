package com.konvi.http

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.konvi.auth.BasicAuthenticator
import com.konvi.auth.JwtAuthenticator
import com.konvi.auth.JwtClaims
import com.konvi.config.AuthConfig
import com.konvi.config.CorsConfig
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.basic
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.ratelimit.RateLimit
import kotlin.time.Duration.Companion.seconds

internal fun Application.configureHttp(
    corsConfig: CorsConfig,
    authConfig: AuthConfig,
    basicAuthenticator: BasicAuthenticator,
    jwtAuthenticator: JwtAuthenticator
) {
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

    // Registered so the native Ktor path (`authenticate("auth-basic" | "auth-jwt") { }`) is available
    // to developers who drop down to raw routing via KonviRouter.routing { }.
    install(Authentication) {
        basic("auth-basic") {
            realm = authConfig.basic.realm
            validate { credential ->
                basicAuthenticator.authenticate(credential.name, credential.password)
            }
        }
        jwt("auth-jwt") {
            realm = authConfig.jwt.realm
            verifier(
                JWT.require(Algorithm.HMAC256(authConfig.jwt.secret))
                    .withIssuer(authConfig.jwt.issuer)
                    .withAudience(authConfig.jwt.audience)
                    .build()
            )
            validate { credential -> jwtAuthenticator.authenticate(JwtClaims(credential.payload)) }
        }
    }
}
