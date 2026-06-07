package com.konvi.ksp

/**
 * AuthScheme represents the interface used for authentication in a scheme (basic, jwt etc).
 */
internal data class AuthScheme(
    val interfaceFqn: String,
    val denyAllFqn: String,
    val provideFunction: String
) {
    val interfaceName get() = interfaceFqn.substringAfterLast('.')
    val denyAllName get() = denyAllFqn.substringAfterLast('.')
}
