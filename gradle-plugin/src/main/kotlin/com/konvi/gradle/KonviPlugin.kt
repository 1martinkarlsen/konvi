package com.konvi.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

private const val KONVI_VERSION = "0.1.0-SNAPSHOT"
private const val KOTLIN_INJECT_VERSION = "0.9.0"

class KonviPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("com.google.devtools.ksp")
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        project.dependencies.apply {
            add("ksp", "com.konvi:ksp:$KONVI_VERSION")
            add("ksp", "me.tatarka.inject:kotlin-inject-compiler-ksp:$KOTLIN_INJECT_VERSION")
        }
    }
}
