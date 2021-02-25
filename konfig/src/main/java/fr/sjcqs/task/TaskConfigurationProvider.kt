package fr.sjcqs.task

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.AndroidSourceSet
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectCollection
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

object TaskConfigurationProvider {
    private const val APPLICATION_PLUGIN_ID: String = "com.android.application"
    private const val LIBRARY_PLUGIN_ID: String = "com.android.library"

    fun getConfiguration(project: Project): TaskConfiguration {
        val plugins = project.plugins
        return when {
            plugins.hasPlugin(APPLICATION_PLUGIN_ID) -> AndroidApplicationTaskConfiguration(project)
            plugins.hasPlugin(LIBRARY_PLUGIN_ID) -> AndroidLibraryTaskConfiguration(project)
            else -> throw IllegalArgumentException("Konfig only support android libraries and applications.")
        }
    }

    private class AndroidApplicationTaskConfiguration(project: Project) : TaskConfiguration {
        private val extension = project.extensions.getByType(AppExtension::class.java)
        override val variants: DomainObjectCollection<out BaseVariant>
            get() = extension.applicationVariants
        override val sourceSets: NamedDomainObjectContainer<AndroidSourceSet>
            get() = extension.sourceSets
    }

    private class AndroidLibraryTaskConfiguration(project: Project) : TaskConfiguration {
        private val extension = project.extensions.getByType(LibraryExtension::class.java)
        override val variants: DomainObjectCollection<out BaseVariant>
            get() = extension.libraryVariants
        override val sourceSets: NamedDomainObjectContainer<AndroidSourceSet>
            get() = extension.sourceSets
    }
}
