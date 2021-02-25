#!/usr/bin/env -S kotlinc -script --

@file:DependsOn("com.github.ajalt:clikt:2.8.0")

import com.github.ajalt.clikt.core.CliktCommand

Delivery.main(args)

object Delivery : CliktCommand(name = "delivery") {

    override fun run() {
        gradle("assemble").start().redirectOutput()
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
        gradle("publishPlugins", *params).start().redirectOutput()
    }


    private const val GRADLE_EXEC = "./gradlew"
    private fun gradle(task: String, vararg params: String): ProcessBuilder {
        return ProcessBuilder().command(GRADLE_EXEC, task, "--console=rich", *params)
    }

    private fun Process.redirectOutput(): Process {
        inputStream.copyTo(System.out)
        errorStream.copyTo(System.err)
        return this
    }
}
