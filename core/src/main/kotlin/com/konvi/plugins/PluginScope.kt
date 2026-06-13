package com.konvi.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.Plugin

interface PluginScope {
    fun <B : Any, F : Any> install(plugin: Plugin<Application, B, F>, configure: B.() -> Unit = {})
}
