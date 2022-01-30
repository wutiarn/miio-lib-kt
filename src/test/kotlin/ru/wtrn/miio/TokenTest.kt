package ru.wtrn.miio

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class TokenTest {
    private val tokenHex = "586e584268475142564d485234734d4b"
    private val token = Token(tokenHex)

    @Test
    fun getTokenBytes() {
        assertThat(token.tokenBytes.toHex()).isEqualTo(tokenHex)
    }

    @Test
    fun encrypt() {
        val payload = "Hello world".toByteArray()
        val encrypted = token.encrypt(payload)
        assertThat(encrypted.toHex()).isEqualTo("00ddf4342e3a4f49984204a2eceefeef")
    }

    @Test
    fun decrypt() {
        val encrypted = "00ddf4342e3a4f49984204a2eceefeef"
        val decrypted = token.decrypt(encrypted.hexStringToByteArray())
        assertThat(decrypted.decodeToString()).isEqualTo("Hello world")
    }
}
