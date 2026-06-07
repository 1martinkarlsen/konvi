package com.konvi.di

import com.konvi.auth.basic.BasicAuthGuard
import com.konvi.auth.jwt.JwtAuthGuard
import com.konvi.auth.jwt.JwtService
import com.konvi.config.AuthConfig
import com.konvi.config.loadConfig
import com.konvi.routing.middleware.AuthMiddleware
import me.tatarka.inject.annotations.Provides

abstract class KonviComponent {
    abstract val authMiddleware: AuthMiddleware
    abstract val basicAuthGuard: BasicAuthGuard
    abstract val jwtAuthGuard: JwtAuthGuard
    abstract val jwtService: JwtService

    @Provides
    protected fun provideAuthConfig(): AuthConfig = loadConfig().auth
}
