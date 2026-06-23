package com.konvi.template

import io.pebbletemplates.pebble.loader.ClasspathLoader

internal object PebbleConfiguration {

    internal fun pebbleLoader() = ClasspathLoader().apply {
        prefix = "templates/"
    }
}
