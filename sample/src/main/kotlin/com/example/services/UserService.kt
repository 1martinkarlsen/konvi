package com.example.services

import com.example.models.User
import com.example.repositories.UserRepository
import com.konvi.logging.logger
import me.tatarka.inject.annotations.Inject

class UserService @Inject constructor(private val repo: UserRepository) {
    private val log = logger<UserService>()

    fun getAll(): List<User> = repo.findAll().also { log.info("Fetched ${it.size} users") }
    fun find(id: Int): User? = repo.find(id).also { log.info("Fetched user $id: $it") }
}