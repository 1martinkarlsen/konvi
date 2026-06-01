package com.konvi.config

data class AuthConfig(
    val basic: BasicAuthConfig = BasicAuthConfig(realm = "Konvi")
)

data class BasicAuthConfig(val realm: String)
