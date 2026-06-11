plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    `java-library`
    `maven-publish`
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])   // artifactId defaults to the module name, e.g. "core"
        }
    }
}

dependencies {
    api(ktorLibs.server.core)
    api(ktorLibs.server.netty)
    api(ktorLibs.server.auth)
    implementation(ktorLibs.server.auth.jwt)
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

    runtimeOnly(libs.h2)

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}
