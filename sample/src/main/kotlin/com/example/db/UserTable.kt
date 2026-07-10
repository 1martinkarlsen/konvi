package com.example.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable : LongIdTable("user") {
    val name = varchar(name = "name", length = 80)
    val email = varchar(name = "email", length = 80)
    val password = varchar(name = "password", length = 80)
    val createdAt = datetime(name = "created_at")
    val emailVerified = bool(name = "email_verified")
}
