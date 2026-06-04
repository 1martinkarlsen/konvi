package com.example.repositories

import com.example.models.User
import me.tatarka.inject.annotations.Inject

class UserRepository @Inject constructor() {
    private val users = listOf(
        User(id = 1, name = "Alice"),
        User(id = 2, name = "Bob"),
        User(id = 3, name = "Charlie")
    )

    fun findAll(): List<User> = users
    fun find(id: Int): User? = users.find { it.id == id }

    // Demo credential check: any known user with the password "secret".
    fun findByCredentials(name: String, password: String): User? =
        users.find { it.name.equals(name, ignoreCase = true) }?.takeIf { password == "secret" }
}
