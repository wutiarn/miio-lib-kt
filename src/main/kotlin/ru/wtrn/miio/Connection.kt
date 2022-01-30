package ru.wtrn.miio

import java.io.Closeable
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.time.Duration

internal class Connection private constructor(private val ipAddress: InetAddress) : Closeable {
    private val socket = DatagramSocket().also {
        it.soTimeout = Duration.ofSeconds(5).toMillis().toInt()
    }

    fun sendAndReceive(request: ByteArray): ByteArray {
        send(socket, request)
        return receive(socket).data
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

    override fun close() {
        socket.close()
    }

    class ReceivedPacket(
        val data: ByteArray,
        val srcAddress: InetAddress
    )

    internal class Factory(private val ipAddress: InetAddress) {
        @Synchronized
        fun <T> withConnection(action: (Connection) -> T): T {
            val connection = Connection(ipAddress)
            return connection.use(action)
        }
    }
}
