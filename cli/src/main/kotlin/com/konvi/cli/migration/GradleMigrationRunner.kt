package com.konvi.cli.migration

import com.github.ajalt.clikt.core.CliktError
import java.io.IOException

internal object GradleMigrationRunner {

    fun run(task: String, properties: Map<String, String> = emptyMap()): Int =
        startProcess(task, properties) { inheritIO() }.waitFor()

    fun capture(task: String, properties: Map<String, String> = emptyMap()): String {
        val process = startProcess(task, properties) { this }

        val output = StringBuilder()
        val errorOutput = StringBuilder()
        val outputThread = Thread { process.inputStream.bufferedReader().forEachLine { output.appendLine(it) } }
        val errorThread = Thread { process.errorStream.bufferedReader().forEachLine { errorOutput.appendLine(it) } }
        outputThread.start()
        errorThread.start()

        val exitCode = process.waitFor()
        outputThread.join()
        errorThread.join()

        if (exitCode != 0) {
            throw CliktError("Gradle task '$task' failed (exit code $exitCode)\n$errorOutput")
        }

        return output.toString()
    }

    private fun startProcess(
        task: String,
        properties: Map<String, String>,
        configure: ProcessBuilder.() -> ProcessBuilder,
    ): Process {
        val builder = ProcessBuilder(command(task, properties)).configure()
        return try {
            builder.start()
        } catch (e: IOException) {
            throw CliktError(
                "Could not run 'gradle $task' — is Gradle installed and on your PATH? (${e.message})"
            )
        }
    }

    private fun command(task: String, properties: Map<String, String>): List<String> =
        listOf("gradle", task) + properties.map { (key, value) -> "-P$key=$value" }
}
