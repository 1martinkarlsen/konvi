package com.konvi.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) = Konvi().subcommands(NewCommand()).main(args)

class Konvi : CliktCommand(name = "konvi") {
    override fun run() = Unit
}
