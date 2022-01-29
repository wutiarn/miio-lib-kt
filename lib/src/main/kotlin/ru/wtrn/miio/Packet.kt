package ru.wtrn.miio

import java.nio.ByteBuffer

data class Packet(
    val deviceId: Int,
    val miioTimestamp: Int,
    val payload: String?
) {
    companion object {
        internal const val MAGIC = 0x2131.toShort()
        internal val HELLO = ByteBuffer.allocate(32)
            .putShort(MAGIC)
            .putShort(32.toShort())
            .put(ByteArray(28) { 0xFF.toByte() })
            .array()

        fun encodePacket(
            deviceId: Int,
            miioTimestamp: Int,
            payload: String,
            token: Token
        ): ByteArray {
            val payloadBytes = token.encrypt(payload.toByteArray(charset = Charsets.ISO_8859_1))
            val packetLength = (payloadBytes.size + 32)
            val messageBytes = ByteArray(packetLength)
            val buffer = ByteBuffer.wrap(messageBytes)
                .putShort(MAGIC)
                .putShort(packetLength.toShort())
                .putInt(0) // unknown1
                .putInt(deviceId)
                .putInt(miioTimestamp)
                .put(token.tokenBytes)
                .put(payloadBytes)

            val md5 = messageBytes.md5()

            buffer.position(16)
            buffer.put(md5)

            return messageBytes
        }
    }
}
