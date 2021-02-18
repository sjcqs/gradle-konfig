package fr.sjcqs.konfig

import fr.sjcqs.task.ConfigurationGenerationTask
import fr.sjcqs.task.TaskConfiguration
import fr.sjcqs.task.TaskConfigurationProvider
import fr.sjcqs.utils.get
import org.gradle.api.Plugin
import org.gradle.api.Project

open class KonfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val configuration = TaskConfigurationProvider.getConfiguration(target)
        target.createTasks(configuration)
    }

    private fun Project.createTasks(configuration: TaskConfiguration) {
        configuration.variants.all { variant ->
            val task = ConfigurationGenerationTask.create(project, variant)
            val outputDirectory = task.outputDir
            variant.registerJavaGeneratingTask(task, outputDirectory)
            configuration.sourceSets[variant.name].java.srcDir(outputDirectory)
        }
    }
}