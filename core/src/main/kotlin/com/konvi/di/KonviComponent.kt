package com.konvi.di

import com.konvi.auth.basic.BasicAuthenticator
import com.konvi.auth.jwt.JwtAuthenticator
import com.konvi.auth.jwt.JwtService
import com.konvi.config.AuthConfig
import com.konvi.config.DatabaseConfig
import com.konvi.config.loadConfig
import com.konvi.lifecycle.Lifecycle
import com.konvi.routing.middleware.AuthMiddleware
import com.konvi.template.PebbleConfiguration
import com.konvi.template.TemplateService
import com.zaxxer.hikari.HikariDataSource
import io.pebbletemplates.pebble.PebbleEngine
import me.tatarka.inject.annotations.Provides

abstract class KonviComponent {
    abstract val authMiddleware: AuthMiddleware
    abstract val basicAuthenticator: BasicAuthenticator
    abstract val jwtAuthenticator: JwtAuthenticator
    abstract val jwtService: JwtService

    abstract val lifecycle: List<Lifecycle>
    
    abstract val hikariDataSource: HikariDataSource

    @Provides
    protected fun provideAuthConfig(): AuthConfig = loadConfig().auth
    
    @Provides
    protected fun provideDatabaseConfig(): DatabaseConfig = loadConfig().database
    
    @Provides
    protected fun provideHikariDataSource(dbConfig: DatabaseConfig): HikariDataSource = 
        com.konvi.database.createHikariDataSource(dbConfig)

    @Provides
    protected fun providesTemplateEngine(): TemplateService = TemplateService(
        templateEngine = PebbleEngine.Builder()
            .loader(PebbleConfiguration.pebbleLoader())
            .build()
    )
}
