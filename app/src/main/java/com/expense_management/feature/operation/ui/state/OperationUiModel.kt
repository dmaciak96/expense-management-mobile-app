package com.expense_management.feature.operation.ui.state

import com.expense_management.domain.model.OperationType
import com.expense_management.domain.model.SignedPayload
import java.time.LocalDateTime
import java.util.UUID

data class OperationUiModel(
    val id: Int = 0,
    val identity: UUID = UUID.randomUUID(),
    val groupIdentity: UUID,
    val operationAuthorIdentity: UUID,

    // for UI/human time only (not used for conflict resolution)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    //see: https://www.baeldung.com/cs/lamport-clock
    val lamportClock: Long,

    val type: OperationType,
    val signedPayload: SignedPayload
)
