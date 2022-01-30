package ru.wtrn.miio

import java.security.MessageDigest

internal fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }

internal fun ByteArray.md5() = MessageDigest.getInstance("MD5").digest(this)

internal fun String.hexStringToByteArray() = ByteArray(this.length / 2) {
    this.substring(it * 2, it * 2 + 2).toInt(16).toByte()
}

internal fun arrayToHex(bytes: ByteArray) = bytes.toHex()
