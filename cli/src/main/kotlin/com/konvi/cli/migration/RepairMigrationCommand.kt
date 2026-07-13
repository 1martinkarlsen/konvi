package com.konvi.cli.migration

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError

internal class RepairMigrationCommand :
    CliktCommand(name = "repair", help = "Clears a failed migration entry so a corrected retry can run") {

    override fun run() {
        val exitCode = GradleMigrationRunner.run("migrationRepair")
        if (exitCode != 0) {
            throw CliktError("Repair failed (exit code $exitCode)")
        }
    }
}
