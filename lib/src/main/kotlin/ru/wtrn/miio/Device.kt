package ru.wtrn.miio

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import java.net.InetAddress
import java.net.SocketTimeoutException

class Device(val ipAddress: String, val token: Token) {
    private val connectionHolder = ConnectionHolder(Connection(InetAddress.getByName(ipAddress)))

    fun sendCommand(command: Command): String? {
        return connectionHolder.use { connection ->
            val handshakeResponse = handshake(connection)

            val commandJsonNode = objectMapper.valueToTree<ObjectNode>(command)
            val requestMiIoTimestamp = handshakeResponse.miioTimestamp + 1
            commandJsonNode.put("id", requestMiIoTimestamp)
            val payload = commandJsonNode.toString()

            val commandPacket = Packet.encodePacket(
                deviceId = handshakeResponse.deviceId,
                miioTimestamp = requestMiIoTimestamp,
                payload = payload,
                token = token
            )
            val received = connection.sendAndReceive(commandPacket)
            Packet.decodePacket(received, token).payload
        }
    }

    private fun handshake(connection: Connection): Packet {
        val received = try {
            connection.sendAndReceive(Packet.HELLO)
        } catch (e: SocketTimeoutException) {
            throw MiIoRequestTimeoutException("Hello handshake timeout", e)
        }
        return Packet.decodePacket(received, token)
    }

    companion object {
        private val objectMapper = ObjectMapper()
    }
}
