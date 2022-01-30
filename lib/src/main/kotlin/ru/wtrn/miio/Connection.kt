package ru.wtrn.miio

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.time.Duration

internal class Connection(private val ipAddress: InetAddress) {
    fun sendAndReceive(request: ByteArray): ByteArray {
        return withSocket { socket ->
            send(socket, request)
            receive(socket).data
        }
    }

    private fun send(socket: DatagramSocket, request: ByteArray) {
        val datagramPacket = DatagramPacket(
            request,
            request.size,
            ipAddress,
            54321
        )
        socket.send(datagramPacket)
    }

    private fun receive(socket: DatagramSocket): ReceivedPacket {
        val rcv = ByteArray(65507)
        val receivedPacket = DatagramPacket(rcv, rcv.size);
        socket.receive(receivedPacket)
        return ReceivedPacket(
            data = rcv.copyOfRange(0, receivedPacket.length),
            srcAddress = receivedPacket.address
        )
    }

    @Synchronized
    private fun <T> withSocket(action: (DatagramSocket) -> T): T {
        val socket = DatagramSocket().also {
            it.soTimeout = Duration.ofSeconds(5).toMillis().toInt()
        }
        return socket.use(action)
    }

    class ReceivedPacket(
        val data: ByteArray,
        val srcAddress: InetAddress
    )
}

internal class ConnectionHolder(private val connection: Connection) {
    @Synchronized
    fun <T> use(action: (Connection) -> T): T {
        return action(connection)
    }
}
