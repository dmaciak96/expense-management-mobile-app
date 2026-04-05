package com.expense_management.domain.model

import com.expense_management.domain.model.binary.Payload
import com.expense_management.domain.model.binary.Signature
import java.time.Instant
import java.util.UUID

data class Operation(
    val id: Int,
    val identity: UUID,
    val groupIdentity: UUID,
    val operationAuthorIdentity: UUID,

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

enum class OperationType {
    ADD_MEMBER,
    ADD_EXPENSE,
    ADD_GROUP,
    REMOVE_MEMBER,
    REMOVE_EXPENSE,
    REMOVE_GROUP,
}
