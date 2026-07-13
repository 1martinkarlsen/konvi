package com.konvi.cli.migration

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.writeText

private const val MIGRATION_DIR = "src/main/resources/db/migration"
private const val SQL_STATEMENT_PREFIX = "SQL::"
private val VERSION_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

internal class CreateMigrationCommand : CliktCommand(name = "create", help = "Create a new migration file") {

    private val description by argument(help = "Short description of the migration, e.g. add_users_table")

    private val generate by option(
        "--generate",
        help = "[Experimental] Auto-populate the migration by diffing all tables against the database",
    ).flag(default = false)

    override fun run() {
        val targetDir = Path(MIGRATION_DIR)
        targetDir.createDirectories()

        val version = LocalDateTime.now().format(VERSION_FORMAT)
        val fileName = "V${version}__${sanitize(description)}.sql"
        val targetFile = targetDir.resolve(fileName)

        if (targetFile.exists()) {
            throw CliktError("Migration file '$fileName' already exists.")
        }

        targetFile.writeText(if (generate) generatedSql() else "")
        echo("Created $targetFile")
    }

    private fun generatedSql(): String =
        GradleMigrationRunner.capture("generateMigration")
            .lineSequence()
            .filter { it.startsWith(SQL_STATEMENT_PREFIX) }
            .joinToString(System.lineSeparator()) { it.removePrefix(SQL_STATEMENT_PREFIX) }

    private fun sanitize(input: String): String =
        input.trim().lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
}
