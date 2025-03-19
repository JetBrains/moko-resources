/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("android-base-convention")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("multiplatform-android-publish-convention")
    id("apple-main-convention")
    id("detekt-convention")
    id("org.jetbrains.compose")
    id("javadoc-stub-convention")
    id("publication-convention")
}

repositories {
    mavenCentral()
    google()

    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    val kotlin_repo_url: String? by project
    kotlin_repo_url?.also { maven(it) }
}

compose {
    val compose_compiler_version: String? by project
    compose_compiler_version?.let { kotlinCompilerPlugin.set("org.jetbrains.kotlin:kotlin-compose-compiler-plugin-embeddable:$it") }
}

android {
    defaultConfig {
        minSdk = 21
    }

    namespace = "dev.icerock.moko.resources.compose"
}

kotlin {
    jvm()
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    js(IR) {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(projects.resources)
                api(compose.runtime)
                api(compose.foundation)
            }
        }

        named("androidMain") {
            dependencies {
                api(libs.composeUi)
            }
        }

        named("jvmMain") {
            dependencies {
                api(compose.desktop.common)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

val kotlin_language_version: String? by project
val kotlin_api_version: String? by project
val kotlin_additional_cli_options: String? by project
println("[QG] The build uses custom LanguageVersion: $kotlin_language_version and ApiVersion: $kotlin_api_version")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    val kotlin_version: String? by project
    kotlin_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=$it"
            )
        }
    }
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
    kotlinOptions {
        freeCompilerArgs += listOf("-Xreport-all-warnings")
        freeCompilerArgs += listOf("-Xrender-internal-diagnostic-names")
    }
    kotlin_additional_cli_options?.let {
        kotlinOptions {
            freeCompilerArgs += it.split(" ")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile>().configureEach {
    val kotlin_version: String? by project
    kotlin_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=$it"
            )
        }
    }
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
    kotlinOptions {
        freeCompilerArgs += listOf("-Xreport-all-warnings")
        freeCompilerArgs += listOf("-Xrender-internal-diagnostic-names")
    }
    kotlin_additional_cli_options?.let {
        kotlinOptions {
            freeCompilerArgs += it.split(" ")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile>().configureEach {
    val kotlin_version: String? by project
    kotlin_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=$it"
            )
        }
    }
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
    kotlinOptions {
        freeCompilerArgs += listOf("-Xreport-all-warnings")
        freeCompilerArgs += listOf("-Xrender-internal-diagnostic-names")
    }
    kotlin_additional_cli_options?.let {
        kotlinOptions {
            freeCompilerArgs += it.split(" ")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon>().configureEach {
    val kotlin_version: String? by project
    kotlin_version?.let {
        kotlinOptions {
            freeCompilerArgs += listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=$it"
            )
        }
    }
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
    kotlinOptions {
        freeCompilerArgs += listOf("-Xreport-all-warnings")
        freeCompilerArgs += listOf("-Xrender-internal-diagnostic-names")
    }
    kotlin_additional_cli_options?.let {
        kotlinOptions {
            freeCompilerArgs += it.split(" ")
        }
    }
}
