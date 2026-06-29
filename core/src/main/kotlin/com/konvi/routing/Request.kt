package com.konvi.routing

import io.ktor.server.routing.RoutingCall

inline fun <reified T> RoutingCall.pathParameter(parameterName: String): T =
    getPathParameter<T>(parameterName)

inline fun <reified T> RoutingCall.queryParameter(parameterName: String): T =
    getQueryParameter<T>(parameterName)
