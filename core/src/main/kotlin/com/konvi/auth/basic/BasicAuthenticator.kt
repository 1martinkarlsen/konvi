package com.konvi.auth.basic

/**
 * Validates Basic credentials. Implement this in your application and annotate the
 * implementation with [com.konvi.routing.Authenticator] to enable basic authentication.
 */
fun interface BasicAuthenticator {
    /** Return any object to use as the authenticated principal, or null to reject. */
    suspend fun authenticate(username: String, password: String): Any?
}

/** Default binding used when no [com.konvi.routing.Authenticator] is provided. Rejects everything. */
object DenyAllBasicAuthenticator : BasicAuthenticator {
    override suspend fun authenticate(username: String, password: String): Any? = null
}
