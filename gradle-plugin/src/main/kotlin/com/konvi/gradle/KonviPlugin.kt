package com.konvi.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer

private const val KONVI_VERSION = "0.1.0-SNAPSHOT"
private const val KOTLIN_INJECT_VERSION = "0.9.0"
private const val MIGRATE_RUN_TASK = "migrateDatabase"
private const val MIGRATE_STATUS_TASK = "migrationStatus"
private const val MIGRATE_REPAIR_TASK = "migrationRepair"
private const val MIGRATE_BASELINE_TASK = "migrationBaseline"
private const val GENERATE_MIGRATION_TASK = "generateMigration"

class KonviPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("com.google.devtools.ksp")
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        project.dependencies.apply {
            add("ksp", "com.konvi:ksp:$KONVI_VERSION")
            add("ksp", "me.tatarka.inject:kotlin-inject-compiler-ksp:$KOTLIN_INJECT_VERSION")
        }

        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        val runtimeClasspath = sourceSets.getByName("main").runtimeClasspath

        project.tasks.register(MIGRATE_RUN_TASK, JavaExec::class.java) {
            group = "konvi"
            description = "Runs pending database migrations"
            classpath = runtimeClasspath
            mainClass.set("com.konvi.database.tasks.MigrateDatabaseTaskKt")
        }

        project.tasks.register(MIGRATE_STATUS_TASK, JavaExec::class.java) {
            group = "konvi"
            description = "Check status of pending database migrations"
            classpath = runtimeClasspath
            mainClass.set("com.konvi.database.tasks.MigrationStatusTaskKt")
        }

        project.tasks.register(MIGRATE_REPAIR_TASK, JavaExec::class.java) {
            group = "konvi"
            description = "Clears a failed migration entry so a corrected retry can run"
            classpath = runtimeClasspath
            mainClass.set("com.konvi.database.tasks.MigrationRepairTaskKt")
        }

        project.tasks.register(MIGRATE_BASELINE_TASK, JavaExec::class.java) {
            group = "konvi"
            description = "Marks the database baseline"
            classpath = runtimeClasspath
            mainClass.set("com.konvi.database.tasks.MigrationBaselineTaskKt")
            project.findProperty("baselineVersion")?.let { args = listOf(it.toString()) }
        }

        project.tasks.register(GENERATE_MIGRATION_TASK, JavaExec::class.java) {
            group = "konvi"
            description = "Generates migration SQL by diffing discovered tables against the database"
            classpath = runtimeClasspath
            mainClass.set("com.konvi.database.tasks.GenerateMigrationTaskKt")
        }
    }
}
