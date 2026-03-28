package com.expense_management.domain.model.binary

class Signature private constructor(bytes: ByteArray) : BinaryValue(bytes) {
    companion object {
        fun from(bytes: ByteArray) = Signature(bytes.copyOf())
    }
}
