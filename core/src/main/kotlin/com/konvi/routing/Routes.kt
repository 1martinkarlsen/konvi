package com.konvi.routing

internal fun configureFrameworkRoutes() = router {
    staticResources("/static", "static")
}
