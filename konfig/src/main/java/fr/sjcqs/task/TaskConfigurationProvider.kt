package fr.sjcqs.task

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project

object TaskConfigurationProvider {
    private const val APPLICATION_PLUGIN_ID: String = "com.android.application"
    private const val LIBRARY_PLUGIN_ID: String = "com.android.library"

    fun getConfiguration(project: Project): TaskConfiguration {
        val plugins = project.plugins
        return when {
            plugins.hasPlugin(APPLICATION_PLUGIN_ID) -> AndroidApplication.getConfiguration(project)
            plugins.hasPlugin(LIBRARY_PLUGIN_ID) -> AndroidLibrary.getConfiguration(project)
            else -> throw IllegalArgumentException("Konfig only support android libraries and applications.")
        }
    }

    private object AndroidApplication {
        fun getConfiguration(project: Project): TaskConfiguration {
            val extension = project.extensions.getByType(AppExtension::class.java)
            val variants = extension.applicationVariants
            val sourceSet = extension.sourceSets
            return TaskConfiguration(variants, sourceSet)
        }
    }

    private object AndroidLibrary {

        fun getConfiguration(project: Project): TaskConfiguration {
            val extension = project.extensions.getByType(LibraryExtension::class.java)
            val variants = extension.libraryVariants
            val sourceSet = extension.sourceSets
            return TaskConfiguration(variants, sourceSet)
        }
    }
}