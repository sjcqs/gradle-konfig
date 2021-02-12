package fr.sjcqs.transpiler

import java.nio.file.Path

interface FileWriter {
    fun writeTo(outputPath: Path)
    override fun toString(): String
}