package com.konvi.database.tasks

import com.konvi.database.migrationStatus
import java.text.SimpleDateFormat

private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

fun main() {
    migrationStatus().forEach {
        val installedOn = it.installedOn?.let { date -> DATE_FORMAT.format(date) }.orEmpty()
        println("${it.version}\t${it.description}\t$installedOn\t${it.state}")
    }
}
