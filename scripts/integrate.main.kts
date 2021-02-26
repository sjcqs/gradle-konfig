#!/usr/bin/env -S kotlinc -script --

@file:DependsOn("com.github.ajalt:clikt:2.8.0")

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import java.io.File

Integration.main(args)

object Integration : CliktCommand(name = "integration") {

    private val skipBuild by option("--skip-build", help = "Skip build")
        .flag(default = false)
    private val skipLint by option("--skip-lint", help = "Skip the linter")
        .flag(default = false)
    private val skipChecks by option("--skip-checks", help = "Skip checks")
        .flag(default = false)

    private val skipIntegrationTests by option(
        "--skip-integration-test", help = "Skip integration tests"
    ).flag(default = false)

    override fun run() {

        if (!skipBuild) {
            runBuild()
        }
        if (!skipLint) {
            runLint()
        }

        if (!skipChecks) {
            runChecks()
        }

        if (!skipIntegrationTests) {
            runIntegrationTests()
        }
    }

    private fun runBuild() {
        println("Building...")
        gradle("assemble")
            .start()
            .redirectOutput()
    }

    private fun runLint() {
        println("Lint...")
        gradle("spotlessCheck")
            .start()
            .redirectOutput()
    }

    private fun runChecks() {
        println("Checks..")
        gradle("check")
            .start()
            .redirectOutput()
    }

    private fun runIntegrationTests() {
        println("Integrations tests...")
        gradle("publishAllPublicationsToTestRepository")
            .start()
            .redirectOutput()
        gradle("test")
            .directory(File("test-app"))
            .start()
            .redirectOutput()
    }

    private const val GRADLE_EXEC = "./gradlew"

    private fun gradle(task: String, vararg params: String): ProcessBuilder {
        return ProcessBuilder().command(GRADLE_EXEC, task, *params)
    }

    private fun Process.redirectOutput(): Process {
        inputStream.copyTo(System.out)
        errorStream.copyTo(System.err)
        return this
    }
}
