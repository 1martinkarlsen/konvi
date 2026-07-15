package com.konvi.database

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

internal fun validateDatabaseScheme(
    tables: List<Table>
) {
    val statements = transaction {
        SchemaUtils.statementsRequiredToActualizeScheme(*tables.toTypedArray())
    }
    if (statements.isNotEmpty()) {
        error(
            "Database schema does not match the declared tables. " +
                    "Run pending migrations:\n${statements.joinToString("\n")}"
        )
    }
}