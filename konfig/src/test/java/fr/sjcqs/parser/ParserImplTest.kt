package fr.sjcqs.parser

import fr.sjcqs.ast.KonfigMap
import fr.sjcqs.ast.Token
import fr.sjcqs.utils.NoOpLogger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.yaml.snakeyaml.Yaml
import java.util.Date
import java.util.stream.Stream

internal class ParserImplTest {
    private lateinit var parser: ParserImpl

    /* TODO (2019-08-14) satyanjacquens: remove test dependencies to yaml.
        parser is only suppose to parse map, yaml file is only an input.
    */
    private val yaml: Yaml = Yaml()

    @BeforeEach
    fun setUp() {
        parser = ParserImpl(NoOpLogger())
    }

    @ParameterizedTest(name = "using input: \"{0}\" should yield to {1} ({index})")
    @MethodSource
    internal fun `config file generate the correct element`(
        input: String,
        expected: Token
    ) {
        val output = parser.parse(input.load())
        assertEquals(expected, output)
    }

    @ParameterizedTest
    @MethodSource
    internal fun `mixed types list in config should yield to an exception`(input: String) {
        org.junit.jupiter.api.assertThrows<UnsupportedMixedTypes> {
            parser.parse(input.load())
        }
    }

    private fun String.load(): KonfigMap =
        yaml.loadAs(this, mutableMapOf<String, Any?>()::class.java) ?: emptyMap()

    companion object {
        private const val EMPTY_CONFIG = ""
        private val EMPTY_ELEMENT = Token.Root(emptyList())
        private const val SIMPLE_CONFIG = """
            key0: 0
            key1: value1
            key2: 2019-06-08
            key3: false
        """
        private val SIMPLE_ELEMENT = Token.Root(
            listOf(
                Token.Field("key0", 0, Int::class),
                Token.Field("key1", "value1", String::class),
                Token.Field("key2", Date(1559952000000L), Date::class),
                Token.Field("key3", false, Boolean::class)
            )
        )
        private const val LIST_CONFIG = """
            key0:
                - 1
                - 2
                - 3
            key1:
                - value0
                - value1
                - value2
                - value3
            key2:
                - key: value0
                - key: value1
                - key: value2
                - key: value3
        """
        private val LIST_ELEMENT = Token.Root(
            listOf(
                Token.KList(
                    "key0",
                    listOf(
                        Token.Field("key0", 1, Int::class),
                        Token.Field("key0", 2, Int::class),
                        Token.Field("key0", 3, Int::class)
                    )
                ),
                Token.KList(
                    "key1",
                    listOf(
                        Token.Field("key1", "value0", String::class),
                        Token.Field("key1", "value1", String::class),
                        Token.Field("key1", "value2", String::class),
                        Token.Field("key1", "value3", String::class)
                    )
                ),
                Token.KList(
                    "key2",
                    listOf(
                        Token.Class("key2", listOf(Token.Field("key", "value0", String::class))),
                        Token.Class("key2", listOf(Token.Field("key", "value1", String::class))),
                        Token.Class("key2", listOf(Token.Field("key", "value2", String::class))),
                        Token.Class("key2", listOf(Token.Field("key", "value3", String::class)))
                    )
                )
            )
        )
        private const val MAP_CONFIG = """
            root0:
                value0:
                    subKey1: value
                    subKey2: value
                value1:
                    subKey1: value
                    subKey2: value
                value2:
                    subKey1: value
                    subKey2: value
                value3:
                    subKey1: value
                    subKey2: value
            root1:
                value0:
                    subKey1: 1
                    subKey2: 2
                value1:
                    subKey1: 1
                    subKey2: 2
                value2:
                    subKey1: 1
                    subKey2: 2
                value3:
                    subKey1: 1
                    subKey2: 2
        """
        private val MAP_ELEMENT = Token.Root(
            listOf(
                Token.Class(
                    "root0",
                    listOf(
                        Token.Class(
                            "value0",
                            listOf(
                                Token.Field("subKey1", "value", String::class),
                                Token.Field("subKey2", "value", String::class)
                            )
                        ),
                        Token.Class(
                            "value1",
                            listOf(
                                Token.Field("subKey1", "value", String::class),
                                Token.Field("subKey2", "value", String::class)
                            )
                        ),
                        Token.Class(
                            "value2",
                            listOf(
                                Token.Field("subKey1", "value", String::class),
                                Token.Field("subKey2", "value", String::class)
                            )
                        ),
                        Token.Class(
                            "value3",
                            listOf(
                                Token.Field("subKey1", "value", String::class),
                                Token.Field("subKey2", "value", String::class)
                            )
                        )
                    )
                ),
                Token.Class(
                    "root1",
                    listOf(
                        Token.Class(
                            "value0",
                            listOf(
                                Token.Field("subKey1", 1, Int::class),
                                Token.Field("subKey2", 2, Int::class)
                            )
                        ),
                        Token.Class(
                            "value1",
                            listOf(
                                Token.Field("subKey1", 1, Int::class),
                                Token.Field("subKey2", 2, Int::class)
                            )
                        ),
                        Token.Class(
                            "value2",
                            listOf(
                                Token.Field("subKey1", 1, Int::class),
                                Token.Field("subKey2", 2, Int::class)
                            )
                        ),
                        Token.Class(
                            "value3",
                            listOf(
                                Token.Field("subKey1", 1, Int::class),
                                Token.Field("subKey2", 2, Int::class)
                            )
                        )
                    )
                )
            )
        )

        private const val PAIR_CONFIG: String = """
            pairs: !!pairs
                - meeting: with team.
                - meeting: with boss.
                - meeting: with client.
        """
        private val PAIR_ELEMENT = Token.Root(
            listOf(
                Token.KList(
                    "pairs",
                    listOf(
                        Token.Class(
                            "pairs_meeting",
                            listOf(
                                Token.Field("meeting", "with team.", String::class)
                            )
                        ),
                        Token.Class(
                            "pairs_meeting",
                            listOf(
                                Token.Field("meeting", "with boss.", String::class)
                            )
                        ),
                        Token.Class(
                            "pairs_meeting",
                            listOf(
                                Token.Field("meeting", "with client.", String::class)
                            )
                        )
                    )
                )
            )
        )

        private const val MIXED_TYPES_CONFIG: String = """
            list:
                - 1
                - 1.0
                - value
        """
        private const val MIXED_MAPS_CONFIG: String = """
            list:
                -   key0: value
                    key1: value
                -   key0: value
                -   key0: value
        """

        @JvmStatic
        internal fun `config file generate the correct element`() = Stream.of(
            Arguments.of(EMPTY_CONFIG, EMPTY_ELEMENT),
            Arguments.of(SIMPLE_CONFIG, SIMPLE_ELEMENT),
            Arguments.of(LIST_CONFIG, LIST_ELEMENT),
            Arguments.of(MAP_CONFIG, MAP_ELEMENT),
            Arguments.of(PAIR_CONFIG, PAIR_ELEMENT)
        )

        @JvmStatic
        internal fun `mixed types list in config should yield to an exception`() = Stream.of(
            MIXED_TYPES_CONFIG,
            MIXED_MAPS_CONFIG
        )
    }
}
