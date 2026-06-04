package com.konvi.di

import com.konvi.auth.BasicAuthenticator
import com.konvi.auth.JwtAuthenticator
import com.konvi.config.AuthConfig
import com.konvi.config.loadConfig
import com.konvi.routing.middleware.AuthMiddleware
import me.tatarka.inject.annotations.Provides

abstract class KonviComponent {
    abstract val authMiddleware: AuthMiddleware
    abstract val basicAuthenticator: BasicAuthenticator
    abstract val jwtAuthenticator: JwtAuthenticator

    @Provides
    protected fun provideAuthConfig(): AuthConfig = loadConfig().auth
}
