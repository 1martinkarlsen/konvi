package com.konvi.template

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.pebble.Pebble
import io.pebbletemplates.pebble.loader.ClasspathLoader

internal fun Application.configureTemplate() {
    install(Pebble) {
        loader(ClasspathLoader().apply {
            prefix = "templates/"
        })
    }
}
