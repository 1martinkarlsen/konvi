package com.konvi.ksp

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

private const val ROUTE = "com.konvi.routing.Route"
private const val MIDDLEWARE = "com.konvi.routing.Middleware"
private const val AUTHENTICATOR = "com.konvi.routing.Authenticator"
private const val GENERATED_PACKAGE = "com.konvi.generated"
private const val GENERATED_FILE = "Routes"

/**
 * An authentication scheme that can be plugged in via [AUTHENTICATOR]. To add a new scheme,
 * add an entry here and a matching middleware method on AuthMiddleware.
 */
private data class Scheme(
    val interfaceFqn: String,
    val denyAllFqn: String,
    val provideFunction: String
) {
    val interfaceName get() = interfaceFqn.substringAfterLast('.')
    val denyAllName get() = denyAllFqn.substringAfterLast('.')
}

private val SCHEMES = listOf(
    Scheme("com.konvi.auth.BasicAuthenticator", "com.konvi.auth.DenyAllAuthenticator", "provideBasicAuthenticator"),
    Scheme("com.konvi.auth.JwtAuthenticator", "com.konvi.auth.DenyAllJwtAuthenticator", "provideJwtAuthenticator")
)

class KonviProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val routes = resolver.classesAnnotatedWith(ROUTE)
        val middlewares = resolver.classesAnnotatedWith(MIDDLEWARE)
        val authenticators = resolver.classesAnnotatedWith(AUTHENTICATOR)

        // Resolve each @Authenticator to the scheme it implements (at most one impl per scheme).
        val implByScheme = mutableMapOf<Scheme, KSClassDeclaration>()
        for (authenticator in authenticators) {
            val scheme = SCHEMES.firstOrNull { authenticator.implements(it.interfaceFqn) }
            if (scheme == null) {
                logger.error(
                    "@Authenticator ${authenticator.fqn()} must implement one of: " +
                        SCHEMES.joinToString { it.interfaceName },
                    authenticator
                )
                continue
            }
            val existing = implByScheme.put(scheme, authenticator)
            if (existing != null) {
                logger.error(
                    "Multiple @Authenticator classes implement ${scheme.interfaceName}: " +
                        "${existing.fqn()} and ${authenticator.fqn()}; only one is supported",
                    authenticator
                )
            }
        }

        if (routes.isNotEmpty() || middlewares.isNotEmpty() || authenticators.isNotEmpty()) {
            generateComponent(routes, middlewares, implByScheme)
        }

        return emptyList()
    }

    private fun generateComponent(
        routes: List<KSClassDeclaration>,
        middlewares: List<KSClassDeclaration>,
        implByScheme: Map<Scheme, KSClassDeclaration>
    ) {
        val exposedClasses = routes + middlewares
        val sourceFiles = (exposedClasses + implByScheme.values)
            .mapNotNull { it.containingFile }
            .distinct()
            .toTypedArray()

        @Suppress("SpreadOperator")
        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true, sources = sourceFiles),
            packageName = GENERATED_PACKAGE,
            fileName = GENERATED_FILE
        ).bufferedWriter().use { writer ->
            writer.appendLine("package $GENERATED_PACKAGE")
            writer.appendLine()
            writer.appendLine("import me.tatarka.inject.annotations.Component")
            writer.appendLine("import me.tatarka.inject.annotations.Provides")
            writer.appendLine("import com.konvi.di.KonviComponent")
            writer.appendLine("import com.konvi.Konvi")
            writer.appendLine("import com.konvi.routing.KonviRouter")
            SCHEMES.forEach { scheme ->
                writer.appendLine("import ${scheme.interfaceFqn}")
                if (implByScheme[scheme] == null) writer.appendLine("import ${scheme.denyAllFqn}")
            }
            (exposedClasses + implByScheme.values).forEach {
                writer.appendLine("import ${it.qualifiedName!!.asString()}")
            }
            writer.appendLine()
            writer.appendLine("@Component")
            writer.appendLine("abstract class Routes : KonviComponent() {")
            exposedClasses.forEach {
                val name = it.simpleName.asString()
                writer.appendLine("    abstract val ${name.replaceFirstChar { c -> c.lowercase() }}: $name")
            }
            SCHEMES.forEach { scheme ->
                writer.appendLine()
                writer.appendLine("    @Provides")
                val impl = implByScheme[scheme]
                if (impl != null) {
                    writer.appendLine(
                        "    fun ${scheme.provideFunction}(impl: ${impl.simpleName.asString()}): " +
                            "${scheme.interfaceName} = impl"
                    )
                } else {
                    writer.appendLine("    fun " +
                            "${scheme.provideFunction}(): " +
                            "${scheme.interfaceName} = " +
                            "${scheme.denyAllName}")
                }
            }
            writer.appendLine("}")
            writer.appendLine()
            writer.appendLine("fun konviStart(routes: Routes.() -> KonviRouter) =")
            writer.appendLine("    Konvi.start(Routes::class.create(), routes)")
        }
    }

    private fun Resolver.classesAnnotatedWith(annotation: String): List<KSClassDeclaration> =
        getSymbolsWithAnnotation(annotation).filterIsInstance<KSClassDeclaration>().toList()

    private fun KSClassDeclaration.implements(interfaceFqn: String): Boolean =
        getAllSuperTypes().any { it.declaration.qualifiedName?.asString() == interfaceFqn }

    private fun KSClassDeclaration.fqn(): String = qualifiedName?.asString() ?: simpleName.asString()
}
