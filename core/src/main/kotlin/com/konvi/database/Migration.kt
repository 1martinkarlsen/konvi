package com.konvi.database

import com.konvi.config.loadConfig
import com.konvi.di.DatabaseContext
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationInfo
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

private const val GENERATED_COMPONENT = "com.konvi.generated.InjectAppComponent"

internal fun migrateDatabase() {
    connect().migrate()
}

@Suppress("SpreadOperator")
internal fun generateMigration(): List<String> {
    val dbConfig = loadConfig().database
    Database.connect(
        url = dbConfig.url,
        driver = dbConfig.driver,
        user = dbConfig.username,
        password = dbConfig.password,
    )

    val tables = loadDatabaseContext().tables.toTypedArray()

    return transaction {
        MigrationUtils.statementsRequiredForDatabaseMigration(*tables)
    }
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

private fun loadDatabaseContext(): DatabaseContext =
    try {
        Class.forName(GENERATED_COMPONENT).getDeclaredConstructor().newInstance() as DatabaseContext
    } catch (e: ClassNotFoundException) {
        error("Generated Konvi component '$GENERATED_COMPONENT' was not found. ${e.message}")
    }
