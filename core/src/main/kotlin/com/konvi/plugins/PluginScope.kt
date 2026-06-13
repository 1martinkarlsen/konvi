package com.konvi.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.Plugin

interface PluginScope {
    fun <C : Any, B : Any> install(plugin: Plugin<Application, B, C>, configure: B.() -> Unit = {})
}
