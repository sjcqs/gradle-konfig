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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.yaml.snakeyaml.Yaml
import java.io.File
import javax.inject.Inject

abstract class ConfigurationGenerationTask @Inject constructor() : DefaultTask() {
    private val logger: Logger = DependencyContainer.taskLogger
    private val yaml: Yaml = DependencyContainer.yaml
    private val parser: Parser = DependencyContainer.parser
    private val transpiler: FileTranspiler = DependencyContainer.transpiler
    private val merger: MapMerger = DependencyContainer.mapMerger

    @get:Input
    lateinit var packageName: String

    @get:InputFiles
    @get:Incremental
    abstract val settingsFiles: ConfigurableFileCollection

    @get:OutputDirectory
    lateinit var outputDir: File

    @TaskAction
    fun execute(inputChanges: InputChanges) {
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
            fileSpec.writeTo(outputDir.toPath())
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

    companion object {
        private const val DEFAULT_CONFIG_NAME = "default"
        private const val GENERATED_DIRECTORY = "generated/source/konfig"
        private const val INPUT_DIRECTORY = "config"
        private val LOAD_CLASS = mutableMapOf<String, Any?>()::class

        @Throws(InvalidUserDataException::class)
        fun create(project: Project, variant: BaseVariant): ConfigurationGenerationTask {
            val name = "generate${variant.name}${Token.Root.ROOT_KEY}"
            return project.tasks.create(name, ConfigurationGenerationTask::class.java) { task ->
                task.apply {
                    packageName = variant.getPackageNameFromManifest()
                    outputDir = project.file(getOutputDirectoryName(project.buildDir, variant.dirName))

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

        private fun getOutputDirectoryName(buildDir: File, variantDirectoryName: String): String {
            return pathJoin(buildDir.absolutePath, GENERATED_DIRECTORY, variantDirectoryName)
        }
    }
}

private fun BaseVariant.getPackageNameFromManifest(): String {
    val manifestFile = sourceSets.map { it.manifestFile }
        .first(File::exists)
    val node = XmlParser().parse(manifestFile)
    return node.attribute("package").toString()
}
