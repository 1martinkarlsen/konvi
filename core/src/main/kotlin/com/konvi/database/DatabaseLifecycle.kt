package com.konvi.database

import com.konvi.lifecycle.Lifecycle
import com.zaxxer.hikari.HikariDataSource
import me.tatarka.inject.annotations.Inject
import org.jetbrains.exposed.v1.core.Table

/**
 * Framework-provided [Lifecycle] hook that manages the database connection across the
 * application's start and stop events.
 */
@Inject
class DatabaseLifecycle(
    private val hikariDataSource: HikariDataSource,
    private val tables: List<Table>
) : Lifecycle {
    override suspend fun onStart() {
        validateDatabaseScheme(tables)
    }

    override suspend fun onStop() {
        hikariDataSource.closeConnection()
    }
}
