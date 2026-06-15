package com.example.lifecycle

import com.konvi.lifecycle.Lifecycle
import me.tatarka.inject.annotations.Inject

@Inject
class StartupLogger : Lifecycle {
    override suspend fun onStart() {
        println("Konvi sample started")
    }

    override suspend fun onStop() {
        println("Konvi sample stopping")
    }
}
