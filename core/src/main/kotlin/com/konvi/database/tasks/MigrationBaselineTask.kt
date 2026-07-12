package com.konvi.database.tasks

import com.konvi.database.migrationBaseline

fun main(args: Array<String>) {
    migrationBaseline(args.getOrNull(0))
}
