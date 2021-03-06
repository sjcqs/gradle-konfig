package com.polidea.samplelibrary

import org.junit.Assert
import org.junit.Test

class ConfigurationTest {
    @Test
    fun `property should be defined`() {
        Assert.assertNotNull(Configuration.lib_properties)
    }

    @Test
    fun `on debug build config, lib property should be "Debug value"`() {
        if (BuildConfig.DEBUG) {
            Assert.assertEquals(Configuration.lib_properties.lib_property_1, "Debug value")
        }
    }

    @Test
    fun `on non-debug build config, lib property should be "Default value"`() {
        if (!BuildConfig.DEBUG) {
            Assert.assertEquals(Configuration.lib_properties.lib_property_1, "Default value")
        }
    }
}
