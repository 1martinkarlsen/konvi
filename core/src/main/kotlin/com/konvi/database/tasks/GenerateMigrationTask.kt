package com.konvi.database.tasks

import com.konvi.database.generateMigration

private const val SQL_STATEMENT_PREFIX = "SQL::"

fun main() {
    generateMigration().forEach { println("$SQL_STATEMENT_PREFIX$it;") }
}
