package com.konvi.config

data class CorsConfig(
    val allowedOrigins: List<String> = emptyList(),
    val allowedHosts: List<String> = emptyList()
)
