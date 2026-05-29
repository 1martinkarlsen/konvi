package com.konvi.config

data class DatabaseConfig(
    val url: String = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    val driver: String = "org.h2.Driver",
    val username: String = "sa",
    val password: String = ""
)
