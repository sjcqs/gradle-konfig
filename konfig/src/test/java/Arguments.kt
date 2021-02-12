package fr.sjcqs.utils

import org.junit.jupiter.params.provider.Arguments

infix fun Arguments.named(name: String): Arguments = Arguments.of(*get(), name)