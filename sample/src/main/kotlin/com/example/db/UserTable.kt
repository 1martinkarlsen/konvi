package com.example.db

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object UserTable : LongIdTable("user") {
    val name = varchar(name = "name", length = 80)
    val email = varchar(name = "email", length = 80)
    val password = varchar(name = "password", length = 80)
    val createdAt = datetime(name = "created_at")
    val emailVerified = bool(name = "email_verified")
}
