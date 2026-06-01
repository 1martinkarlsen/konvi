package com.konvi.config

data class Config(
    val port: Int = 8080,
    val database: DatabaseConfig = DatabaseConfig(),
    val cors: CorsConfig = CorsConfig(),
    val auth: AuthConfig = AuthConfig(),
)
