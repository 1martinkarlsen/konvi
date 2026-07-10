package com.konvi.ksp

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.io.BufferedWriter
import kotlin.collections.forEach

internal fun BufferedWriter.writeImports(declarations: Collection<KSClassDeclaration>) {
    declarations.forEach { appendLine("import ${it.qualifiedName!!.asString()}") }
}

internal fun Resolver.classesAnnotatedWith(annotation: String): List<KSClassDeclaration> =
    getSymbolsWithAnnotation(annotation).filterIsInstance<KSClassDeclaration>().toList()

internal fun KSClassDeclaration.implements(interfaceFqn: String): Boolean =
    getAllSuperTypes().any { it.declaration.qualifiedName?.asString() == interfaceFqn }

internal fun KSClassDeclaration.fqn(): String = qualifiedName?.asString() ?: simpleName.asString()

// Discovers concrete classes implementing the given interface (directly or transitively).
// Abstract classes and interfaces are skipped since kotlin-inject can only construct concrete types.
internal fun Resolver.classesWithInterface(interfaceFqn: String): List<KSClassDeclaration> =
    getAllFiles()
        .flatMap { it.declarations }
        .filterIsInstance<KSClassDeclaration>()
        .filter { it.classKind == ClassKind.CLASS && !it.isAbstract() && it.implements(interfaceFqn) }
        .toList()

// Discovers singleton `object` declarations that inherit the given class (directly or transitively).
// Only objects are included: the generated code references each match as a bare value expression,
// which type-checks for a singleton instance but not for a class that still needs constructor args.
internal fun Resolver.objectsInheriting(classFqn: String): List<KSClassDeclaration> =
    getAllFiles()
        .flatMap { it.declarations }
        .filterIsInstance<KSClassDeclaration>()
        .filter { it.classKind == ClassKind.OBJECT && it.implements(classFqn) }
        .toList()
