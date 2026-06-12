plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass = "com.konvi.cli.MainKt"
    applicationName = "konvi"
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

dependencies {
    implementation(libs.clikt)
}

val templatesDir = layout.projectDirectory.dir("src/main/resources/templates")
val templateIndexDir = layout.buildDirectory.dir("generated/templateIndex")

val generateTemplateIndex by tasks.registering {
    val root = templatesDir.asFile
    val indexFile = templateIndexDir.get().dir("templates").asFile.resolve("index.txt")
    inputs.dir(templatesDir)
    outputs.dir(templateIndexDir)
    doLast {
        val entries = root.walkTopDown()
            .filter { it.isFile }
            .map { it.relativeTo(root).invariantSeparatorsPath }
            .sorted()
            .toList()
        indexFile.parentFile.mkdirs()
        indexFile.writeText(entries.joinToString("\n"))
    }
}

sourceSets.main {
    resources.srcDir(generateTemplateIndex)
}
