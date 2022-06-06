package fr.sjcqs.konfig

import fr.sjcqs.task.ConfigurationGenerationTask
import fr.sjcqs.task.TaskConfiguration
import fr.sjcqs.task.TaskConfigurationFactory
import fr.sjcqs.utils.get
import fr.sjcqs.utils.pathJoin
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

open class KonfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val configuration = TaskConfigurationFactory.getConfiguration(target)
        target.createTasks(configuration)
    }

    private fun Project.createTasks(configuration: TaskConfiguration) {
        configuration.variants.all { variant ->
            val outputDirectory = project.file(getOutputDirectoryName(project.buildDir, variant.dirName))
            val task = ConfigurationGenerationTask.register(
                project,
                variant,
                configuration.namespaceProvider,
                outputDirectory
            )
            variant.registerJavaGeneratingTask(task, outputDirectory)
            configuration.sourceSets[variant.name].java.srcDir(outputDirectory)
        }
    }

    private fun getOutputDirectoryName(buildDir: File, variantDirectoryName: String): String {
        return pathJoin(buildDir.absolutePath, GENERATED_DIRECTORY, variantDirectoryName)
    }

    companion object {
        private const val GENERATED_DIRECTORY = "generated/source/konfig"
    }
}
