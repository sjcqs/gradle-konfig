package fr.sjcqs.parser

import fr.sjcqs.ast.KonfigMap
import fr.sjcqs.ast.Token

interface Parser {
    fun parse(configMap: KonfigMap): Token.Root
}
