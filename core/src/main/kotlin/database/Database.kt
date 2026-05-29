package com.konvi.database

import com.konvi.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database

internal fun Application.configureDatabase(dbConfig: DatabaseConfig) {
    val hikari = HikariConfig().apply {
        jdbcUrl = dbConfig.url
        driverClassName = dbConfig.driver
        username = dbConfig.username
        password = dbConfig.password
        maximumPoolSize = 10
    }
    Database.connect(HikariDataSource(hikari))
}
