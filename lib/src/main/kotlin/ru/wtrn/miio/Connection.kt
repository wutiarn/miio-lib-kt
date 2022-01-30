package ru.wtrn.miio

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.time.Duration
import java.util.concurrent.atomic.AtomicReference

internal class Connection(private val ipAddress: InetAddress) {
    private val activeSocket: AtomicReference<DatagramSocket> = AtomicReference()

    fun sendAndReceive(request: ByteArray): DatagramPacket {
        return withSocket { socket ->
            send(socket, request)
            receive(socket)
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

    private fun receive(socket: DatagramSocket): DatagramPacket {
        val rcv = ByteArray(65507)
        val receivedPacket = DatagramPacket(rcv, rcv.size);
        socket.receive(receivedPacket)
        return receivedPacket
    }

    @Synchronized
    private fun <T> withSocket(action: (DatagramSocket) -> T): T {
        var socket = activeSocket.get()
        if (socket?.isClosed != false) {
            socket = DatagramSocket().also {
                it.soTimeout = Duration.ofSeconds(5).toMillis().toInt()
            }
            activeSocket.set(socket)
        }
        return action(socket)
    }
}

//internal class ConnectionHolder()
