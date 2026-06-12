package com.konvi.cli

import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.outputStream
import kotlin.io.path.writeText

class TemplateRenderer {

    fun render(variables: Map<String, String>, targetDir: Path) {
        for (entry in readIndex()) {
            val outputPath = substitute(entry.removeSuffix(TEMPLATE_SUFFIX), variables)
            val outputFile = targetDir.resolve(outputPath)
            outputFile.parent?.createDirectories()

            if (entry.endsWith(TEMPLATE_SUFFIX)) {
                outputFile.writeText(substitute(readText(entry), variables))
            } else {
                copyResource(entry, outputFile)
            }
        }
    }

    private fun copyResource(relativePath: String, target: Path) {
        openResource(relativePath).use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        }
    }

    private fun readIndex(): List<String> =
        readText("index.txt")
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toList()

    private fun readText(relativePath: String): String =
        openResource(relativePath).bufferedReader().use { it.readText() }

    private fun openResource(relativePath: String): InputStream =
        javaClass.getResourceAsStream("/$TEMPLATES_ROOT/$relativePath")
            ?: error("Template resource not found: /$TEMPLATES_ROOT/$relativePath")

    private fun substitute(text: String, variables: Map<String, String>): String =
        variables.entries.fold(text) { acc, (key, value) -> acc.replace("<$key>", value) }

    companion object {
        private const val TEMPLATES_ROOT = "templates"
        private const val TEMPLATE_SUFFIX = ".template"
    }
}
