package com.konvi.cli.migration

import com.github.ajalt.clikt.core.CliktCommand

internal class MigrationCommand : CliktCommand(name = "migration", help = "Manage database migrations") {
    override fun run() = Unit
}
