package com.expense_management.feature.operation.mapper

import com.expense_management.core.time.ZoneProvider
import com.expense_management.core.time.toInstant
import com.expense_management.core.time.toLocalDateTime
import com.expense_management.domain.model.Operation
import com.expense_management.domain.model.SignedPayload
import com.expense_management.domain.model.binary.Payload
import com.expense_management.domain.model.binary.Signature
import com.expense_management.feature.operation.ui.state.OperationUiModel
import jakarta.inject.Inject
import kotlinx.serialization.json.Json

class OperationUiMapper @Inject constructor(
    private val zoneProvider: ZoneProvider
) {

    fun toUiState(operation: Operation) = OperationUiModel(
        id = operation.id,
        createdAt = operation.createdAt.toLocalDateTime(zoneProvider.zoneId()),
        identity = operation.identity,
        groupIdentity = operation.groupIdentity,
        operationAuthorIdentity = operation.operationAuthorIdentity,
        lamportClock = operation.lamportClock,
        type = operation.type,
        signedPayload = SignedPayload(
            payload = Payload.from(operation.signedPayload.payload.toByteArray()),
            signature = Signature.from(operation.signedPayload.signature.toByteArray())
        )
    )

    fun toUiStates(operations: List<Operation>) = operations.map { toUiState(it) }

    fun toDomain(operationUiModel: OperationUiModel) = Operation(
        id = operationUiModel.id,
        createdAt = operationUiModel.createdAt.toInstant(zoneProvider.zoneId()),
        identity = operationUiModel.identity,
        groupIdentity = operationUiModel.groupIdentity,
        operationAuthorIdentity = operationUiModel.operationAuthorIdentity,
        lamportClock = operationUiModel.lamportClock,
        type = operationUiModel.type,
        signedPayload = SignedPayload(
            payload = Payload.from(operationUiModel.signedPayload.payload.toByteArray()),
            signature = Signature.from(operationUiModel.signedPayload.signature.toByteArray())
        )
    )

    companion object {
        val JSON = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }
}
