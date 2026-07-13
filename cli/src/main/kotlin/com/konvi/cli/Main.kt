package com.konvi.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.konvi.cli.migration.CreateMigrationCommand
import com.konvi.cli.migration.MigrationCommand

fun main(args: Array<String>) = Konvi()
    .subcommands(
        NewCommand(),
        MigrationCommand().subcommands(
            CreateMigrationCommand(),
        ),
    )
    .main(args)

internal class Konvi : CliktCommand(name = "konvi") {
    override fun run() = Unit
}
