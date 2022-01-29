package ru.wtrn.miio

class IncorrectMiIoMagicHeaderException(actual: Short) : RuntimeException("Incorrect magic header: $actual")

class IncorrectMiIoChecksumException : RuntimeException("Incorrect packet checksum")
