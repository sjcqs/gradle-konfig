import org.jetbrains.changelog.date

plugins {
    kotlin("jvm") version "1.6.21" apply false
    id("org.jetbrains.changelog") version "1.3.1"
}

changelog {
    val pluginVersion: String by project
    version.set(pluginVersion)
    path.set("${project.projectDir}/CHANGELOG.md")
    header.set(provider { "[$version] - ${date()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}