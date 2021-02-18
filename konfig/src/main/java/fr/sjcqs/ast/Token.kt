package fr.sjcqs.ast

import kotlin.reflect.KClass

typealias KonfigMap = Map<String, Any?>

sealed class Token {
    abstract val key: String

    data class Root(
        val settings: List<Token>
    ) : Token() {
        override val key: String = ROOT_KEY

        companion object {
            const val ROOT_KEY = "Configuration"
        }
    }

    data class KList(
        override val key: String,
        val settings: List<Token>
    ) : Token()

    data class Class(
        override val key: String,
        val settings: List<Token>
    ) : Token()

    data class Field<out T : Any>(
        override val key: String,
        val value: T?,
        val kClass: KClass<out T>
    ) : Token() {
        companion object {
            val CONSTANT_TYPES = arrayOf(
                Boolean::class,
                Byte::class,
                Short::class,
                Int::class,
                Long::class,
                Float::class,
                Double::class,
                String::class
            )
        }
    }
}


