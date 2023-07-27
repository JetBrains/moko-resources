/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()

        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        val kotlin_repo_url: String? by settings
        logger.info("[COMMUNITY_PROJECT_BUILD] kotlin repository url: $kotlin_repo_url")
        kotlin_repo_url?.also { maven(it) }
    }
    versionCatalogs {
        create("moko") {
            from(files("gradle/moko.versions.toml"))
        }
        create("libs") {
            val compose_mpp_version: String? by settings
            compose_mpp_version?.also {version("composeJetbrainsVersion", it) } //rewrites version from .toml
        }
    }
}

includeBuild("resources-build-logic")
includeBuild("resources-generator")

include(":resources")
include(":resources-compose")
include(":resources-test")
