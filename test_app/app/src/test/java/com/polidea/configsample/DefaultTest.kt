package com.polidea.configsample

import org.junit.Assert
import org.junit.Test

class DefaultTest {
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
        val expectedDevice = Settings.Device(Settings.DeviceConnect("192.168.1.2", 9876), -1)
        val actualDevice = Settings.device
        Assert.assertEquals(expectedDevice.connect.ip, actualDevice.connect.ip)
        Assert.assertEquals(expectedDevice.connect.port, actualDevice.connect.port)
    }

    @Test
    fun `districs should match the defined ones`() {
        Assert.assertArrayEquals(arrayOf("Praga", "Wola"), Settings.districs.toArray())
    }

    @Test
    fun `credentials should match the defined ones`() {
        val actualCredentials = Settings.debug_credentials

        val firstExpectedCredential = Settings.DebugCredentialsElement("userA", "Kabum1")
        Assert.assertEquals(firstExpectedCredential.name, actualCredentials[0].name)
        Assert.assertEquals(firstExpectedCredential.pass, actualCredentials[0].pass)

        val secondExpectedCredential = Settings.DebugCredentialsElement("userB", "Kabum2")
        Assert.assertEquals(secondExpectedCredential.name, actualCredentials[1].name)
        Assert.assertEquals(secondExpectedCredential.pass, actualCredentials[1].pass)
    }
}
