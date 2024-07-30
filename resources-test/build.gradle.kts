/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-convention")
    id("multiplatform-android-publish-convention")
    id("apple-main-convention")
    id("detekt-convention")
    id("javadoc-stub-convention")
    id("publication-convention")
}

repositories {
    mavenCentral()
    google()

    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    val kotlin_repo_url: String? by project
    kotlin_repo_url?.also { maven(it) }
}

android {
    namespace = "dev.icerock.moko.resources.test"
}

dependencies {
    commonMainApi(projects.resources)
}

val kotlin_language_version: String? by project
val kotlin_api_version: String? by project
println("[QG] The build uses custom LanguageVersion: $kotlin_language_version and ApiVersion: $kotlin_api_version")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlin_language_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf("-language-version", it)
        }
    }
    kotlin_api_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf("-api-version", it)
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile>().configureEach {
    kotlin_language_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf("-language-version", it)
        }
    }
    kotlin_api_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf("-api-version", it)
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile>().configureEach {
    kotlin_language_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf("-language-version", it)
        }
    }
    kotlin_api_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf("-api-version", it)
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon>().configureEach {
    kotlin_language_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf("-language-version", it)
        }
    }
    kotlin_api_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf("-api-version", it)
        }
    }
}
