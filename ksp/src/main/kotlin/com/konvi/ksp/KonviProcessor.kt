package com.konvi.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

private const val ROUTE = "com.konvi.route.Route"
private const val MIDDLEWARE = "com.konvi.route.Middleware"
private const val GENERATED_PACKAGE = "com.konvi.generated"
private const val GENERATED_FILE = "Routes"

class KonviProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val routes = resolver
            .getSymbolsWithAnnotation(ROUTE)
            .filterIsInstance<KSClassDeclaration>()
            .toList()

        val middlewares = resolver
            .getSymbolsWithAnnotation(MIDDLEWARE)
            .filterIsInstance<KSClassDeclaration>()
            .toList()

        if (routes.isNotEmpty() || middlewares.isNotEmpty()) {
            generateComponent(routes, middlewares)
        }

        return emptyList()
    }

    private fun generateComponent(routes: List<KSClassDeclaration>, middlewares: List<KSClassDeclaration>) {
        val allClasses = routes + middlewares
        val sourceFiles = allClasses.mapNotNull { it.containingFile }.toTypedArray()

        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true, *sourceFiles),
            packageName = GENERATED_PACKAGE,
            fileName = GENERATED_FILE
        ).bufferedWriter().use { writer ->
            writer.appendLine("package $GENERATED_PACKAGE")
            writer.appendLine()
            writer.appendLine("import me.tatarka.inject.annotations.Component")
            writer.appendLine("import com.konvi.di.KonviComponent")
            writer.appendLine("import com.konvi.Konvi")
            writer.appendLine("import com.konvi.route.KonviRouter")
            allClasses.forEach {
                writer.appendLine("import ${it.qualifiedName!!.asString()}")
            }
            writer.appendLine()
            writer.appendLine("@Component")
            writer.appendLine("abstract class Routes : KonviComponent() {")
            allClasses.forEach {
                val name = it.simpleName.asString()
                writer.appendLine("    abstract val ${name.replaceFirstChar { c -> c.lowercase() }}: $name")
            }
            writer.appendLine("}")
            writer.appendLine()
            writer.appendLine("fun konviStart(routes: Routes.() -> KonviRouter) =")
            writer.appendLine("    Konvi.start(Routes::class.create(), routes)")
        }
    }
}
