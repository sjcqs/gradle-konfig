package com.polidea.samplelibrary

import org.junit.Assert
import org.junit.Test

class DebugConfigurationTest {
    @Test
    fun `property should be defined`() {
        Assert.assertNotNull(Configuration.lib_properties)
    }

    @Test
    fun `lib property should be "Debug value"`() {
        Assert.assertEquals(Configuration.lib_properties.lib_property_1, "Debug value")
    }

    @Test
    fun `extra debug properties should be defined`() {
        Assert.assertEquals(Configuration.debug.analytics, false)
        Assert.assertEquals(Configuration.debug.trace, true)
    }
}
