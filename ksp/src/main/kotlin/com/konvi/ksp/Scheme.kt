package com.konvi.ksp

/**
 * An authentication scheme that can be plugged in via [AUTHENTICATOR]. To add a new scheme,
 * add an entry here and a matching middleware method on AuthMiddleware.
 */
internal data class Scheme(
    val interfaceFqn: String,
    val denyAllFqn: String,
    val provideFunction: String
) {
    val interfaceName get() = interfaceFqn.substringAfterLast('.')
    val denyAllName get() = denyAllFqn.substringAfterLast('.')
}