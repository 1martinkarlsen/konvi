package com.konvi.routing

import com.konvi.routing.middleware.MiddlewarePlugin
import io.ktor.server.application.ApplicationCall
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

@Suppress("TooManyFunctions")
class KonviRoutingBuilder(
    private val routing: Route,
    private val groupMiddlewares: List<suspend (ApplicationCall) -> Unit> = emptyList()
) {

    fun get(
        path: String,
        handler: suspend RoutingCall.() -> Unit
    ) = routing.create(
        path = path,
        middlewares = groupMiddlewares,
        block = { get { handler(call) }}
    )

    fun get(
        path: String,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (ApplicationCall) -> Unit
    ) = routing.create(
        path = path,
        middlewares = groupMiddlewares + middlewares.toList(),
        block = { get { handler(call) } }
    )

    fun get(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit
    ) = routing.create(
        path = path,
        middlewares = groupMiddlewares,
        block = { get { handler(call) } }
    )

    fun get(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (ApplicationCall) -> Unit
    ) = routing.create(
        path = path,
        middlewares = groupMiddlewares + middlewares.toList(),
        block = { get { handler(call) } }
    )

    fun post(path: String, handler: suspend RoutingCall.() -> Unit) =
        routing.create(
            path = path,
            middlewares = groupMiddlewares,
            block = { post { handler(call) } }
        )

    fun post(
        path: String,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (ApplicationCall) -> Unit
    ) = routing.create(
        path = path,
        middlewares = groupMiddlewares + middlewares.toList(),
        block = { post { handler(call) } }
    )

    fun post(path: Regex, handler: suspend RoutingCall.() -> Unit) =
        routing.create(
            path = path,
            middlewares = groupMiddlewares,
            block = { post { handler(call) } }
        )

    fun post(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (ApplicationCall) -> Unit
    ) = routing.create(
        path = path,
        middlewares = groupMiddlewares + middlewares.toList(),
        block = { post { handler(call) } }
    )

    fun put(path: String, handler: suspend RoutingCall.() -> Unit) =
        routing.create(
            path = path,
            middlewares = groupMiddlewares,
            block = { put { handler(call) } }
        )

    fun put(
        path: String,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (ApplicationCall) -> Unit
    ) = routing.create(
        path = path,
        middlewares = groupMiddlewares + middlewares.toList(),
        block = { put { handler(call) } }
    )

    fun put(path: Regex, handler: suspend RoutingCall.() -> Unit) =
        routing.create(
            path = path,
            middlewares = groupMiddlewares,
            block = { put { handler(call) } }
        )

    fun put(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (ApplicationCall) -> Unit
    ) = routing.create(
        path = path,
        middlewares = groupMiddlewares + middlewares.toList(),
        block = { put { handler(call) } }
    )

    fun delete(path: String, handler: suspend RoutingCall.() -> Unit) =
        routing.create(path = path, middlewares = groupMiddlewares, block = { delete { handler(call) } })

    fun delete(
        path: String,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (ApplicationCall) -> Unit
    ) = routing.create(path = path, middlewares = groupMiddlewares + middlewares.toList(), block = { delete { handler(call) } })

    fun delete(path: Regex, handler: suspend RoutingCall.() -> Unit) =
        routing.create(path = path, middlewares = groupMiddlewares, block = { delete { handler(call) } })

    fun delete(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (ApplicationCall) -> Unit
    ) = routing.create(path = path, middlewares = groupMiddlewares + middlewares.toList(), block = { delete { handler(call) } })

    fun patch(path: String, handler: suspend RoutingCall.() -> Unit) =
        routing.create(path = path, middlewares = groupMiddlewares, block = { patch { handler(call) } })

    fun patch(
        path: String,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (ApplicationCall) -> Unit
    ) = routing.create(path = path, middlewares = groupMiddlewares + middlewares.toList(), block = { patch { handler(call) } })

    fun patch(path: Regex, handler: suspend RoutingCall.() -> Unit) =
        routing.create(path = path, middlewares = groupMiddlewares, block = { patch { handler(call) } })

    fun patch(
        path: Regex,
        handler: suspend RoutingCall.() -> Unit,
        vararg middlewares: suspend (ApplicationCall) -> Unit
    ) = routing.create(path = path, middlewares = groupMiddlewares + middlewares.toList(), block = { patch { handler(call) } })

    fun group(path: String, vararg middleware: suspend (ApplicationCall) -> Unit, block: KonviRoutingBuilder.() -> Unit) =
        routing.route(path) { block(KonviRoutingBuilder(this, groupMiddlewares + middleware.toList())) }

    fun group(path: Regex, vararg middleware: suspend (ApplicationCall) -> Unit, block: KonviRoutingBuilder.() -> Unit) =
        routing.route(path) { block(KonviRoutingBuilder(this, groupMiddlewares + middleware.toList())) }

    fun staticResources(remotePath: String, localPath: String) =
        routing.staticResources(remotePath, localPath)

    fun routing(block: Route.() -> Unit) = routing.block()

}

private fun Route.create(
    path: String,
    middlewares: List<suspend (ApplicationCall) -> Unit>,
    block: Route.() -> Unit
) = route(path) {
    if (middlewares.isNotEmpty()) {
        install(MiddlewarePlugin) {
            this.middlewares.addAll(middlewares)
        }
    }
    block()
}

private fun Route.create(
    path: Regex,
    middlewares: List<suspend (ApplicationCall) -> Unit>,
    block: Route.() -> Unit
) = route(path) {
    if (middlewares.isNotEmpty()) {
        install(MiddlewarePlugin) {
            this.middlewares.addAll(middlewares)
        }
    }
    block()
}

fun router(block: KonviRoutingBuilder.() -> Unit) = KonviRouter {
    KonviRoutingBuilder(this).apply(block)
}
