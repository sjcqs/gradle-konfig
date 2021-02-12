package fr.sjcqs.transpiler.kotlin

import com.squareup.kotlinpoet.FileSpec
import fr.sjcqs.transpiler.FileWriter
import java.nio.file.Path

class KotlinFileWriter(private val fileSpec: FileSpec) : FileWriter {
    override fun writeTo(outputPath: Path) {
        fileSpec.writeTo(outputPath)
    }

    override fun toString() = fileSpec.toString()

}