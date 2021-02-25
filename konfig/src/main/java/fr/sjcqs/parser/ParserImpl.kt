package fr.sjcqs.parser

import fr.sjcqs.ast.KonfigMap
import fr.sjcqs.ast.Token
import fr.sjcqs.utils.Logger
import java.util.Date

@Suppress("UNCHECKED_CAST")
internal class ParserImpl(private val logger: Logger) : Parser {

    override fun parse(configMap: KonfigMap): Token.Root {
        logger.i("Parsing the config map")
        val settings = configMap.map { (key, value) ->
            parse(key, value)
        }
        return Token.Root(settings)
    }

    private fun parse(key: String, value: Any?): Token {
        return when (value) {
            is Boolean,
            is Byte,
            is Short,
            is Int,
            is Long,
            is Float,
            is Double,
            is String,
            is Date -> buildField(value, key)
            is Map<*, *> -> buildClass(value, key)
            // TODO (2019-08-14) satyanjacquens: consider creating a set to ensure uniqueness
            is Set<*> -> parseList(key, value.toList())
            is List<*> -> parseList(key, value)
            is Array<*> -> parseArray(key, value as Array<Any?>)
            else -> throw UnsupportedTypeException(key)
        }
    }

    private fun buildField(value: Any?, key: String): Token.Field<Any> {
        val field = value as Any
        return Token.Field(key, field, field::class)
    }

    private fun parseArray(key: String, array: Array<Any?>): Token {
        return if (array.size == 2) {
            val (elementKey, value) = array
            Token.Class(
                "${key}_$elementKey",
                listOf(parse(elementKey as String, value))
            )
        } else {
            parseList(key, array.asList())
        }
    }

    private fun buildClass(value: Any?, key: String): Token.Class {
        val map = value as KonfigMap
        val entries = map.map { (key, value) -> parse(key, value) }
        return Token.Class(key = key, settings = entries)
    }

    private fun parseList(key: String, list: List<Any?>): Token.KList {
        val elements = list.map { item ->
            parse(key, item)
        }
        val last = elements.reduce { previous, current ->
            if (!verifyListElements(previous, current)) {
                throw UnsupportedMixedTypes(key)
            }
            current
        }
        val settings = elements.map { element ->
            when (element) {
                is Token.Class -> element.copy(key = last.key)
                else -> element
            }
        }
        return Token.KList(key = key, settings = settings)
    }

    private fun verifyListElements(
        element: Token,
        expected: Token
    ): Boolean {
        if (element::class != expected::class) {
            return false
        }
        return when (element) {
            is Token.Field<*> -> {
                val field = expected as Token.Field<*>
                element.kClass == field.kClass
            }
            is Token.Class -> {
                val expectedKeys = (expected as Token.Class).settings.map(Token::key)
                val keys = element.settings.map(Token::key)
                expectedKeys.union(keys).size == expectedKeys.size
            }
            is Token.Root -> true
            is Token.KList -> true
        }
    }
}
