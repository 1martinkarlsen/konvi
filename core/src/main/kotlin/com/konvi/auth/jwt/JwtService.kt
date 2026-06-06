package com.konvi.auth.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import com.konvi.config.AuthConfig
import me.tatarka.inject.annotations.Inject
import java.util.Date

private const val MILLIS_PER_SECOND = 1000

/**
 * Issues and verifies HMAC-signed JWTs using `konvi.auth.jwt` configuration.
 * Inject this to mint tokens (e.g. in a login route): `jwtService.issue(subject = user.id.toString())`.
 */
@Inject
class JwtService(authConfig: AuthConfig) {
    private val config = authConfig.jwt
    private val algorithm = Algorithm.HMAC256(config.secret)

    internal val verifier = JWT.require(algorithm)
        .withIssuer(config.issuer)
        .withAudience(config.audience)
        .build()

    /** Creates a signed token for [subject] with optional extra string [claims]. */
    fun issue(subject: String, claims: Map<String, String> = emptyMap()): String {
        val now = System.currentTimeMillis()
        val builder = JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withSubject(subject)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + config.expiresInSeconds * MILLIS_PER_SECOND))
        claims.forEach { (key, value) -> builder.withClaim(key, value) }
        return builder.sign(algorithm)
    }

    /** Verifies signature, issuer, audience and expiry. Returns the decoded token, or null if invalid. */
    internal fun verify(token: String): DecodedJWT? = runCatching { verifier.verify(token) }.getOrNull()
}
