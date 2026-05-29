plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt)
}

allprojects {
    group = "com.konvi"
    version = "0.1.0-SNAPSHOT"
}

detekt {
    buildUponDefaultConfig = true
    source.setFrom(
        fileTree(rootDir) {
            include("**/src/main/kotlin/**/*.kt")
        }
    )
}
