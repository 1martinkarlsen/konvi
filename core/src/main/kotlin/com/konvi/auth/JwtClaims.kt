package com.konvi.auth

import com.auth0.jwt.interfaces.DecodedJWT

/**
 * The verified claims of a JWT, handed to a [JwtAuthenticator]. Wraps the underlying decoded
 * token so application code does not depend on the JWT library directly.
 */
class JwtClaims internal constructor(private val jwt: DecodedJWT) {
    /** The `sub` claim, typically the user id. */
    val subject: String? get() = jwt.subject

    /** A string claim by [name], or null if absent. */
    fun string(name: String): String? = jwt.getClaim(name).asString()

    /** An int claim by [name], or null if absent. */
    fun int(name: String): Int? = jwt.getClaim(name).asInt()

    /** A long claim by [name], or null if absent. */
    fun long(name: String): Long? = jwt.getClaim(name).asLong()

    /** A boolean claim by [name], or null if absent. */
    fun boolean(name: String): Boolean? = jwt.getClaim(name).asBoolean()
}
