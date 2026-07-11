package com.konvi.database

import com.konvi.config.loadConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationInfo

internal fun migrateDatabase() {
    connect().migrate()
}

internal fun migrationStatus(): List<MigrationInfo> {
    return connect().info().all().toList()
}

internal fun migrationRepair() {
    connect().repair()
}

internal fun migrationBaseline(migrationFile: String? = null) {
    connect(migrationFile).baseline()
}

private fun connect(baselineVersion: String? = null): Flyway {
    val dbConfig = loadConfig().database

    return Flyway
        .configure()
        .dataSource(
            dbConfig.url,
            dbConfig.username,
            dbConfig.password
        )
        .apply {
            baselineVersion?.let { baselineVersion(it) }
        }
        .load()
}
