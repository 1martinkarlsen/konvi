package com.konvi.routing

import com.konvi.di.KonviComponent

internal fun KonviComponent.configureFrameworkRoutes() = router {
    staticResources("/static", "static")
}
