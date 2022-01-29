package ru.wtrn.miio

import java.net.InetAddress

class Device(val ipAddress: InetAddress, val token: Token) {
    fun sendCommand(command: Command) {

    }
}
