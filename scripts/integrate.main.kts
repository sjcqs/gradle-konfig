#!/usr/bin/env -S kotlinc -script --

@file:CompilerOptions("-jvm-target", "1.8")
@file:DependsOn("com.github.ajalt:clikt:2.8.0")

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
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

    private val force by option("--force", "-f", help = "Force refresh")
        .flag(default = false)

    private val skipIntegrationTests by option(
        "--skip-integration-test",
        help = "Skip integration tests"
    ).flag(default = false)

    private val androidPluginVersion by option(
        "--android-plugin-version",
        help = "Test with the latest android gradle plugin version."
    )

    override fun run() {

        if (androidPluginVersion != null) {
            printUsedAndroidPluginVersion()
        }

        if (!skipBuild) {
            echo("Building...")
            runBuild()
        }
        if (!skipLint) {
            echo("Lint...")
            runLint()
        }

        if (!skipChecks) {
            echo("Checks..")
            runChecks()
        }

        if (!skipIntegrationTests) {
            echo("Integrations tests...")
            runIntegrationTests()
        }
    }

    private fun printUsedAndroidPluginVersion() {
        val process = gradle(
            "konfig:dependencyInsight",
            "--configuration=compileClasspath",
            "--dependency=com.android.tools.build:gradle",
            "--quiet"
        ).start().exitProcessOnFailure("[Failed] Latest Android Gradle Plugin version.")

        process.inputStream.bufferedReader().useLines { lines ->
            echo(lines.first())
        }
    }

    private fun runBuild() {
        gradle("assemble")
            .start()
            .redirectOutput()
            .exitProcessOnFailure("[Failed] Building.")
    }

    private fun runLint() {
        gradle("spotlessCheck")
            .start()
            .redirectOutput()
            .exitProcessOnFailure("[Failed] Lint.")
    }

    private fun runChecks() {
        gradle("check")
            .start()
            .redirectOutput()
            .exitProcessOnFailure("[Failed] Check.")
    }

    private fun runIntegrationTests() {
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
        val commands: List<String> = listOfNotNull(
            GRADLE_EXEC,
            task,
            "-PandroidBuildToolsVersion=$androidPluginVersion".takeIf { androidPluginVersion != null },
            "--rerun-tasks".takeIf { force },
        ) + params
        return ProcessBuilder().command(commands)
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
