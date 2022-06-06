package fr.sjcqs.utils

import fr.sjcqs.ast.KonfigMap

class MapMerger(private val logger: Logger) {

    fun deepMerge(maps: List<Map<String, Any?>>): KonfigMap {
        return when (maps.size) {
            0 -> emptyMap()
            1 -> maps[0]
            2 -> deepMerge(maps[0], maps[1])
            else -> {
                maps.reduce { onto, map ->
                    this.deepMerge(onto, map)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun deepMerge(onto: KonfigMap, otherMap: KonfigMap): KonfigMap {
        val map = onto.toMutableMap()
        otherMap.forEach { (key, otherValue) ->
            val value = map[key]
            if (value != null) {
                if (value is Map<*, *> && otherValue is Map<*, *>) {
                    logger.d("$key: merging two maps $value and $otherValue")
                    map[key] = deepMerge((value as KonfigMap), otherValue as KonfigMap)
                } else if (value is List<*> && otherValue is List<*>) {
                    logger.d("$key: merging two list $value with $otherValue")
                    map[key] = value.merge(otherValue)
                } else {
                    logger.d("$key: overriding $value with $otherValue")
                    map[key] = otherValue
                }
            } else {
                logger.d("$key: adding a new value $otherValue")
                map[key] = otherValue
            }
        }
        return map
    }

    private inline fun <reified A> List<A>.merge(otherList: List<A>): List<A> {
        return plus(otherList.minus(this))
    }
}
