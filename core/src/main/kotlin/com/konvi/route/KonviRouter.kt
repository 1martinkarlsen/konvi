package com.konvi.route

import io.ktor.server.application.isHandled
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.http.content.staticResources

class KonviRouter internal constructor(internal val block: Routing.() -> Unit)

class KonviRoutingBuilder(
    private val routing: Route,
    private val groupMiddlewares: List<suspend (RoutingCall) -> Unit> = emptyList()
) {

    fun get(
        path: String,
        handler: suspend RoutingCall.() -> Unit
    ) = routing.get(path) { handle(call, handler, emptyList())}

    fun get(
        path: String,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (RoutingCall) -> Unit
    ) = routing.get(path) { handle(call, handler, middlewares.toList()) }

    fun get(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit
    ) = routing.get(path) { handle(call, handler, emptyList()) }

    fun get(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (RoutingCall) -> Unit
    ) = routing.get(path) { handle(call, handler, middlewares.toList()) }

    fun post(path: String, handler: suspend RoutingCall.() -> Unit) =
        routing.post(path) { handle(call, handler, emptyList()) }

    fun post(
        path: String,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (RoutingCall) -> Unit
    ) = routing.post(path) { handle(call, handler, middlewares.toList()) }

    fun post(path: Regex, handler: suspend RoutingCall.() -> Unit) =
        routing.post(path) { handle(call, handler, emptyList()) }

    fun post(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (RoutingCall) -> Unit
    ) = routing.post(path) { handle(call, handler, middlewares.toList()) }

    fun put(path: String, handler: suspend RoutingCall.() -> Unit) =
        routing.put(path) { handle(call, handler, emptyList()) }

    fun put(path: String, handler: suspend RoutingCall.() -> Unit, vararg middlewares: suspend (RoutingCall) -> Unit) =
        routing.put(path) { handle(call, handler, middlewares.toList()) }

    fun put(path: Regex, handler: suspend RoutingCall.() -> Unit) =
        routing.put(path) { handle(call, handler, emptyList()) }

    fun put(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (RoutingCall) -> Unit
    ) = routing.put(path) { handle(call, handler, middlewares.toList()) }

    fun delete(path: String, handler: suspend RoutingCall.() -> Unit) =
        routing.delete(path) { handle(call, handler, emptyList()) }

    fun delete(
        path: String,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (RoutingCall) -> Unit
    ) = routing.delete(path) { handle(call, handler, middlewares.toList()) }

    fun delete(path: Regex, handler: suspend RoutingCall.() -> Unit) =
        routing.delete(path) { handle(call, handler, emptyList()) }

    fun delete(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (RoutingCall) -> Unit
    ) = routing.delete(path) { handle(call, handler, middlewares.toList()) }

    fun patch(path: String, handler: suspend RoutingCall.() -> Unit) =
        routing.patch(path) { handle(call, handler, emptyList()) }

    fun patch(
        path: String,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (RoutingCall) -> Unit
    ) = routing.patch(path) { handle(call, handler, middlewares.toList()) }

    fun patch(path: Regex, handler: suspend RoutingCall.() -> Unit) =
        routing.patch(path) { handle(call, handler, emptyList()) }

    fun patch(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (RoutingCall) -> Unit
    ) = routing.patch(path) { handle(call, handler, middlewares.toList()) }

    fun group(path: String, vararg mw: suspend (RoutingCall) -> Unit, block: KonviRoutingBuilder.() -> Unit) =
        routing.route(path) { block(KonviRoutingBuilder(this, groupMiddlewares + mw.toList())) }

    fun group(path: Regex, vararg mw: suspend (RoutingCall) -> Unit, block: KonviRoutingBuilder.() -> Unit) =
        routing.route(path) { block(KonviRoutingBuilder(this, groupMiddlewares + mw.toList())) }

    fun staticResources(remotePath: String, localPath: String) =
        routing.staticResources(remotePath, localPath)

    fun routing(block: Route.() -> Unit) = routing.block()

    private suspend fun handle(
        call: RoutingCall,
        handler: suspend RoutingCall.() -> Unit,
        middlewares: List<suspend (RoutingCall) -> Unit>
    ) {
        (groupMiddlewares + middlewares).forEach {
            if (!call.isHandled) it(call)
        }
        if (!call.isHandled) handler(call)
    }
}

fun router(block: KonviRoutingBuilder.() -> Unit) = KonviRouter {
    KonviRoutingBuilder(this).apply(block)
}
