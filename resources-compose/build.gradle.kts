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
    compose_compiler_version?.let { kotlinCompilerPlugin.set("org.jetbrains.kotlin.experimental.compose:compiler:$it") }
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
    wasmJs {
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
        androidMain {
            dependencies {
                api(libs.composeUi)
            }
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
}
