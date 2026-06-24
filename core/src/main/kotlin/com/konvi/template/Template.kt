package com.konvi.template

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.pebble.Pebble

internal fun Application.configureTemplate() {
    install(Pebble) {
        loader(PebbleConfiguration.pebbleLoader())
    }
}
