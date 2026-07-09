package com.konvi.routing

import com.konvi.di.KonviComponent

internal fun KonviComponent.configureFrameworkRoutes(): KonviRouter = buildRouter {
    staticResources("/static", "static")
}
