/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.gradle.tasks

import dev.icerock.gradle.utils.toKonanFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileFilter
import kotlin.io.path.absolutePathString
import org.jetbrains.kotlin.library.loader.KlibLoader
import dev.icerock.gradle.actions.apple.KlibResourcesComponent

abstract class CopyExecutableResourcesToApp : DefaultTask() {

    @get:InputFiles
    @get:Classpath
    abstract val klibs: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        group = "moko-resources"
    }

    @TaskAction
    fun copyResources() {
        val outputDir: File = outputDirectory.get().asFile

        klibs
            .filter { library -> library.extension == "klib" }
            .filter(File::exists)
            .forEach { inputFile ->
                val library = KlibLoader { libraryPaths(inputFile.path) }.load().librariesStdlibFirst.single()
                val component = library.getComponent(KlibResourcesComponent.Kind)!!

                // extracting bundles
                component
                    .resourcesDir
                    .absolutePathString()
                    .let(::File)
                    .listFiles(FileFilter { it.extension == "bundle" })
                    // copying bundles to app
                    ?.forEach {
                        logger.info("${it.absolutePath} copying to $outputDir")
                        it.copyRecursively(target = File(outputDir, it.name), overwrite = true)
                    }
            }
    }
}
