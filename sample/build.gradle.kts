plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    application
}

application {
    mainClass = "com.example.MainKt"
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

dependencies {
    implementation(project(":core"))
    ksp(project(":ksp"))
    ksp(libs.kotlin.inject)
    runtimeOnly(libs.h2)
}
