package fr.sjcqs.utils

import org.gradle.api.NamedDomainObjectContainer

operator fun <T> NamedDomainObjectContainer<T>.get(name: String): T = getByName(name)
