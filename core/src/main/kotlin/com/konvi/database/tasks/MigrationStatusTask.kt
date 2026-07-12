package com.konvi.database.tasks

import com.konvi.database.migrationStatus

fun main() {
    migrationStatus().forEach { println("${it.version}\t${it.description}\t${it.state}") }
}
