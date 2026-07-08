package com.konvi.routing

import com.konvi.di.KonviComponent

internal fun KonviComponent.configureFrameworkRoutes(): KonviRouter = KonviRouter {
    KonviRoutingBuilder(routing = this).apply {
        staticResources("/static", "static")
    }
}
