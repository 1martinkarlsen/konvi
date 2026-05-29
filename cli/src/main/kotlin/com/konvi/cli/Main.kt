package com.konvi.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) = Konvi().subcommands(NewCommand()).main(args)

class Konvi : CliktCommand(name = "konvi") {
    override fun run() = Unit
}

class NewCommand : CliktCommand(name = "new", help = "Scaffold a new Konvi project") {
    override fun run() {
        echo("Scaffolding new Konvi project...")
    }
}
