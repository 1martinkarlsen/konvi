plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
}

allprojects {
    group = "com.konvi"
    version = "0.1.0-SNAPSHOT"
}
