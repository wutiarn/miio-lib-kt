package ru.wtrn.miio

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * MiIo Device Token
 *
 * Used for message encryption, decryption and authentication.
 *
 * This implementation is thread safe.
 */
class Token(tokenHex: String) {
    internal val tokenBytes = tokenHex.hexStringToByteArray()

    private val tokenMd5 = tokenBytes.md5()
    private val key = SecretKeySpec(tokenMd5, "AES")
    private val iv = IvParameterSpec((tokenMd5 + tokenBytes).md5())

    private fun getCipher() = Cipher.getInstance("AES/CBC/PKCS5Padding")

    fun encrypt(msg: ByteArray): ByteArray {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        return cipher.doFinal(msg)
    }

    fun decrypt(encrypted: ByteArray): ByteArray {
        val cipher = getCipher()
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        return cipher.doFinal(encrypted)
    }
}
