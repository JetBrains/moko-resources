/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "resources-generator"

pluginManagement {
    repositories {
        mavenCentral()
        google()

        gradlePluginPortal()

        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
        val kotlin_repo_url: String? by settings
        kotlin_repo_url?.also { maven(it) }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()

        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
        val kotlin_repo_url: String? by settings
        kotlin_repo_url?.also { maven(it) }
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
            val kotlin_version: String? by settings
            kotlin_version?.also { version("kotlinVersion", it) } //rewrites version from .toml
        }
        create("moko") {
            from(files("../gradle/moko.versions.toml"))
        }
    }
}
// support gradle Composite builds - https://github.com/icerockdev/moko-resources/issues/558
if (gradle.parent == null) {
    includeBuild("../resources-build-logic")
}
