plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])   // artifactId defaults to the module name, e.g. "ksp"
        }
    }
}

dependencies {
    implementation(libs.ksp.api)
}
