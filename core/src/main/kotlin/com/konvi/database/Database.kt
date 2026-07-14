package com.konvi.database

import com.konvi.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database

private const val POOL_SIZE = 10

/**
 * Creates a HikariDataSource from the database configuration
 */
fun createHikariDataSource(dbConfig: DatabaseConfig): HikariDataSource {
    val hikari = HikariConfig().apply {
        jdbcUrl = dbConfig.url
        driverClassName = dbConfig.driver
        username = dbConfig.username
        password = dbConfig.password
        maximumPoolSize = POOL_SIZE
    }
    return HikariDataSource(hikari)
}

/**
 * Configures the database connection using the provided HikariDataSource
 */
internal fun Application.configureDatabase(dataSource: HikariDataSource) {
    Database.connect(dataSource)
}

/**
 * Closes the database connection pool
 */
fun HikariDataSource.closeConnection() {
    this.close()
}
