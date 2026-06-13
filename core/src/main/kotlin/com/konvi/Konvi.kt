package com.konvi

import com.konvi.config.loadConfig
import com.konvi.database.configureDatabase
import com.konvi.di.KonviComponent
import com.konvi.exception.configureExceptions
import com.konvi.http.configureHttp
import com.konvi.logging.configureLogging
import com.konvi.plugins.PluginScope
import com.konvi.routing.KonviRouter
import com.konvi.routing.configureFrameworkRoutes
import com.konvi.template.configureTemplate
import io.ktor.server.application.Application
import io.ktor.server.application.Plugin
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing

private const val GENERATED_COMPONENT = "com.konvi.generated.InjectAppComponent"

object Konvi {
    fun <T : KonviComponent> start(
        component: T,
        routes: T.() -> KonviRouter,
        plugins: PluginScope.() -> Unit = {}
    ) {
        val config = loadConfig()

        embeddedServer(Netty, port = config.port) {
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

            val pluginScope = object : PluginScope {
                override fun <C : Any, B : Any> install(plugin: Plugin<Application, B, C>, configure: B.() -> Unit) {
                    this@embeddedServer.install(plugin, configure)
                }
            }
            pluginScope.plugins()

            routing {
                configureFrameworkRoutes().block(this)
                component.routes().block(this)
            }
        }.start(wait = true)
    }
}

fun <T : KonviComponent> startKonvi(
    routes: T.() -> KonviRouter,
    plugins: PluginScope.() -> Unit = {}
) {
    @Suppress("UNCHECKED_CAST")
    val component = loadGeneratedComponent() as T
    Konvi.start(component, routes, plugins)
}

private fun loadGeneratedComponent(): KonviComponent =
    try {
        Class.forName(GENERATED_COMPONENT).getDeclaredConstructor().newInstance() as KonviComponent
    } catch (e: ClassNotFoundException) {
        error("Generated Konvi component '$GENERATED_COMPONENT' was not found. ${e.message}")
    }
