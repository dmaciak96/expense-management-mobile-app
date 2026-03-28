package com.expense_management.domain.model.binary

class PublicKey private constructor(bytes: ByteArray) : BinaryValue(bytes) {
    companion object {
        fun from(bytes: ByteArray) = PublicKey(bytes.copyOf())
    }
}
