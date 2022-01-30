package ru.wtrn.miio

import java.net.DatagramPacket
import java.net.InetAddress

class Device(val ipAddress: InetAddress, val token: Token) {
    private val connection = Connection(ipAddress)

    fun sendCommand(command: Command) {

    }

    private fun handshake() {
        val packet = Packet.HELLO
        val datagramPacket = DatagramPacket(
            packet,
            packet.size,
            ipAddress,
            54321
        )

    }
}
