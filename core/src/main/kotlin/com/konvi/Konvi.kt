package com.konvi

import com.konvi.config.loadConfig
import com.konvi.database.configureDatabase
import com.konvi.di.KonviComponent
import com.konvi.exception.configureExceptions
import com.konvi.http.configureHttp
import com.konvi.logging.configureLogging
import com.konvi.routing.KonviRouter
import com.konvi.template.configureTemplate
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

private const val GENERATED_COMPONENT = "com.konvi.generated.InjectAppComponent"

object Konvi {
    fun <T : KonviComponent> start(component: T, routes: T.() -> KonviRouter) {
        val config = loadConfig()

        embeddedServer(Netty, port = config.port) {
            install(ContentNegotiation) { json() }

            configureDatabase(config.database)
            configureLogging()
            configureExceptions()
            configureTemplate()
            configureHttp(
                corsConfig = config.cors,
                authConfig = config.auth,
                basicAuthenticator = component.basicAuthenticator,
                jwtAuthenticator = component.jwtAuthenticator,
                jwtService = component.jwtService
            )

            routing {
                component.routes().block(this)
            }
        }.start(wait = true)
    }
}

fun <T : KonviComponent> startKonvi(routes: T.() -> KonviRouter) {
    @Suppress("UNCHECKED_CAST")
    val component = loadGeneratedComponent() as T
    Konvi.start(component, routes)
}

private fun loadGeneratedComponent(): KonviComponent =
    try {
        Class.forName(GENERATED_COMPONENT).getDeclaredConstructor().newInstance() as KonviComponent
    } catch (e: ClassNotFoundException) {
        error("Generated Konvi component '$GENERATED_COMPONENT' was not found. ${e.message}")
    }
