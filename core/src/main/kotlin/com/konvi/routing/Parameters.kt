package com.konvi.routing

import com.konvi.exception.BadRequestException
import io.ktor.http.Parameters
import io.ktor.server.routing.RoutingCall
import java.util.UUID
import kotlin.reflect.typeOf
import kotlin.text.toBooleanStrictOrNull
import kotlin.text.toDoubleOrNull
import kotlin.text.toIntOrNull
import kotlin.text.toLongOrNull

@PublishedApi
internal inline fun <reified T> RoutingCall.getPathParameter(parameterName: String): T {
    val type = typeOf<T>()
    val raw = pathParameters[parameterName]

    if (raw == null) {
        // We should handle calling pathParameter<Int> and pathParameter<Int?> differently
        if (type.isMarkedNullable) return null as T

        throw IllegalArgumentException("Missing path parameter: $parameterName")
    }

    return getParameter<T>(raw, parameterName)
}

@PublishedApi
internal inline fun <reified T> RoutingCall.getQueryParameter(parameterName: String): T {
    val type = typeOf<T>()
    val raw = queryParameters[parameterName]

    if (raw == null) {
        // We should handle calling pathParameter<Int> and pathParameter<Int?> differently
        if (type.isMarkedNullable) return null as T

        throw BadRequestException("Missing query parameter: $parameterName")
    }

    return getParameter<T>(raw, parameterName)
}

@PublishedApi
internal inline fun <reified T> RoutingCall.getParameter(raw: String, parameterName: String): T {
    val converted = when (T::class) {
        String::class -> raw
        Int::class -> raw.toIntOrNull()
        Long::class -> raw.toLongOrNull()
        Double::class -> raw.toDoubleOrNull()
        Boolean::class -> raw.toBooleanStrictOrNull()
        UUID::class -> runCatching { UUID.fromString(raw) }.getOrNull()
        else -> throw IllegalArgumentException("Unsupported parameter type: ${T::class}")
    }

    if (converted == null) {
        throw BadRequestException(
            "Parameter '$parameterName' is not a valid ${T::class.simpleName}: '$raw'"
        )
    }

    return converted as T
}
