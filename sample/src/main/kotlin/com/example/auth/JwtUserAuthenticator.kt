package com.example.auth

import com.example.repositories.UserRepository
import com.konvi.auth.jwt.JwtAuthenticator
import com.konvi.auth.jwt.JwtClaims
import com.konvi.auth.Authenticator
import me.tatarka.inject.annotations.Inject

@Authenticator
class JwtUserAuthenticator @Inject constructor(
    private val users: UserRepository
) : JwtAuthenticator {
    override suspend fun authenticate(claims: JwtClaims): Any? =
        claims.subject?.toIntOrNull()?.let { users.find(it) }
}
