package com.konvi.di

import com.konvi.config.DatabaseConfig
import com.konvi.config.loadConfig
import com.konvi.database.createHikariDataSource
import com.zaxxer.hikari.HikariDataSource
import me.tatarka.inject.annotations.Provides

interface DatabaseContext {
    val hikariDataSource: HikariDataSource

    @Provides
    fun provideDatabaseConfig(): DatabaseConfig = loadConfig().database

    @Provides
    fun provideHikariDataSource(dbConfig: DatabaseConfig): HikariDataSource =
        createHikariDataSource(dbConfig)
}