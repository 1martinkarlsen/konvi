package com.konvi.cli.migration

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.TextColors.yellow

private data class MigrationStatusRow(
    val version: String,
    val description: String,
    val installedOn: String,
    val state: String,
)

internal class StatusMigrationCommand : CliktCommand(name = "status", help = "Show applied and pending migrations") {

    private val all by option("--all", help = "Show full migration history, not just pending/failed").flag(default = false)

    override fun run() {
        val output = GradleMigrationRunner.capture("migrationStatus")
        val rows = parse(output).sortedBy { it.version }

        if (rows.isEmpty()) {
            echo("No migrations found.")
            return
        }

        val visibleRows = if (all) rows else rows.filter { isPending(it.state) || isFailed(it.state) }
        if (visibleRows.isEmpty()) {
            echo("No pending or failed migrations — database is up to date.")
            return
        }

        printTable(visibleRows)
    }

    private fun parse(output: String): List<MigrationStatusRow> =
        output.lineSequence()
            .mapNotNull { line ->
                val parts = line.split("\t")
                if (parts.size != 4) return@mapNotNull null
                MigrationStatusRow(version = parts[0], description = parts[1], installedOn = parts[2], state = parts[3])
            }
            .toList()

    private fun isPending(state: String) = state == "PENDING"

    private fun isFailed(state: String) = state.endsWith("FAILED")

    private fun styledState(state: String): String = when {
        isFailed(state) -> red(state)
        isPending(state) -> yellow(state)
        else -> green(state)
    }

    private fun printTable(rows: List<MigrationStatusRow>) {
        val headers = listOf("VERSION", "DESCRIPTION", "INSTALLED ON", "STATE")
        val installedOnCells = rows.map { it.installedOn.ifEmpty { "-" } }

        val versionWidth = (listOf(headers[0]) + rows.map { it.version }).maxOf { it.length }
        val descriptionWidth = (listOf(headers[1]) + rows.map { it.description }).maxOf { it.length }
        val installedOnWidth = (listOf(headers[2]) + installedOnCells).maxOf { it.length }

        val terminal = currentContext.terminal
        terminal.println(
            headers[0].padEnd(versionWidth) + "  " +
                headers[1].padEnd(descriptionWidth) + "  " +
                headers[2].padEnd(installedOnWidth) + "  " +
                headers[3]
        )

        rows.forEachIndexed { index, row ->
            terminal.println(
                row.version.padEnd(versionWidth) + "  " +
                    row.description.padEnd(descriptionWidth) + "  " +
                    installedOnCells[index].padEnd(installedOnWidth) + "  " +
                    styledState(row.state)
            )
        }
    }
}
