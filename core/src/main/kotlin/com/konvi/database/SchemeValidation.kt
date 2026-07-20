package com.konvi.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

@Suppress("SpreadOperator")
internal fun validateDatabaseScheme(
    tables: List<Table>
) {
    val statements = transaction {
        MigrationUtils.statementsRequiredForDatabaseMigration(*tables.toTypedArray())
    }
    if (statements.isNotEmpty()) {
        error(
            "Database schema does not match the declared tables. " +
                    "Run pending migrations:\n${statements.joinToString("\n")}"
        )
    }
}
