package com.konvi.database

import com.konvi.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database

private const val POOL_SIZE = 10
internal fun Application.configureDatabase(dbConfig: DatabaseConfig) {
    val hikari = HikariConfig().apply {
        jdbcUrl = dbConfig.url
        driverClassName = dbConfig.driver
        username = dbConfig.username
        password = dbConfig.password
        maximumPoolSize = POOL_SIZE
    }
    Database.connect(HikariDataSource(hikari))
}
