package fr.sjcqs.task

import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.ProductFlavor
import fr.sjcqs.ast.KonfigMap
import fr.sjcqs.ast.Token
import fr.sjcqs.di.DependencyContainer
import fr.sjcqs.parser.Parser
import fr.sjcqs.transpiler.FileTranspiler
import fr.sjcqs.utils.Logger
import fr.sjcqs.utils.MapMerger
import fr.sjcqs.utils.pathJoin
import groovy.util.XmlParser
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.yaml.snakeyaml.Yaml
import java.io.File
import javax.inject.Inject

@CacheableTask
abstract class ConfigurationGenerationTask @Inject constructor() : DefaultTask() {

    @get:Input
    lateinit var packageName: String

    @get:InputFiles
    @get:Incremental
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val settingsFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        val logger: Logger = DependencyContainer.logger
        val yaml: Yaml = DependencyContainer.yaml
        val parser: Parser = DependencyContainer.parser
        val transpiler: FileTranspiler = DependencyContainer.transpiler
        val merger: MapMerger = DependencyContainer.mapMerger

        logger.i("Generating settings: (incremental: ${inputChanges.isIncremental})")
        inputChanges.getFileChanges(settingsFiles).forEach { change ->
            logger.i("${change.changeType}: ${change.normalizedPath}")
        }

        val variantConfiguration = settingsFiles.map {
            yaml.loadConfig(it)
        }
        val settings = merger.deepMerge(variantConfiguration)

        if (settings.isNotEmpty()) {
            val root = parser.parse(settings)
            val fileSpec = transpiler.transpile(packageName, root)
            fileSpec.writeTo(outputDir.get().asFile.toPath())
        }
    }

    companion object {
        private const val DEFAULT_CONFIG_NAME = "default"
        private const val INPUT_DIRECTORY = "config"
        private val LOAD_CLASS = mutableMapOf<String, Any?>()::class

        @Throws(InvalidUserDataException::class)
        fun register(
            project: Project,
            variant: BaseVariant,
            namespaceProvider: Provider<String?>,
            targetDirectory: File
        ): TaskProvider<ConfigurationGenerationTask> {
            val name = "generate${variant.name}${Token.Root.ROOT_KEY}"
            return project.tasks.register(name, ConfigurationGenerationTask::class.java) { task ->
                task.apply {
                    /* We use the module namespace then we fall back to the packageName declared in the manifest*/
                    packageName = namespaceProvider.orNull ?: variant.getPackageNameFromManifest() ?: error(
                        "Failed to found the module packageName.\n" +
                            "Either declare a namespace in build.gradle or a packageName in your manifest file."
                    )
                    outputDir.set(targetDirectory)

                    val flavorName = variant.flavorName
                    val buildType = variant.buildType.name
                    val dimensions = variant.productFlavors.reversed().map(ProductFlavor::getName)

                    settingsFiles.setFrom(
                        listOf(listOf(DEFAULT_CONFIG_NAME), dimensions, listOf(flavorName, buildType))
                            .flatten()
                            .flatMap { listOf(it, it.toSecretConfig()) }
                            .map { project.file(pathJoin(INPUT_DIRECTORY, "$it.yml")) }
                    )
                }
            }
        }

        private fun String.toSecretConfig(): String = "${this}_secret"

        private fun Yaml.loadConfig(file: File): KonfigMap {
            if (!file.isFile) {
                return emptyMap()
            }
            return file.reader().use {
                loadAs(it, LOAD_CLASS.java) ?: emptyMap()
            }
        }

        private fun BaseVariant.getPackageNameFromManifest(): String? {
            val manifestFile = sourceSets.map { it.manifestFile }
                .firstOrNull(File::exists)
                ?: return null
            val node = XmlParser().parse(manifestFile)
            return node.attribute("package")?.toString().orEmpty()
        }
    }
}
