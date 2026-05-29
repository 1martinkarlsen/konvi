plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

gradlePlugin {
    plugins {
        create("konvi") {
            id = "com.konvi"
            implementationClass = "com.konvi.gradle.KonviPlugin"
        }
    }
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:${libs.versions.ksp.get()}")
}
