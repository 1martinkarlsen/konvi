package com.example.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable : LongIdTable("user") {
    val name = varchar(name = "name", 80)
    val email = varchar(name = "email", 80)
    val password = varchar(name = "password", 80)
    val createdAt = datetime(name = "created_at")
    val emailVerified = bool(name = "email_verified")
}