package com.example

import com.example.routing.api
import com.konvi.generated.RouteScope
import com.konvi.startKonvi

fun main() = startKonvi(
    RouteScope::api
)
