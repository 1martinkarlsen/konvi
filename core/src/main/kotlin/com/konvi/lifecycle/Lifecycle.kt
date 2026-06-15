package com.konvi.lifecycle

interface Lifecycle {
    suspend fun onStart()
    suspend fun onStop()
}