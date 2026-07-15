package com.konvi.di

import com.konvi.config.DatabaseConfig
import com.konvi.config.loadConfig
import com.konvi.database.DatabaseLifecycle
import com.konvi.database.createHikariDataSource
import com.zaxxer.hikari.HikariDataSource
import me.tatarka.inject.annotations.Provides
import org.jetbrains.exposed.v1.core.Table

interface DatabaseContext {
    val hikariDataSource: HikariDataSource
    val databaseLifecycle: DatabaseLifecycle
    val tables: List<Table>

    @Provides
    fun provideDatabaseConfig(): DatabaseConfig = loadConfig().database

    @AppScope
    @Provides
    fun provideHikariDataSource(dbConfig: DatabaseConfig): HikariDataSource =
        createHikariDataSource(dbConfig)
}
