package fr.sjcqs.konfig

import fr.sjcqs.task.SettingsGenerationTask
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
        val (variants, sourceSets) = configuration
        variants.all { variant ->
            val task = SettingsGenerationTask.create(project, variant)
            val outputDirectory = task.pluginDirectory
            variant.registerJavaGeneratingTask(task, outputDirectory)
            sourceSets[variant.name].java.srcDir(outputDirectory)
        }
    }
}