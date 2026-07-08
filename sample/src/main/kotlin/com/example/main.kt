package com.example

import com.example.routing.api
import com.konvi.generated.RouteComponent
import com.konvi.startKonvi

fun main() = startKonvi(
    RouteComponent::api
)
