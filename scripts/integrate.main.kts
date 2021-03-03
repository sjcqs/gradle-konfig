#!/usr/bin/env -S kotlinc -script --

@file:DependsOn("com.github.ajalt:clikt:2.8.0")

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import java.io.File
import kotlin.system.exitProcess

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
        echo("Building...")
        gradle("assemble")
            .start()
            .redirectOutput()
            .exitProcessOnFailure("[Failed] Building.")
    }

    private fun runLint() {
        echo("Lint...")
        gradle("spotlessCheck")
            .start()
            .redirectOutput()
            .exitProcessOnFailure("[Failed] Lint.")
    }

    private fun runChecks() {
        echo("Checks..")
        gradle("check")
            .start()
            .redirectOutput()
            .exitProcessOnFailure("[Failed] Check.")
    }

    private fun runIntegrationTests() {
        echo("Integrations tests...")
        gradle("publishAllPublicationsToTestRepository")
            .start()
            .redirectOutput()
            .exitProcessOnFailure("[Failed] Integrations tests.")
        gradle("test")
            .directory(File("test-app"))
            .start()
            .redirectOutput()
            .exitProcessOnFailure("[Failed] Integrations tests.")

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
    private fun Process.exitProcessOnFailure(message: String? = null): Process {
        onExit().whenComplete { process, error ->
            val exitValue = process.exitValue()
            if (exitValue != 0 || error != null) {
                if (message != null) echo("$message ($exitValue)")
                exitProcess(-1)
            }
        }
        return this
    }
}
