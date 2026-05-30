package com.konvi.template

import io.ktor.server.pebble.PebbleContent
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall

suspend fun RoutingCall.view(template: String, data: Map<String, Any> = emptyMap()) {
    respond(PebbleContent("$template.html", data))
}
