package com.example.auth

import com.example.repositories.UserRepository
import com.konvi.auth.basic.BasicAuthenticator
import com.konvi.auth.Authenticator
import me.tatarka.inject.annotations.Inject

@Authenticator
class CredentialAuthenticator @Inject constructor(
    private val users: UserRepository
) : BasicAuthenticator {
    override suspend fun authenticate(username: String, password: String): Any? =
        users.findByCredentials(username, password)
}
