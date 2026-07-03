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
import org.jetbrains.kotlin.library.loader.KlibLoader
import org.jetbrains.kotlin.library.KlibComponent
import org.jetbrains.kotlin.library.KlibComponentLayout
import org.jetbrains.kotlin.library.KlibConstants.KLIB_DEFAULT_COMPONENT_NAME
import org.jetbrains.kotlin.library.KlibConstants.KLIB_RESOURCES_FOLDER_NAME
import org.jetbrains.kotlin.library.KlibLayoutReader
import java.io.File
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString
import kotlin.io.path.extension
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries

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

                val library = KlibLoader { libraryPaths(inputFile.path) }.load().librariesStdlibFirst.single()
                val component = library.getComponent(KlibResourcesComponent.Kind)!!

                try {
                    File(component.resourcesDir.pathString).copyRecursively(
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

internal class KlibResourcesComponent(
    private val layoutReader: KlibLayoutReader<KlibResourcesComponentLayout>
) : KlibComponent {
    val resourcesDir: Path
        get() = layoutReader.readExtractingToTemp(KlibResourcesComponentLayout::resourcesDir)

    object Kind : KlibComponent.Kind<KlibResourcesComponent, KlibResourcesComponentLayout> {
        override fun createLayout(root: Path) = KlibResourcesComponentLayout(root)

        override fun createComponentIfDataInKlibIsAvailable(layoutReader: KlibLayoutReader<KlibResourcesComponentLayout>) =
            if (layoutReader.readInPlaceOrFallback(false) { it.resourcesDir.exists() }) KlibResourcesComponent(layoutReader) else null
    }
}

internal class KlibResourcesComponentLayout(root: Path) : KlibComponentLayout(root) {
    val resourcesDir: Path
        get() = root.resolve(KLIB_DEFAULT_COMPONENT_NAME).resolve(KLIB_RESOURCES_FOLDER_NAME)
}
