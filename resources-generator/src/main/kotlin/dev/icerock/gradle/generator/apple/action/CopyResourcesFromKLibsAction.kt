/*
 * Copyright 2023 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.gradle.generator.apple.action

import dev.icerock.gradle.utils.klibs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.file.createTempDir
import org.jetbrains.kotlin.konan.file.unzipTo
import java.io.File
import org.jetbrains.kotlin.konan.file.File as KFile

internal abstract class CopyResourcesFromKLibsAction : Action<Task> {

    protected fun copyKlibsResourcesIntoFramework(linkTask: KotlinNativeLink) {
        val project = linkTask.project
        val framework = linkTask.binary as Framework

        copyResourcesFromLibraries(
            linkTask = linkTask,
            project = project,
            outputDir = framework.outputFile
        )
    }

    protected fun copyResourcesFromLibraries(
        linkTask: KotlinNativeLink,
        project: Project,
        outputDir: File
    ) {
        linkTask.klibs
            .filter { it.extension == "klib" }
            .filter { it.exists() }
            .forEach { inputFile ->
                project.logger.info("copy resources from $inputFile into $outputDir")

                val klibFile = KFile(inputFile.path)

                val extractedResourcesDir = createTempDir(klibFile.name)
                extractedResourcesDir.deleteOnExitRecursively()
                klibFile.unzipTo(extractedResourcesDir, fromSubdirectory = KFile("/default/resources"))

                try {
                    File(extractedResourcesDir.path).copyRecursively(
                        target = outputDir,
                        overwrite = true
                    )
                } catch (@Suppress("SwallowedException") exc: NoSuchFileException) {
                    project.logger.info("resources in $inputFile not found")
                } catch (@Suppress("SwallowedException") exc: java.nio.file.NoSuchFileException) {
                    project.logger.info("resources in $inputFile not found (empty lib)")
                }
            }
    }
}
