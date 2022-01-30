package ru.wtrn.miio

import java.nio.ByteBuffer

internal class Packet(
    val deviceId: Int,
    val miioTimestamp: Int,
    val payload: String?
) {
    companion object {
        private const val MAGIC = 0x2131.toShort()
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

        fun decodePacket(packetBytes: ByteArray, token: Token?): Packet {
            val buffer = ByteBuffer.wrap(packetBytes)

            val magic = buffer.short
            if (magic != MAGIC) {
                throw IncorrectMiIoMagicHeaderException(magic)
            }

            val length = buffer.short.toInt()

            buffer.int // skip unknown1 header

            val deviceId = buffer.int
            val timestamp = buffer.int
            val checksum = ByteArray(16).also { buffer.get(it) }

            val dataLength = length - 32
            val payload = when {
                dataLength > 0 && token != null -> ByteArray(dataLength)
                    .also { buffer.get(it) }
                    .let { encryptedBytes ->
                        val decryptedBytes = token.decrypt(encryptedBytes)
                        String(decryptedBytes)
                    }
                else -> null
            }

            buffer.rewind()
            val decodedPacket = Packet(
                deviceId = deviceId,
                miioTimestamp = timestamp,
                payload = payload
            )

            if (payload != null) {
                if (token == null) {
                    // Payload is not decoded if token is not provided.
                    // So if we have decoded payload - token can't be null.
                    throw IllegalStateException("Token is null, but payload is not")
                }
                buffer.position(16)
                buffer.put(token.tokenBytes)

                val actualMd5 = packetBytes.md5()
                if (!checksum.contentEquals(actualMd5)) {
                    throw IncorrectMiIoChecksumException()
                }
            }

            return decodedPacket
        }
    }
}
