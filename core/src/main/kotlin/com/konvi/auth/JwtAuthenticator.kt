package com.konvi.auth

/**
 * Maps the verified claims of a JWT to an application principal. Implement this and annotate
 * the implementation with [com.konvi.routing.Authenticator] to enable JWT authentication.
 *
 * The token signature, issuer, audience and expiry are already verified before this is called;
 * here you only decide which principal (if any) the claims represent.
 */
fun interface JwtAuthenticator {
    /** Return any object to use as the authenticated principal, or null to reject. */
    suspend fun authenticate(claims: JwtClaims): Any?
}

/** Default binding used when no JWT [com.konvi.routing.Authenticator] is provided. Rejects everything. */
object DenyAllJwtAuthenticator : JwtAuthenticator {
    override suspend fun authenticate(claims: JwtClaims): Any? = null
}
