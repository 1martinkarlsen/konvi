package com.konvi.config

import com.konvi.logging.logger

private val log = logger<Config>()

internal fun validateConfig(config: Config, environment: Environment) {
    val violations = buildList {
        if (config.auth.jwt.secret.isBlank() || config.auth.jwt.secret == "change-me") {
            add("JWT secret is the insecure default 'change-me'. Set KONVI_AUTH_JWT_SECRET.")
        }
        if (config.database.url.isBlank() || config.database.url.startsWith("jdbc:h2:mem:")) {
            add("Database URL is in-memory H2 (data is lost on restart). Set KONVI_DATABASE_URL.")
        }
    }

    if (violations.isEmpty()) return

    val details = violations.joinToString(separator = "\n") { "  - $it" }

    when (environment) {
        Environment.PRODUCTION ->
            error("Konvi refused to start in PRODUCTION due to insecure configuration:\n$details")
        Environment.DEVELOPMENT ->
            log.warn("Insecure configuration detected (allowed in DEVELOPMENT):\n{}", details)
    }
}
