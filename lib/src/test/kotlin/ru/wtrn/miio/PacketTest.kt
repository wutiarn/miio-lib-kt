package ru.wtrn.miio

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PacketTest {
    @Test
    fun checkHelloPacket() {
        @Suppress("SpellCheckingInspection")
        assertThat(Packet.HELLO.toHex()).isEqualTo("21310020ffffffffffffffffffffffffffffffffffffffffffffffffffffffff")
    }

    @Test
    fun encodePacket() {
        val token = Token("3c92df7588021efbcd6bd55c9147bed0")
        val encoded = Packet.encodePacket(
            deviceId = 133525349,
            miioTimestamp = 70633,
            payload = """{"method":"miIO.info","params":[],"id":70633}""",
            token = token
        )
        @Suppress("SpellCheckingInspection")
        assertThat(encoded.toHex())
            .isEqualTo(
                "213100500000000007f56f65000113e95424d99f4f6f0e89fb5c5d54e79e2c413083a0b3cebdbe3b2813dd94f" +
                        "20e5247acf3e9f86e51ed9f95caa50ffa1f899d3026f0fcfae93a52dbdc4fc088a54205"
            )
    }

    @Test
    fun decodePacket() {
        val token = Token("3c92df7588021efbcd6bd55c9147bed0")
        val packetBytes = ("213100500000000007f56f65000113e95424d99f4f6f0e89fb5c5d54e79e2c413083a0b3cebdbe3b2813dd9" +
                "4f20e5247acf3e9f86e51ed9f95caa50ffa1f899d3026f0fcfae93a52dbdc4fc088a54205").hexStringToByteArray()
        val packet = Packet.decodePacket(packetBytes, token)
        assertThat(packet.deviceId).isEqualTo(133525349)
        assertThat(packet.miioTimestamp).isEqualTo(70633)
        assertThat(packet.payload).isEqualTo("""{"method":"miIO.info","params":[],"id":70633}""")
    }
}
