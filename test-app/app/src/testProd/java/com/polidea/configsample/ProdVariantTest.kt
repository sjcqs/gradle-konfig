package com.polidea.configsample

import org.junit.Assert
import org.junit.Test

class ProdVariantTest {
    @Test
    fun `settings should be initialized`() {
        Assert.assertNotNull(Configuration.backend)
        Assert.assertNotNull(Configuration.debug_credentials)
        Assert.assertNotNull(Configuration.device)
        Assert.assertNotNull(Configuration.districs)
        Assert.assertNotNull(Configuration.properties)
    }

    @Test
    fun `device should match the defined one`() {
        val expectedDevice = Configuration.Device(Configuration.Device.Connect("192.168.1.3", 9876), 30)
        val actualDevice = Configuration.device
        Assert.assertEquals(expectedDevice.connect.ip, actualDevice.connect.ip)
        Assert.assertEquals(expectedDevice.connect.port, actualDevice.connect.port)
        Assert.assertEquals(
            expectedDevice.critical_battery_level,
            actualDevice.critical_battery_level
        )
    }

    @Test
    fun `properties should match the defined ones`() {
        Assert.assertEquals("Production value", Configuration.properties.property_1)
    }

    @Test
    fun `districs should match the defined ones`() {
        Assert.assertArrayEquals(arrayOf("Praga", "Wola"), Configuration.districs.toTypedArray())
    }

    @Test
    fun `backend should match the defined one`() {
        Assert.assertEquals("https://polidea.com", Configuration.backend)
    }

    @Test
    fun `credentials should match the defined ones`() {
        val actualCredentials = Configuration.debug_credentials

        val firstExpectedCredential = Configuration.DebugCredentials("userA", "Kabum1")
        Assert.assertEquals(firstExpectedCredential.name, actualCredentials[0].name)
        Assert.assertEquals(firstExpectedCredential.pass, actualCredentials[0].pass)

        val secondExpectedCredential = Configuration.DebugCredentials("userB", "Kabum2")
        Assert.assertEquals(secondExpectedCredential.name, actualCredentials[1].name)
        Assert.assertEquals(secondExpectedCredential.pass, actualCredentials[1].pass)
    }
}
