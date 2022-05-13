#!/usr/bin/env -S kotlinc -script --

@file:CompilerOptions("-jvm-target", "1.8")
@file:DependsOn("com.github.ajalt:clikt:2.8.0")

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import java.io.File
import java.time.LocalDate
import java.util.Properties
import kotlin.system.exitProcess

Delivery.main(args)
sealed class Action {
    object PrepareRelease : Action()
    object Publish : Action()
    object ExtractChangeLog : Action()
}

object Delivery : CliktCommand(name = "delivery") {
    private val action: Action by option("--action", "-a")
        .choice(
            "publish" to Action.Publish,
            "extract-changelog" to Action.ExtractChangeLog,
            "prepare-release" to Action.PrepareRelease,
        ).required()

    private val propertiesFile
        get() = File(PROPERTIES_FILE)

    private val changelogFile
        get() = File(CHANGELOG_FILE)

    private fun loadProperties() = Properties().apply { load(propertiesFile.inputStream()) }

    private val version by option("--version", "-v", help = "Version to prepare release")

    override fun run() {
        when (action) {
            is Action.PrepareRelease -> {
                version?.let { version ->
                    prepareRelease(version)
                } ?: echo("Error: Missing option \"--version\".", err = true)
            }
            is Action.Publish -> publish()
            is Action.ExtractChangeLog -> extractChangelog()
        }
    }

    private fun prepareRelease(version: String) {
        if (!SEM_VERSION_REGEX.matches(version)) {
            echo("Error: version \"$version\" does not respect semantic versioning", err = true)
            return
        }
        // Update version
        val properties = loadProperties()
        properties[VERSION_PROPERTY] = version
        properties.store(propertiesFile.outputStream(), null)

        // Update changelog
        val changelogText = changelogFile
            .readText()
            .replace(UNRELEASED_HEADER, formatChangelogHeader(version))
        changelogFile.writeText(changelogText)

        // Commit and tag changes
        commit("Prepare release $version", CHANGELOG_FILE, PROPERTIES_FILE)
            .start()
            .redirectOutput()
            .exitProcessOnFailure()
        tag(version).start().redirectOutput().exitProcessOnFailure()
    }

    private fun extractChangelog() {
        gradle("getChangelog", "-q").start().redirectOutput().exitProcessOnFailure()
    }

    private fun publish() {
        gradle("assemble").start().redirectOutput().exitProcessOnFailure()
        val publishKey = System.getenv("GRADLE_PUBLISH_KEY")
        val publishSecret = System.getenv("GRADLE_PUBLISH_SECRET")
        val params = if (publishKey != null && publishSecret != null) {
            arrayOf(
                "-Pgradle.publish.key=$publishKey",
                "-Pgradle.publish.secret=$publishSecret"
            )
        } else {
            emptyArray()
        }
        gradle("publishPlugins", *params).start().redirectOutput().exitProcessOnFailure()
    }

    private fun gradle(task: String, vararg params: String): ProcessBuilder {
        return ProcessBuilder().command(GRADLE_EXEC, task, *params)
    }

    private fun commit(message: String, vararg pathSpec: String): ProcessBuilder {
        return ProcessBuilder().command("git", "commit", "-m $message", *pathSpec)
    }

    private fun tag(version: String): ProcessBuilder {
        return ProcessBuilder().command("git", "tag", version, "-am Version $version")
    }

    private fun Process.redirectOutput(): Process {
        inputStream.copyTo(System.out)
        errorStream.copyTo(System.err)
        return this
    }


    private fun Process.exitProcessOnFailure(message: String? = null): Process {
        onExit().whenComplete { process: Process, error: Throwable? ->
            val exitValue = process.exitValue()
            if (exitValue != 0 || error != null) {
                if (message != null) echo("$message ($exitValue)")
                exitProcess(-1)
            }
        }
        return this
    }

    private fun formatChangelogHeader(version: String) = """
        |$UNRELEASED_HEADER
        |
        |## [${version}] - ${LocalDate.now()}
        |[${version}]: https://github.com/sjcqs/gradle-konfig/releases/tag/${version}
    """.trimMargin()

    private const val GRADLE_EXEC = "./gradlew"
    private val SEM_VERSION_REGEX =
        """^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?$""".toRegex()
    private const val PROPERTIES_FILE = "gradle.properties"
    private const val CHANGELOG_FILE = "CHANGELOG.md"
    private const val VERSION_PROPERTY = "pluginVersion"
    private val UNRELEASED_HEADER = """
    |## [Unreleased]
    |[Unreleased]: https://github.com/sjcqs/gradle-konfig/compare/1.0.0...HEAD
    """.trimMargin()
}
