package com.konvi.config

import io.ktor.server.config.yaml.YamlConfigLoader

private fun envName(path: String) = path.uppercase().replace(".", "_")

private fun envOverride(path: String): String? =
    System.getProperty(path) ?: System.getenv(envName(path))

private fun resolve(yaml: io.ktor.server.config.ApplicationConfig?, path: String): String? =
    envOverride(path) ?: yaml?.propertyOrNull(path)?.getString()

internal fun loadConfig(): Config {
    val yaml = YamlConfigLoader().load("application.yaml")

    fun str(path: String, default: String) = resolve(yaml, path) ?: default
    fun int(path: String, default: Int) = resolve(yaml, path)?.toIntOrNull() ?: default
    fun long(path: String, default: Long) = resolve(yaml, path)?.toLongOrNull() ?: default
    fun list(path: String) =
        envOverride(path)?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?: yaml?.propertyOrNull(path)?.getList() ?: emptyList()

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
