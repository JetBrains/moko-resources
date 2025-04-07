/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-extended-convention")
    id("multiplatform-android-publish-convention")
    id("apple-main-convention")
    id("apple-bundle-searcher-convention")
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

kotlin {
    sourceSets {
        commonJsMain.dependencies {
            implementation(libs.kotlinxCoroutines)

            api(npm("bcp-47", "2.1.0"))
            api(npm("@messageformat/core", "3.1.0"))
            api(npm("mini-css-extract-plugin", "2.7.5"))
            api(npm("css-loader", "6.7.3"))
            api(npm("style-loader", "3.3.2"))
        }

        wasmJsMain.dependencies {
            implementation(libs.kotlinxBrowser)
        }
    }
}

android {
    namespace = "dev.icerock.moko.resources"
}

dependencies {
    commonMainApi(libs.mokoGraphics)

    jvmMainImplementation(libs.icu4j)
    jvmMainImplementation(libs.batikRasterizer)
    jvmMainImplementation(libs.batikTranscoder)

    androidMainImplementation(libs.appCompatResources)

    iosTestImplementation(libs.mokoTestCore)
}

tasks.named("publishToMavenLocal") {
    val pluginPublish = gradle.includedBuild("resources-generator")
        .task(":publishToMavenLocal")
    dependsOn(pluginPublish)
}

val copyIosX64TestResources = tasks.register<Copy>("copyIosX64TestResources") {
    from("src/iosTest/resources")
    into("build/bin/iosX64/debugTest")
}

tasks.findByName("iosX64Test")!!.dependsOn(copyIosX64TestResources)

val copyIosArm64TestResources = tasks.register<Copy>("copyIosArm64TestResources") {
    from("src/iosTest/resources")
    into("build/bin/iosSimulatorArm64/debugTest")
}

tasks.findByName("iosSimulatorArm64Test")!!.dependsOn(copyIosArm64TestResources)

val kotlin_language_version: String? by project
val kotlin_api_version: String? by project
val kotlin_additional_cli_options: String? by project
println("[QG] The build uses custom LanguageVersion: $kotlin_language_version and ApiVersion: $kotlin_api_version")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlin_language_version?.let {
        compilerOptions {
            freeCompilerArgs.addAll("-language-version", it)
        }
    }
    kotlin_api_version?.let {
        compilerOptions {
            freeCompilerArgs.addAll("-api-version", it)
        }
    }
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xreport-all-warnings",
            "-Xrender-internal-diagnostic-names",
            "-Wextra",
            "-Xuse-fir-experimental-checkers"
        )
    }
    kotlin_additional_cli_options?.let {
        compilerOptions {
            freeCompilerArgs.addAll(it.split(" "))
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile>().configureEach {
    kotlin_language_version?.let {
        compilerOptions {
            freeCompilerArgs.addAll("-language-version", it)
        }
    }
    kotlin_api_version?.let {
        compilerOptions {
            freeCompilerArgs.addAll("-api-version", it)
        }
    }
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xreport-all-warnings",
            "-Xrender-internal-diagnostic-names",
            "-Wextra",
            "-Xuse-fir-experimental-checkers"
        )
    }
    kotlin_additional_cli_options?.let {
        compilerOptions {
            freeCompilerArgs.addAll(it.split(" "))
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile>().configureEach {
    kotlin_language_version?.let {
        compilerOptions {
            freeCompilerArgs.addAll("-language-version", it)
        }
    }
    kotlin_api_version?.let {
        compilerOptions {
            freeCompilerArgs.addAll("-api-version", it)
        }
    }
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xreport-all-warnings",
            "-Xrender-internal-diagnostic-names",
            "-Wextra",
            "-Xuse-fir-experimental-checkers"
        )
    }
    kotlin_additional_cli_options?.let {
        compilerOptions {
            freeCompilerArgs.addAll(it.split(" "))
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon>().configureEach {
    kotlin_language_version?.let {
        compilerOptions {
            freeCompilerArgs.addAll("-language-version", it)
        }
    }
    kotlin_api_version?.let {
        compilerOptions {
            freeCompilerArgs.addAll("-api-version", it)
        }
    }
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xreport-all-warnings",
            "-Xrender-internal-diagnostic-names",
            "-Wextra",
            "-Xuse-fir-experimental-checkers"
        )
    }
    kotlin_additional_cli_options?.let {
        compilerOptions {
            freeCompilerArgs.addAll(it.split(" "))
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
