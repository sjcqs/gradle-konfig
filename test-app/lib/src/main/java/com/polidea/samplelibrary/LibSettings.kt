package com.polidea.samplelibrary

class LibConfiguration {

    val buildVariant: String
        get() = BuildConfig.BUILD_TYPE

    val propertyValue: String
        get() = Configuration.lib_properties.lib_property_1
}
