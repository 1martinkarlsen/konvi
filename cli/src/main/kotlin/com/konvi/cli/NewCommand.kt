package com.konvi.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import kotlin.io.path.Path
import kotlin.io.path.exists

internal class NewCommand constructor(
    private val renderer: TemplateRenderer = TemplateRenderer()
) : CliktCommand(name = "new", help = "Scaffold a new Konvi project") {

    private val name by argument(help = "Name of the project to create, e.g. my-app")

    private val packageName by option(
        "--package", "-p",
        help = "Base package for the project (default: com.<name>.<name>)",
    )

    override fun run() {
        val targetDir = Path(name)
        if (targetDir.exists()) {
            throw CliktError("Directory '$name' already exists.")
        }

        val slug = name.lowercase().filter { it.isLetterOrDigit() }
        val basePackage = packageName ?: "com.$slug.$slug"

        renderer.render(
            variables = mapOf(
                "projectName" to name,
                "basePackage" to basePackage,
                "packagePath" to basePackage.replace('.', '/'),
            ),
            targetDir = targetDir,
        )
    }
}
