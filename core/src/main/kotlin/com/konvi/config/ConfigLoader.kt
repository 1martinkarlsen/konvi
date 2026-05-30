package com.konvi.config

import io.ktor.server.config.yaml.YamlConfigLoader

internal fun loadConfig(): Config {
    val yaml = YamlConfigLoader().load("application")

    fun str(path: String, default: String) = yaml?.propertyOrNull(path)?.getString() ?: default
    fun int(path: String, default: Int) = yaml?.propertyOrNull(path)?.getString()?.toIntOrNull() ?: default
    fun list(path: String) = yaml?.propertyOrNull(path)?.getList() ?: emptyList()

    return Config(
        port = int(path = "konvi.port", default = 8080),
        database = DatabaseConfig(
            url = str("konvi.database.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"),
            driver = str("konvi.database.driver", "org.h2.Driver"),
            username = str("konvi.database.username", "sa"),
            password = str("konvi.database.password", "")
        ),
        cors = CorsConfig(
            allowedOrigins = list("konvi.cors.allowedOrigins")
        )
    )
}
