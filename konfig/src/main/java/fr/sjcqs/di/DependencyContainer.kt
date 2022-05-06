package fr.sjcqs.di

import fr.sjcqs.parser.Parser
import fr.sjcqs.parser.ParserImpl
import fr.sjcqs.transpiler.FileTranspiler
import fr.sjcqs.transpiler.kotlin.KotlinFileTranspiler
import fr.sjcqs.utils.GradleLogger
import fr.sjcqs.utils.Logger
import fr.sjcqs.utils.MapMerger
import org.yaml.snakeyaml.Yaml

object DependencyContainer {
    val logger: Logger get() = GradleLogger()
    val yaml get() = Yaml()
    val transpiler: FileTranspiler get() = KotlinFileTranspiler(logger)
    val parser: Parser get() = ParserImpl(logger)
    val mapMerger: MapMerger get() = MapMerger(logger)
}
