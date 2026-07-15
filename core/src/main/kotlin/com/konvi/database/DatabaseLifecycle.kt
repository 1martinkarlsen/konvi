package com.konvi.database

import com.konvi.lifecycle.Lifecycle
import com.zaxxer.hikari.HikariDataSource
import me.tatarka.inject.annotations.Inject
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Lifecycle hook that validates the declared tables against the live database schema on startup,
 * and closes the database connection when the application stops.
 */
@Inject
class DatabaseLifecycle(
    private val hikariDataSource: HikariDataSource,
    private val tables: List<Table>
) : Lifecycle {
    @Suppress("SpreadOperator")
    override suspend fun onStart() {
        validateDatabaseScheme(tables)
    }

    override suspend fun onStop() {
        hikariDataSource.closeConnection()
    }
}
