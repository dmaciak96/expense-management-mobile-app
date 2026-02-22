package com.expense_management.domain.model

abstract class BinaryValue(
    private val bytes: ByteArray
) {
    fun toByteArray(): ByteArray = bytes.copyOf()

    override fun equals(other: Any?): Boolean =
        other is BinaryValue &&
                bytes.contentEquals(other.bytes)

    override fun hashCode(): Int =
        bytes.contentHashCode()
}