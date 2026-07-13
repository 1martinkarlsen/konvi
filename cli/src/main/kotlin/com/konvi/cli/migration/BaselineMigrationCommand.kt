package com.konvi.cli.migration

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.options.option
import kotlin.io.path.Path
import kotlin.io.path.exists

private const val MIGRATION_DIR = "src/main/resources/db/migration"
private val VERSION_REGEX = Regex("""^V(\d+)__.+\.sql$""")

internal class BaselineMigrationCommand :
    CliktCommand(name = "baseline", help = "Marks an existing database as the starting point for future migrations") {

    private val migrationFile by option(
        "--migration",
        help = "Filename of the migration to baseline at, e.g. V20260706120000__snapshot.sql",
    )

    override fun run() {
        val properties = mutableMapOf<String, String>()

        migrationFile?.let { fileName ->
            if (!Path(MIGRATION_DIR).resolve(fileName).exists()) {
                throw CliktError("Migration file '$fileName' not found in $MIGRATION_DIR")
            }

            val version = VERSION_REGEX.find(fileName)?.groupValues?.get(1)
                ?: throw CliktError(
                    "Could not parse a version from '$fileName'; expected format V<version>__<description>.sql"
                )

            properties["baselineVersion"] = version
        }

        val exitCode = GradleMigrationRunner.run("migrationBaseline", properties)
        if (exitCode != 0) {
            throw CliktError("Baseline failed (exit code $exitCode)")
        }
    }
}
