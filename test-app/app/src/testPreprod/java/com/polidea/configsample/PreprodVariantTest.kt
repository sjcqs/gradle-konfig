package com.polidea.configsample

import org.junit.Assert
import org.junit.Test

class PreprodVariantTest {

    @Test
    fun `custom preprod value should match the defined one`() {
        Assert.assertEquals("hello world", Settings.custom_preprod)
    }

    @Test
    fun `settings should be initialized`() {
        Assert.assertNotNull(Settings.backend)
        Assert.assertNotNull(Settings.debug_credentials)
        Assert.assertNotNull(Settings.device)
        Assert.assertNotNull(Settings.districs)
        Assert.assertNotNull(Settings.properties)
    }

    @Test
    fun `device should match the defined one`() {
        val expectedDevice = Settings.Device(Settings.Device.Connect("192.168.1.2", 9876), 5)
        val actualDevice = Settings.device
        Assert.assertEquals(expectedDevice.connect.ip, actualDevice.connect.ip)
        Assert.assertEquals(expectedDevice.connect.port, actualDevice.connect.port)
        Assert.assertEquals(
            expectedDevice.critical_battery_level,
            actualDevice.critical_battery_level
        )
    }

    @Test
    fun `properties should match the defined ones`() {
        Assert.assertEquals("Default value", Settings.properties.property_1)
    }

    @Test
    fun `districs should match the defined ones`() {
        Assert.assertArrayEquals(arrayOf("Praga", "Wola"), Settings.districs.toTypedArray())
    }

    @Test
    fun `backend should match the defined one`() {
        Assert.assertEquals("https://uat.polidea.com", Settings.backend)
    }

    @Test
    fun `credentials should match the defined ones`() {
        val actualCredentials = Settings.debug_credentials

        val firstExpectedCredential = Settings.DebugCredentials("userA", "Kabum1")
        Assert.assertEquals(firstExpectedCredential.name, actualCredentials[0].name)
        Assert.assertEquals(firstExpectedCredential.pass, actualCredentials[0].pass)

        val secondExpectedCredential = Settings.DebugCredentials("userB", "Kabum2")
        Assert.assertEquals(secondExpectedCredential.name, actualCredentials[1].name)
        Assert.assertEquals(secondExpectedCredential.pass, actualCredentials[1].pass)
    }
}
