package fr.sjcqs.di

import fr.sjcqs.parser.Parser
import fr.sjcqs.parser.ParserImpl
import fr.sjcqs.task.ConfigurationGenerationTask
import fr.sjcqs.transpiler.FileTranspiler
import fr.sjcqs.transpiler.kotlin.KotlinFileTranspiler
import fr.sjcqs.utils.GradleLogger
import fr.sjcqs.utils.Logger
import fr.sjcqs.utils.MapMerger
import org.yaml.snakeyaml.Yaml

object DependencyContainer {
    val taskLogger: Logger = GradleLogger<ConfigurationGenerationTask>()
    val yaml = Yaml()
    val transpiler: FileTranspiler = KotlinFileTranspiler(GradleLogger<FileTranspiler>())
    val parser: Parser = ParserImpl(GradleLogger<Parser>())
    val mapMerger: MapMerger = MapMerger(GradleLogger<MapMerger>())
}
