package com.konvi

import com.konvi.config.loadConfig
import com.konvi.database.configureDatabase
import com.konvi.di.KonviComponent
import com.konvi.exception.configureExceptions
import com.konvi.http.configureHttp
import com.konvi.logging.configureLogging
import com.konvi.route.KonviRouter
import com.konvi.template.configureTemplate
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

object Konvi {
    fun <T : KonviComponent> start(component: T, routes: T.() -> KonviRouter) {
        val config = loadConfig()

        embeddedServer(Netty, port = config.port) {
            install(ContentNegotiation) { json() }

            configureDatabase(config.database)
            configureLogging()
            configureExceptions()
            configureTemplate()
            configureHttp()

            routing {
                component.routes().block(this)
            }
        }.start(wait = true)
    }
}
