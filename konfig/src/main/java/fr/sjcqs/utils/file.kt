package fr.sjcqs.utils

import java.io.File

fun pathJoin(vararg path: String) = path.joinToString(File.separator)

fun String.packageToPath() = replace('.', '/')