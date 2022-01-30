package ru.wtrn.miio

class IncorrectMiIoMagicHeaderException(actual: Short) : RuntimeException("Incorrect magic header: $actual")

class IncorrectMiIoChecksumException : RuntimeException("Incorrect packet checksum")

class MiIoRequestTimeoutException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
