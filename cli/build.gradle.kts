plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass = "com.konvi.cli.MainKt"
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

dependencies {
    implementation(libs.clikt)
}
