package fr.sjcqs.transpiler.kotlin


import fr.sjcqs.ast.Token
import fr.sjcqs.utils.NoOpLogger
import java.util.Date
import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class KotlinFileTranspilerTest {
    private lateinit var compilerKotlinPoet: KotlinFileTranspiler

    @BeforeEach
    internal fun setUp() {
        compilerKotlinPoet = KotlinFileTranspiler(NoOpLogger())
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource
    internal fun `config should create the expected file`(element: Token.Root, expected: String) {
        val fileSpec = compilerKotlinPoet.transpile(root = element,)
        val actual = fileSpec.toString()
        assertEquals(expected, actual)
    }

    companion object {
        private val EMPTY_ELEMENT = Token.Root(emptyList())
        private val EMPTY_FILE =
            """
            |public object Settings
            |
            """.trimMargin()
        private val SIMPLE_ELEMENT = Token.Root(
            listOf(
                Token.Field("key0", 0, Int::class),
                Token.Field("key1", "value1", String::class),
                Token.Field("key2", Date(1559952000000L), Date::class),
                Token.Field("key3", false, Boolean::class)
            )
        )
        private val SIMPLE_FILE =
            """
            |import java.util.Date
            |import kotlin.Boolean
            |import kotlin.Int
            |import kotlin.String
            |import kotlin.jvm.JvmField
            |
            |public object Settings {
            |  public const val key0: Int = 0
            |
            |  public const val key1: String = "value1"
            |
            |  @JvmField
            |  public val key2: Date = Date(1559952000000L)
            |
            |  public const val key3: Boolean = false
            |}
            |
            """.trimMargin()
        private val LIST_ELEMENT = Token.Root(
            listOf(
                Token.KList(
                    "ints", listOf(
                        Token.Field("key0", 1, Int::class),
                        Token.Field("key0", 2, Int::class),
                        Token.Field("key0", 3, Int::class)
                    )
                ),
                Token.KList(
                    "strings", listOf(
                        Token.Field("key1", "value0", String::class),
                        Token.Field("key1", "value1", String::class),
                        Token.Field("key1", "value2", String::class),
                        Token.Field("key1", "value3", String::class)
                    )
                )
            )
        )
        private val LIST_FILE =
            """
            |import kotlin.Int
            |import kotlin.String
            |import kotlin.collections.List
            |import kotlin.jvm.JvmField
            |
            |public object Settings {
            |  @JvmField
            |  public val ints: List<Int> = listOf(1, 2, 3)
            |
            |  @JvmField
            |  public val strings: List<String> = listOf("value0", "value1", "value2", "value3")
            |}
            |
            """.trimMargin()
        private val MAP_LIST_ELEMENT = Token.Root(
            listOf(
                Token.KList(
                    "mapList", listOf(
                        Token.Class("mapList", listOf(Token.Field("key", "value0", String::class))),
                        Token.Class("mapList", listOf(Token.Field("key", "value1", String::class))),
                        Token.Class("mapList", listOf(Token.Field("key", "value2", String::class))),
                        Token.Class("mapList", listOf(Token.Field("key", "value3", String::class)))
                    )
                )
            )
        )
        private val MAP_LIST_FILE =
            """
            |import kotlin.String
            |import kotlin.collections.List
            |import kotlin.jvm.JvmField
            |
            |public object Settings {
            |  @JvmField
            |  public val mapList: List<MapList> = listOf(MapList("value0"), MapList("value1"),
            |      MapList("value2"), MapList("value3"))
            |
            |  public data class MapList(
            |    @JvmField
            |    public val key: String
            |  )
            |}
            |
            """.trimMargin()
        private val MAP_ELEMENT = Token.Root(
            listOf(
                Token.Class(
                    "root0", listOf(
                        Token.Class(
                            "value0", listOf(
                                Token.Field("subKey1", "value", String::class),
                                Token.Field("subKey2", "value", String::class)
                            )
                        ),
                        Token.Class(
                            "value1", listOf(
                                Token.Field("subKey1", "value", String::class),
                                Token.Field("subKey2", "value", String::class)
                            )
                        ),
                        Token.Class(
                            "value2", listOf(
                                Token.Field("subKey1", "value", String::class),
                                Token.Field("subKey2", "value", String::class)
                            )
                        ),
                        Token.Class(
                            "value3", listOf(
                                Token.Field("subKey1", "value", String::class),
                                Token.Field("subKey2", "value", String::class)
                            )
                        )
                    )
                ),
                Token.Class(
                    "root1", listOf(
                        Token.Class(
                            "value0", listOf(
                                Token.Field("subKey1", 1, Int::class),
                                Token.Field("subKey2", 2, Int::class)
                            )
                        ),
                        Token.Class(
                            "value1", listOf(
                                Token.Field("subKey1", 1, Int::class),
                                Token.Field("subKey2", 2, Int::class)
                            )
                        ),
                        Token.Class(
                            "value2", listOf(
                                Token.Field("subKey1", 1, Int::class),
                                Token.Field("subKey2", 2, Int::class)
                            )
                        ),
                        Token.Class(
                            "value3", listOf(
                                Token.Field("subKey1", 1, Int::class),
                                Token.Field("subKey2", 2, Int::class)
                            )
                        )
                    )
                )
            )
        )
        private val MAP_FILE =
            """
            |import Settings.Root0.Value0
            |import Settings.Root0.Value1
            |import Settings.Root0.Value2
            |import Settings.Root0.Value3
            |import kotlin.Int
            |import kotlin.String
            |import kotlin.jvm.JvmField
            |
            |public object Settings {
            |  @JvmField
            |  public val root0: Root0 = Root0(Value0("value", "value"), Value1("value", "value"),
            |      Value2("value", "value"), Value3("value", "value"))
            |
            |  @JvmField
            |  public val root1: Root1 = Root1(Settings.Root1.Value0(1, 2), Settings.Root1.Value1(1, 2),
            |      Settings.Root1.Value2(1, 2), Settings.Root1.Value3(1, 2))
            |
            |  public data class Root0(
            |    @JvmField
            |    public val value0: Value0,
            |    @JvmField
            |    public val value1: Value1,
            |    @JvmField
            |    public val value2: Value2,
            |    @JvmField
            |    public val value3: Value3
            |  ) {
            |    public data class Value0(
            |      @JvmField
            |      public val subKey1: String,
            |      @JvmField
            |      public val subKey2: String
            |    )
            |
            |    public data class Value1(
            |      @JvmField
            |      public val subKey1: String,
            |      @JvmField
            |      public val subKey2: String
            |    )
            |
            |    public data class Value2(
            |      @JvmField
            |      public val subKey1: String,
            |      @JvmField
            |      public val subKey2: String
            |    )
            |
            |    public data class Value3(
            |      @JvmField
            |      public val subKey1: String,
            |      @JvmField
            |      public val subKey2: String
            |    )
            |  }
            |
            |  public data class Root1(
            |    @JvmField
            |    public val value0: Value0,
            |    @JvmField
            |    public val value1: Value1,
            |    @JvmField
            |    public val value2: Value2,
            |    @JvmField
            |    public val value3: Value3
            |  ) {
            |    public data class Value0(
            |      @JvmField
            |      public val subKey1: Int,
            |      @JvmField
            |      public val subKey2: Int
            |    )
            |
            |    public data class Value1(
            |      @JvmField
            |      public val subKey1: Int,
            |      @JvmField
            |      public val subKey2: Int
            |    )
            |
            |    public data class Value2(
            |      @JvmField
            |      public val subKey1: Int,
            |      @JvmField
            |      public val subKey2: Int
            |    )
            |
            |    public data class Value3(
            |      @JvmField
            |      public val subKey1: Int,
            |      @JvmField
            |      public val subKey2: Int
            |    )
            |  }
            |}
            |
            """.trimMargin()
        private val PAIR_ELEMENT = Token.Root(
            listOf(
                Token.KList(
                    "pairs",
                    listOf(
                        Token.Class(
                            "pairs_meeting", listOf(
                                Token.Field("meeting", "with team.", String::class)
                            )
                        ),
                        Token.Class(
                            "pairs_meeting", listOf(
                                Token.Field("meeting", "with boss.", String::class)
                            )
                        ),
                        Token.Class(
                            "pairs_meeting", listOf(
                                Token.Field("meeting", "with client.", String::class)
                            )
                        )
                    )
                )
            )
        )
        private val PAIR_FILE =
            """
            |import kotlin.String
            |import kotlin.collections.List
            |import kotlin.jvm.JvmField
            |
            |public object Settings {
            |  @JvmField
            |  public val pairs: List<PairsMeeting> = listOf(PairsMeeting("with team."),
            |      PairsMeeting("with boss."), PairsMeeting("with client."))
            |
            |  public data class PairsMeeting(
            |    @JvmField
            |    public val meeting: String
            |  )
            |}
            |
            """.trimMargin()

        @JvmStatic
        internal fun `config should create the expected file`() = Stream.of(
            Arguments.of(EMPTY_ELEMENT, EMPTY_FILE),
            Arguments.of(SIMPLE_ELEMENT, SIMPLE_FILE),
            Arguments.of(LIST_ELEMENT, LIST_FILE),
            Arguments.of(MAP_LIST_ELEMENT, MAP_LIST_FILE),
            Arguments.of(MAP_ELEMENT, MAP_FILE),
            Arguments.of(PAIR_ELEMENT, PAIR_FILE)
        )
    }
}