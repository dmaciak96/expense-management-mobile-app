package com.expense_management.domain

import java.time.Instant
import java.util.UUID

data class Operation(
    val id: UUID,
    val groupId: UUID,
    val operationAuthorId: UUID,

    // for UI/human time only (not used for conflict resolution)
    val createdAt: Instant,

    //see: https://www.baeldung.com/cs/lamport-clock
    val lamportClock: Long,

    val type: OperationType,
    val signedPayload: SignedPayload
)

data class SignedPayload(
    val payload: Payload,
    val signature: Signature
)

class Signature private constructor(bytes: ByteArray) : BinaryValue(bytes) {
    companion object {
        fun from(bytes: ByteArray) = Signature(bytes.copyOf())
    }
}

class Payload private constructor(bytes: ByteArray) : BinaryValue(bytes) {
    companion object {
        fun from(bytes: ByteArray) = Payload(bytes.copyOf())
    }
}

enum class OperationType {
    ADD_MEMBER,
    ADD_EXPENSE,
    REMOVE_MEMBER,
    REMOVE_EXPENSE,
}
