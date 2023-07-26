/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        val kotlin_repo_url: String? by settings
        kotlin_repo_url?.also { maven(it) }
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
            val kotlin_version: String? by settings
            kotlin_version?.also {version("kotlinVersion", it) } //rewrites version from .toml
        }
    }
}
