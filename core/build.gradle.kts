plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    `java-library`
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

dependencies {
    api(ktorLibs.server.core)
    api(ktorLibs.server.netty)
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.rateLimit)
    api(ktorLibs.server.contentNegotiation)
    api(ktorLibs.serialization.kotlinx.json)

    api(libs.kotlin.inject.runtime)
    ksp(libs.kotlin.inject)

    implementation(ktorLibs.server.pebble)

    implementation(libs.logback.classic)

    api(libs.exposed.core)
    api(libs.exposed.jdbc)
    api(libs.exposed.dao)
    api(libs.exposed.kotlin.datetime)
    api(libs.hikaricp)

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
