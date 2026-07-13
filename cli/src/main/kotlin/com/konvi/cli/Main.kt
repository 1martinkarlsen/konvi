package com.konvi.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.konvi.cli.migration.BaselineMigrationCommand
import com.konvi.cli.migration.CreateMigrationCommand
import com.konvi.cli.migration.MigrationCommand
import com.konvi.cli.migration.RepairMigrationCommand
import com.konvi.cli.migration.RunMigrationCommand
import com.konvi.cli.migration.StatusMigrationCommand

fun main(args: Array<String>) = Konvi()
    .subcommands(
        NewCommand(),
        MigrationCommand().subcommands(
            CreateMigrationCommand(),
            RunMigrationCommand(),
            StatusMigrationCommand(),
            RepairMigrationCommand(),
            BaselineMigrationCommand(),
        ),
    )
    .main(args)

internal class Konvi : CliktCommand(name = "konvi") {
    override fun run() = Unit
}
