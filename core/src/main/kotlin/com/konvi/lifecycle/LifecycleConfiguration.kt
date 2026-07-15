package com.konvi.lifecycle

import com.konvi.di.DatabaseContext
import com.konvi.di.KonviComponent
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopping
import kotlinx.coroutines.runBlocking

internal fun <T> Application.configureLifecycle(component: T) where T : KonviComponent, T : DatabaseContext{
    val lifecycleHooks = listOf(
        component.databaseLifecycle
    ) + component.lifecycle

    monitor.subscribe(ApplicationStarted) {
        runBlocking {
            lifecycleHooks.forEach { hook ->
                hook.onStart()
            }
        }
    }

    monitor.subscribe(ApplicationStopping) {
        runBlocking {
            lifecycleHooks.reversed().forEach { hook ->
                hook.onStop()
            }
        }
    }
}