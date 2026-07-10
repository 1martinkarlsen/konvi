package com.konvi.di

import com.konvi.auth.basic.BasicAuthenticator
import com.konvi.auth.jwt.JwtAuthenticator
import com.konvi.auth.jwt.JwtService
import com.konvi.config.AuthConfig
import com.konvi.config.loadConfig
import com.konvi.lifecycle.Lifecycle
import com.konvi.template.PebbleConfiguration
import com.konvi.template.TemplateService
import io.pebbletemplates.pebble.PebbleEngine
import me.tatarka.inject.annotations.Provides

abstract class KonviComponent {
    abstract val basicAuthenticator: BasicAuthenticator
    abstract val jwtAuthenticator: JwtAuthenticator
    abstract val jwtService: JwtService

    abstract val lifecycle: List<Lifecycle>

    @Provides
    protected fun provideAuthConfig(): AuthConfig = loadConfig().auth

    @Provides
    protected fun providesTemplateEngine(): TemplateService = TemplateService(
        templateEngine = PebbleEngine.Builder()
            .loader(PebbleConfiguration.pebbleLoader())
            .build()
    )
}
