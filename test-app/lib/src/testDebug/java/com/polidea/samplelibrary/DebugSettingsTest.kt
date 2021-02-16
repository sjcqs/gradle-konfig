package com.polidea.samplelibrary

import org.junit.Assert
import org.junit.Test

class DebugSettingsTest {
    @Test
    fun `property should be defined`() {
        Assert.assertNotNull(Settings.lib_properties)
    }

    @Test
    fun `lib property should be "Debug value"`() {
        Assert.assertEquals(Settings.lib_properties.lib_property_1, "Debug value")
    }

    @Test
    fun `extra debug properties should be defined`() {
        Assert.assertEquals(Settings.debug.analytics, false)
        Assert.assertEquals(Settings.debug.trace, true)
    }
}
