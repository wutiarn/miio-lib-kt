package ru.wtrn.miio

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class DeviceTest {

    @Test
    @Disabled
    fun sendCommand() {
        val token = Token("259b3cfe0f1763d5f5b04e60000bd705")
        val device = Device(
            ipAddress = "192.168.2.246",
            token = token
        )
        val command = Command(
            method = "set_power",
            params = listOf("on", "smooth", 0)
        )
        device.sendCommand(command)
    }
}
