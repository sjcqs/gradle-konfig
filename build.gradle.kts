import org.jetbrains.changelog.closure
import org.jetbrains.changelog.date

buildscript {
    repositories {
        mavenCentral()
        jcenter()
        gradlePluginPortal()
    }


    val changelogPluginVersion: String by project
    val kotlinVersion: String by project
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.intellij.plugins:gradle-changelog-plugin:$changelogPluginVersion")
    }
}
apply(plugin = "org.jetbrains.changelog")

configure<org.jetbrains.changelog.ChangelogPluginExtension> {
    val pluginVersion: String by project
    version = pluginVersion
    path = "${project.projectDir}/CHANGELOG.md"
    header = closure { "[$version] - ${date()}" }
    itemPrefix = "-"
    keepUnreleasedSection = true
    unreleasedTerm = "[Unreleased]"
    groups = listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security")
}