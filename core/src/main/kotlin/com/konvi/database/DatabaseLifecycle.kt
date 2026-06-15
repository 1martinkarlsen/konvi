package com.konvi.database

import com.konvi.lifecycle.Lifecycle
import com.zaxxer.hikari.HikariDataSource
import me.tatarka.inject.annotations.Inject

/**
 * Lifecycle hook that closes the database connection when the application stops.
 * This ensures proper cleanup of the HikariCP connection pool.
 */
@Inject
class DatabaseLifecycle(
    private val hikariDataSource: HikariDataSource
) : Lifecycle {
    override suspend fun onStart() {
        // Database connection is established during application startup
        // No action needed here as configureDatabase() is called during app init
    }

    override suspend fun onStop() {
        hikariDataSource.closeConnection()
    }
}