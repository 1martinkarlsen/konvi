package com.konvi.config

enum class Environment {
    DEVELOPMENT,
    PRODUCTION;

    companion object {
        fun resolve(): Environment {
            val raw = System.getenv("KONVI_ENV") ?: System.getProperty("konvi.environment")
            return when (raw?.lowercase()) {
                null -> DEVELOPMENT
                "dev", "development" -> DEVELOPMENT
                "prod", "production" -> PRODUCTION
                else -> PRODUCTION
            }
        }
    }
}
