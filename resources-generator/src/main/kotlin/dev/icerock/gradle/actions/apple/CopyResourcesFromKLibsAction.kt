/*
 * Copyright 2024 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.gradle.actions.apple

import dev.icerock.gradle.utils.klibs
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.logging.Logger
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
import kotlin.io.path.extension
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries

internal abstract class CopyResourcesFromKLibsAction : Action<Task> {

    protected fun copyResourcesFromLibraries(
        linkTask: KotlinNativeLink,
        outputDir: File
    ) {
        val logger: Logger = linkTask.logger

        linkTask.klibs
            .onEach { logger.debug("found klib dependency {}", it) }
            .flatMap { getBundlesFromSources(sourceFile = it, logger = logger) }
            .forEach { bundle ->
                logger.info("copy $bundle to $outputDir")
                bundle.copyRecursively(File(outputDir, bundle.name), overwrite = true)
            }
    }

    /**
     * Search bundles in klib different types.
     *
     * We know about 3 types of klib in filesystem:
     * 1. packed klib - single file with .klib extension
     * 2. unpacked klib directory - root directory with klib content (used for local project
     * dependencies)
     * 3. unpacked klib content - all files inside klib directory (used for current project
     * compilation results)
     *
     * @param sourceFile file from linking task dependencies and sources list
     * @param logger gradle logger
     *
     * @return list of .bundle directories founded in klibs
     */
    private fun getBundlesFromSources(sourceFile: File, logger: Logger): List<File> {
        val isPackedKlib = sourceFile.isFile && sourceFile.extension == "klib"
        val isUnpackedKlib = sourceFile.isDirectory

        return if (isPackedKlib || isUnpackedKlib) {
            logger.info("found klib {}", sourceFile)
            getBundlesFromKotlinLibrary(sourceFile)
        } else if (sourceFile.name == "manifest" && sourceFile.parentFile.name == "default") {
            // for unpacked klibs we can see content files instead of klib directory.
            // try to check this case
            logger.info("found manifest of klib {}", sourceFile)
            val unpackedKlibRoot: File = sourceFile.parentFile.parentFile
            getBundlesFromKotlinLibrary(unpackedKlibRoot)
        } else {
            logger.debug("found some file {}", sourceFile)
            emptyList()
        }
    }

    private fun getBundlesFromKotlinLibrary(klibFile: File): List<File> {
        val library = KlibLoader { libraryPaths(klibFile) }.load().librariesStdlibFirst.singleOrNull() ?: return emptyList()
        val component = library.getComponent(KlibResourcesComponent.Kind) ?: return emptyList()
        return component.resourcesDir.listDirectoryEntries().filter { it.isDirectory() && it.extension == "bundle" }.map { it.toFile() }
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
