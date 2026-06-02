package com.konvi.routing.middleware

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.isHandled

typealias MiddlewareFunction = suspend (ApplicationCall) -> Unit

class KonviMiddlewareConfig {
    internal val middlewares = mutableListOf<MiddlewareFunction>()
}

val MiddlewarePlugin = createRouteScopedPlugin(name = "MiddlewarePlugin", ::KonviMiddlewareConfig) {
    val middlewares = pluginConfig.middlewares.toList()
    onCall { call ->
        middlewares.forEach { mw ->
            if (!call.isHandled) mw(call)
        }
    }
}