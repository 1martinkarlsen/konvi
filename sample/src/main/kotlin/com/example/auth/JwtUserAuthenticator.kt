package com.example.auth

import com.example.repositories.UserRepository
import com.konvi.auth.JwtAuthenticator
import com.konvi.auth.JwtClaims
import com.konvi.routing.Authenticator
import me.tatarka.inject.annotations.Inject

@Authenticator
class JwtUserAuthenticator @Inject constructor(
    private val users: UserRepository
) : JwtAuthenticator {
    override suspend fun authenticate(claims: JwtClaims): Any? =
        claims.subject?.toIntOrNull()?.let { users.find(it) }
}
