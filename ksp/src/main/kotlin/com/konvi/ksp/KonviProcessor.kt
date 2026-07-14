package com.konvi.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.io.BufferedWriter

private const val ROUTE_ANNOTATION = "com.konvi.routing.Route"
private const val MIDDLEWARE_ANNOTATION = "com.konvi.routing.Middleware"
private const val AUTHENTICATOR_ANNOTATION = "com.konvi.auth.Authenticator"
private const val LIFECYCLE_INTERFACE = "com.konvi.lifecycle.Lifecycle"
private const val TABLE_INTERFACE = "org.jetbrains.exposed.v1.core.Table"
private const val GENERATED_PACKAGE = "com.konvi.generated"
private const val GENERATED_FILE = "AppComponent"
private const val GENERATED_ROUTE_FILE = "RouteScope"
private const val GENERATED_DATABASE_FILE = "DatabaseScope"

private val AUTHENTICATION_SCHEMES = listOf(
    AuthScheme(
        interfaceFqn = "com.konvi.auth.basic.BasicAuthenticator",
        denyAllFqn = "com.konvi.auth.basic.DenyAllBasicAuthenticator",
        provideFunction = "provideBasicAuthenticator"
    ),
    AuthScheme(
        interfaceFqn = "com.konvi.auth.jwt.JwtAuthenticator",
        denyAllFqn = "com.konvi.auth.jwt.DenyAllJwtAuthenticator",
        provideFunction = "provideJwtAuthenticator"
    )
)

class KonviProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    // The component is an aggregating output built from all annotated classes, which are all
    // resolvable in the first round. Generating again in later KSP rounds would try to recreate
    // AppComponent.kt and fail with FileAlreadyExistsException, so generate exactly once. The flag
    // is per-build (a fresh processor is created each compilation), so edits still regenerate.
    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()
        invoked = true

        val routes = resolver.classesAnnotatedWith(ROUTE_ANNOTATION)
        val middlewares = resolver.classesAnnotatedWith(MIDDLEWARE_ANNOTATION)
        val authenticators = resolver.classesAnnotatedWith(AUTHENTICATOR_ANNOTATION)
        val lifecycleHooks = resolver.classesWithInterface(LIFECYCLE_INTERFACE)
        val tables = resolver.objectsInheriting(TABLE_INTERFACE)

        // Resolve each @Authenticator to the scheme it implements (at most one impl per scheme).
        val implByScheme = mutableMapOf<AuthScheme, KSClassDeclaration>()
        for (authenticator in authenticators) {
            val scheme = AUTHENTICATION_SCHEMES.firstOrNull { authenticator.implements(it.interfaceFqn) }
            if (scheme == null) {
                logger.error(
                    "@Authenticator ${authenticator.fqn()} must implement one of: " +
                            AUTHENTICATION_SCHEMES.joinToString { it.interfaceName },
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

        generateRouteComponent(
            routes = routes,
            middlewares = middlewares
        )

        generateDatabaseScope(
            tables = tables
        )

        generateComponent(
            lifecycleHooks = lifecycleHooks,
            implByScheme = implByScheme
        )

        return emptyList()
    }

    private fun generateComponent(
        lifecycleHooks: List<KSClassDeclaration>,
        implByScheme: Map<AuthScheme, KSClassDeclaration>
    ) {
        val exposedClasses = implByScheme.values + lifecycleHooks
        generateFile(GENERATED_FILE, exposedClasses) { writer ->
            writer.appendLine("import me.tatarka.inject.annotations.Component")
            writer.appendLine("import me.tatarka.inject.annotations.Provides")
            writer.appendLine("import com.konvi.di.KonviComponent")
            writer.appendLine("import com.konvi.routing.KonviRoutingBuilder")
            writer.appendLine("import $LIFECYCLE_INTERFACE")
            AUTHENTICATION_SCHEMES.forEach { scheme ->
                writer.appendLine("import ${scheme.interfaceFqn}")
                if (implByScheme[scheme] == null) writer.appendLine("import ${scheme.denyAllFqn}")
            }
            writer.writeImports(exposedClasses)
            writer.appendLine()
            writer.appendLine("@Component")
            writer.appendLine("abstract class $GENERATED_FILE : KonviComponent(), ")
            writer.appendLine("    $GENERATED_ROUTE_FILE, $GENERATED_DATABASE_FILE {")

            provideLifecycle(
                writer = writer,
                lifecycleHooks = lifecycleHooks,
            )

            provideAuthenticators(
                writer = writer,
                implByScheme = implByScheme,
            )

            writer.appendLine("}")
        }
    }

    private fun generateRouteComponent(
        routes: List<KSClassDeclaration>,
        middlewares: List<KSClassDeclaration>
    ) {
        val exposedClasses = routes + middlewares
        generateFile(GENERATED_ROUTE_FILE, exposedClasses) { writer ->
            writer.appendLine("import com.konvi.di.RouteContext")
            writer.writeImports(exposedClasses)
            writer.appendLine()
            writer.appendLine("interface $GENERATED_ROUTE_FILE : RouteContext {")
            exposedClasses.forEach {
                val name = it.simpleName.asString()
                writer.appendLine("    val ${name.replaceFirstChar { c -> c.lowercase() }}: $name")
            }

            writer.appendLine("}")
        }
    }

    private fun generateDatabaseScope(
        tables: List<KSClassDeclaration>
    ) {
        generateFile(GENERATED_DATABASE_FILE, emptyList()) { writer ->
            writer.appendLine("import com.konvi.di.DatabaseContext")
            writer.appendLine("import me.tatarka.inject.annotations.Provides")
            writer.appendLine("import $TABLE_INTERFACE")
            writer.writeImports(tables)
            writer.appendLine()
            writer.appendLine("interface $GENERATED_DATABASE_FILE : DatabaseContext {")
            writer.appendLine()
            writer.appendLine("    @Provides")
            writer.appendLine("    fun providesDbTables(): List<Table> =")
            writer.appendLine("        listOf(")
            tables.forEach { table ->
                writer.appendLine("            ${table.simpleName.asString()}")
            }
            writer.appendLine("        )")

            writer.appendLine("}")
        }
    }

    // Shared scaffold for a generated file: opens the writer with an aggregating dependency on
    // every source file the exposed classes came from, and writes the package header.
    private fun generateFile(
        fileName: String,
        exposedClasses: Collection<KSClassDeclaration>,
        content: (BufferedWriter) -> Unit
    ) {
        val sourceFiles = exposedClasses
            .mapNotNull { it.containingFile }
            .distinct()
            .toTypedArray()

        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true, sources = sourceFiles),
            packageName = GENERATED_PACKAGE,
            fileName = fileName
        ).bufferedWriter().use { writer ->
            writer.appendLine("package $GENERATED_PACKAGE")
            writer.appendLine()
            content(writer)
        }
    }

    // Collects every concrete Lifecycle implementation into the single List<Lifecycle> the
    // KonviComponent exposes. Emits emptyList() when there are no hooks so apps without any
    // still satisfy the binding.
    private fun provideLifecycle(
        writer: BufferedWriter,
        lifecycleHooks: List<KSClassDeclaration>
    ) {
        writer.appendLine()
        writer.appendLine("    @Provides")
        if (lifecycleHooks.isEmpty()) {
            writer.appendLine("    fun provideLifecycle(): List<Lifecycle> = emptyList()")
            return
        }
        val params = lifecycleHooks
            .mapIndexed { index, hook -> "p$index: ${hook.simpleName.asString()}" }
            .joinToString()
        val args = lifecycleHooks.indices.joinToString { "p$it" }
        writer.appendLine("    fun provideLifecycle($params): List<Lifecycle> = listOf($args)")
    }

    private fun provideAuthenticators(
        writer: BufferedWriter,
        implByScheme: Map<AuthScheme, KSClassDeclaration>
    ) {
        AUTHENTICATION_SCHEMES.forEach { scheme ->
            writer.appendLine()
            writer.appendLine("    @Provides")
            val impl = implByScheme[scheme]
            if (impl != null) {
                writer.appendLine(
                    "    fun ${scheme.provideFunction}(impl: ${impl.simpleName.asString()}): " +
                            "${scheme.interfaceName} = impl"
                )
            } else {
                writer.appendLine(
                    "    fun " +
                            "${scheme.provideFunction}(): " +
                            "${scheme.interfaceName} = " +
                            "${scheme.denyAllName}"
                )
            }
        }
    }

}
