plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()

    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    val kotlin_repo_url: String? by project
    kotlin_repo_url?.also { maven(it) }
}

dependencies {
    api(libs.mokoMultiplatformPlugin)
    println("kgp version from build logic")
    println(libs.kotlinGradlePlugin.get().version)
    api(libs.kotlinGradlePlugin)
    api(libs.androidGradlePlugin)
    api(libs.detektGradlePlugin)
}
