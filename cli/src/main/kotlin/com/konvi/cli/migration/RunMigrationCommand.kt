package com.konvi.cli.migration

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError

internal class RunMigrationCommand : CliktCommand(name = "run", help = "Run pending database migrations") {

    override fun run() {
        val exitCode = GradleMigrationRunner.run("migrateDatabase")
        if (exitCode != 0) {
            throw CliktError("Migration failed (exit code $exitCode)")
        }
    }
}
