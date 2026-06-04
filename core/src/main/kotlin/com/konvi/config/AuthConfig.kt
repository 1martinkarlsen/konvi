package com.konvi.config

data class AuthConfig(
    val basic: BasicAuthConfig = BasicAuthConfig(realm = "Konvi"),
    val jwt: JwtAuthConfig = JwtAuthConfig()
)

data class BasicAuthConfig(val realm: String)

data class JwtAuthConfig(
    val secret: String = "change-me",
    val issuer: String = "konvi",
    val audience: String = "konvi",
    val realm: String = "Konvi",
    val expiresInSeconds: Long = 3600
)
