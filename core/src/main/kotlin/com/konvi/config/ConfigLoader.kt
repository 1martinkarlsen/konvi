package com.konvi.config

import io.ktor.server.config.yaml.YamlConfigLoader

internal fun loadConfig(): Config {
    val yaml = YamlConfigLoader().load("application.yaml")

    fun str(path: String, default: String) = yaml?.propertyOrNull(path)?.getString() ?: default
    fun int(path: String, default: Int) = yaml?.propertyOrNull(path)?.getString()?.toIntOrNull() ?: default
    fun long(path: String, default: Long) = yaml?.propertyOrNull(path)?.getString()?.toLongOrNull() ?: default
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
            allowedOrigins = list("konvi.cors.allowedOrigins"),
            allowedHosts = list("konvi.cors.allowedHosts"),
        ),
        auth = AuthConfig(
            basic = BasicAuthConfig(str("konvi.auth.basic.realm", default = "Konvi")),
            jwt = JwtAuthConfig(
                secret = str("konvi.auth.jwt.secret", default = "change-me"),
                issuer = str("konvi.auth.jwt.issuer", default = "konvi"),
                audience = str("konvi.auth.jwt.audience", default = "konvi"),
                realm = str("konvi.auth.jwt.realm", default = "Konvi"),
                expiresInSeconds = long("konvi.auth.jwt.expiresInSeconds", default = 3600)
            )
        )
    )
}
