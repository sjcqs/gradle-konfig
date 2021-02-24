package fr.sjcqs.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class MapMergerTest {

    private lateinit var merger: MapMerger

    @BeforeEach
    internal fun setUp() {
        merger = MapMerger(NoOpLogger())
    }

    @ParameterizedTest(name = "{3}: {0} and {1} gives {2} ({index})")
    @MethodSource
    internal fun `deep merge two maps should returns the expected map`(
        map0: Map<String, Any>,
        map1: Map<String, Any>,
        expected: Map<String, Any>,
        testName: String
    ) {
        val mergeMap = merger.deepMerge(map0, map1)
        assertEquals(expected, mergeMap)
    }

    @ParameterizedTest(name = "{2}: {0} gives {1} ({index})")
    @MethodSource
    internal fun `deep merge several maps should returns the expected map`(
        maps: List<Map<String, Any>>,
        expected: Map<String, Any>,
        testName: String
    ) {
        val mergeMap = merger.deepMerge(maps)
        assertEquals(expected, mergeMap)
    }

    companion object {
        @JvmStatic
        internal fun `deep merge two maps should returns the expected map`() = Stream.of(
            Arguments.of(
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "value2"
                ),
                mapOf(
                    "key2" to "overrideValue",
                    "key3" to "value3"
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "overrideValue",
                    "key3" to "value3"
                )
            ) named "Replacing value",
            Arguments.of(
                mapOf(
                    "key2" to "overrideValue",
                    "key3" to "value3"
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "value2"
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "value2",
                    "key3" to "value3"
                )
            ) named "Replacing value (order matters)",
            Arguments.of(
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "value2",
                    "key4" to mapOf("key1" to "value1", "key2" to "value2")
                ),
                mapOf(
                    "key2" to "overrideValue",
                    "key3" to "value3",
                    "key4" to mapOf("key1" to "overrideValue1")
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "overrideValue",
                    "key3" to "value3",
                    "key4" to mapOf("key1" to "overrideValue1", "key2" to "value2")
                )
            ) named "Merge sub-maps",
            Arguments.of(
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "value2",
                    "key4" to listOf("value1", "value2", "value3")
                ),
                mapOf(
                    "key2" to "overrideValue",
                    "key3" to "value3",
                    "key4" to listOf("value1", "value2", "value4")
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "overrideValue",
                    "key3" to "value3",
                    "key4" to listOf("value1", "value2", "value3", "value4")
                )
            ) named "Merge lists"
        )

        @JvmStatic
        internal fun `deep merge several maps should returns the expected map`() = Stream.of(
            Arguments.of(
                listOf(
                    mapOf(
                        "key0" to "value0",
                        "key1" to "value1",
                        "key2" to "value2"
                    ),
                    mapOf(
                        "key2" to "overrideValue",
                        "key3" to "value3"
                    )
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "overrideValue",
                    "key3" to "value3"
                )
            ) named "Replacing value",
            Arguments.of(
                listOf(
                    mapOf(
                        "key2" to "overrideValue",
                        "key3" to "value3"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key1" to "value1",
                        "key2" to "value2"
                    )
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "value2",
                    "key3" to "value3"
                )
            ) named "Replacing value",
            Arguments.of(
                listOf(
                    mapOf(
                        "key0" to "value0",
                        "key1" to "value1",
                        "key2" to "value2",
                        "key4" to mapOf("key1" to "value1", "key2" to "value2")
                    ),
                    mapOf(
                        "key2" to "overrideValue",
                        "key3" to "value3",
                        "key4" to mapOf("key1" to "overrideValue1")
                    )
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "overrideValue",
                    "key3" to "value3",
                    "key4" to mapOf("key1" to "overrideValue1", "key2" to "value2")
                )
            ) named "Merge sub-maps",
            Arguments.of(
                listOf(
                    mapOf(
                        "key0" to "value0",
                        "key1" to "value1",
                        "key2" to "value2",
                        "key4" to listOf("value1", "value2", "value3")
                    ),
                    mapOf(
                        "key2" to "overrideValue",
                        "key3" to "value3",
                        "key4" to listOf("value1", "value2", "value4")
                    )
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "value1",
                    "key2" to "overrideValue",
                    "key3" to "value3",
                    "key4" to listOf("value1", "value2", "value3", "value4")
                )
            ) named "Merge lists",
            Arguments.of(
                listOf(
                    mapOf(
                        "key2" to "overrideValue",
                        "key3" to "value3"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key1" to "value1",
                        "key2" to "value2"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key1" to "valueFromThird",
                        "key2" to "value2",
                        "newKey" to "newValue"
                    )
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "valueFromThird",
                    "key2" to "value2",
                    "key3" to "value3",
                    "newKey" to "newValue"
                )
            ) named "Deep merging three maps",
            Arguments.of(
                listOf(
                    mapOf(
                        "key2" to "overrideValue",
                        "key3" to "value3"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key1" to "value1",
                        "key2" to "value2"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key1" to "valueFromThird",
                        "key2" to "value2",
                        "newKey" to "newValue"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key1" to "valueFromFourth",
                        "key2" to "value2",
                        "newKey" to "newValue",
                        "hello" to "world"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key2" to "valueFromFifth",
                        "newKey" to "newValue"
                    ),
                    mapOf(
                        "key0" to "nope",
                        "key1" to "valueFromThird",
                        "newKey" to "newValueFromSixth"
                    )
                ),
                mapOf(
                    "key0" to "nope",
                    "key1" to "valueFromThird",
                    "key2" to "valueFromFifth",
                    "key3" to "value3",
                    "newKey" to "newValueFromSixth",
                    "hello" to "world"
                )
            ) named "Deep merging six maps",
            Arguments.of(
                listOf(
                    mapOf(
                        "key2" to "overrideValue",
                        "key3" to "value3"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key1" to "value1",
                        "key2" to "value2"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key1" to "valueFromThird",
                        "key2" to "value2",
                        "newKey" to "newValue"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key1" to "valueFromFourth",
                        "key2" to "value2",
                        "newKey" to "newValue",
                        "hello" to "world"
                    ),
                    mapOf(
                        "key0" to "nope",
                        "key1" to "valueFromThird",
                        "newKey" to "newValueFromSixth"
                    ),
                    mapOf(
                        "key0" to "value0",
                        "key2" to "valueFromFifth",
                        "newKey" to "newValue"
                    )
                ),
                mapOf(
                    "key0" to "value0",
                    "key1" to "valueFromThird",
                    "key2" to "valueFromFifth",
                    "key3" to "value3",
                    "newKey" to "newValue",
                    "hello" to "world"
                )
            ) named "Deep merging six maps (order matters)"
        )
    }
}
