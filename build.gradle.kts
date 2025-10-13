/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

buildscript {
    repositories {
        mavenCentral()
        google()

        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")

        gradlePluginPortal()
        val kotlin_repo_url: String? by project
        kotlin_repo_url?.also { maven(it) }
    }
    dependencies {
        classpath("dev.icerock.moko:resources-generator")
        classpath(libs.composeJetBrainsPlugin)
        classpath(libs.kotlinComposePlugin)
        classpath(":resources-build-logic")
    }
}

repositories {
    mavenCentral()
    google()

    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    val kotlin_repo_url: String? by project
    kotlin_repo_url?.also { maven(it) }
}

allprojects {
    plugins.withId("org.gradle.maven-publish") {
        group = "dev.icerock.moko"
        version = moko.versions.resourcesVersion.get()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-opt-in",
                "kotlinx.cinterop.ExperimentalForeignApi"
            )
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile>().configureEach { // for JS
        compilerOptions {
            freeCompilerArgs.add("-Xpartial-linkage-loglevel=ERROR")
        }
    }
}

afterEvaluate {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach kotlinCompilationTask@ {
        compilerOptions {
            allWarningsAsErrors.set(false)
            freeCompilerArgs.set(
                freeCompilerArgs.getOrElse(emptyList()).filterNot { it == "-Werror" }
            )
            logger.info(" ${this@kotlinCompilationTask.path} : disabled -Werror")
        }
    }
}
