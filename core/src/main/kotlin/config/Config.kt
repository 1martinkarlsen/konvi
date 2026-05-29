package com.konvi.config

data class Config(
    val port: Int = 8080,
    val database: DatabaseConfig = DatabaseConfig()
)
