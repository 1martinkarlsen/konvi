package com.example.repositories

import com.example.models.User
import me.tatarka.inject.annotations.Inject

class UserRepository @Inject constructor() {
    private val users = listOf(
        User(1, "Alice"),
        User(2, "Bob"),
        User(3, "Charlie")
    )

    fun findAll(): List<User> = users
    fun find(id: Int): User? = users.find { it.id == id }
}