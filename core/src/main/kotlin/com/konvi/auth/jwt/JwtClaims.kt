package com.konvi.auth.jwt

import com.auth0.jwt.interfaces.Payload

/**
 * The verified claims of a JWT, handed to a [JwtAuthenticator]. Wraps the underlying decoded
 * token so application code does not depend on the JWT library directly.
 */
class JwtClaims internal constructor(private val payload: Payload) {
    /** The `sub` claim, typically the user id. */
    val subject: String? get() = payload.subject

    /** A string claim by [name], or null if absent. */
    fun string(name: String): String? = payload.getClaim(name).asString()

    /** An int claim by [name], or null if absent. */
    fun int(name: String): Int? = payload.getClaim(name).asInt()

    /** A long claim by [name], or null if absent. */
    fun long(name: String): Long? = payload.getClaim(name).asLong()

    /** A boolean claim by [name], or null if absent. */
    fun boolean(name: String): Boolean? = payload.getClaim(name).asBoolean()
}
