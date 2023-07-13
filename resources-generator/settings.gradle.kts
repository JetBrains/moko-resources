/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

pluginManagement {
    repositories {
        mavenCentral()
        google()

        gradlePluginPortal()

        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()

        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("moko") {
            from(files("../gradle/moko.versions.toml"))
        }
    }
}

includeBuild("../resources-build-logic")
