package com.konvi.http

import com.konvi.auth.basic.BasicAuthenticator
import com.konvi.auth.jwt.JwtAuthenticator
import com.konvi.auth.jwt.JwtClaims
import com.konvi.auth.jwt.JwtService
import com.konvi.config.AuthConfig
import com.konvi.config.CorsConfig
import com.konvi.exception.UnauthorizedException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authentication
import io.ktor.server.auth.basic
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.response.header
import kotlin.time.Duration.Companion.seconds

internal fun Application.configureHttp(
    corsConfig: CorsConfig,
    authConfig: AuthConfig,
    basicAuthenticator: BasicAuthenticator,
    jwtAuthenticator: JwtAuthenticator,
    jwtService: JwtService
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
                val principal = basicAuthenticator.authenticate(credential.name, credential.password)
                if (principal != null) {
                    authentication.principal(principal)
                } else {
                    response.header(HttpHeaders.WWWAuthenticate, "Basic realm=\"${authConfig.basic.realm}\"")
                    throw UnauthorizedException()
                }
            }
        }
        jwt("auth-jwt") {
            realm = authConfig.jwt.realm
            verifier(
                jwtService.verifier
            )
            validate { credential ->
                val principal = jwtAuthenticator.authenticate(JwtClaims(credential.payload))
                if (principal != null) {
                    authentication.principal(principal)
                } else {
                    val realm = "Bearer realm=\"${authConfig.jwt.realm}\""
                    response.header(HttpHeaders.WWWAuthenticate, realm)
                    throw UnauthorizedException()
                }
            }
        }
    }
}
