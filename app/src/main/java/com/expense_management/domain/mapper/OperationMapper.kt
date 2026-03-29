package com.expense_management.domain.mapper

import com.expense_management.data.model.OperationEntity
import com.expense_management.domain.model.Operation
import com.expense_management.domain.model.OperationType
import com.expense_management.domain.model.SignedPayload
import com.expense_management.domain.model.binary.Payload
import com.expense_management.domain.model.binary.Signature
import java.time.Instant
import java.util.UUID

object OperationMapper {
    fun toEntity(operation: Operation) =
        OperationEntity(
            id = operation.id,
            groupIdentity = operation.groupIdentity.toString(),
            operationAuthorIdentity = operation.operationAuthorIdentity.toString(),
            createdAt = operation.createdAt.toEpochMilli(),
            lamportClock = operation.lamportClock,
            type = operation.type.name,
            payload = operation.signedPayload.payload.toByteArray(),
            signature = operation.signedPayload.signature.toByteArray(),
            identity = operation.identity.toString()
        )

    fun toDomain(operationEntity: OperationEntity) =
        Operation(
            id = operationEntity.id,
            groupIdentity = UUID.fromString(operationEntity.groupIdentity),
            operationAuthorIdentity = UUID.fromString(operationEntity.operationAuthorIdentity),
            createdAt = Instant.ofEpochMilli(operationEntity.createdAt),
            lamportClock = operationEntity.lamportClock,
            type = OperationType.valueOf(operationEntity.type),
            signedPayload = SignedPayload(
                payload = Payload.from(operationEntity.payload),
                signature = Signature.from(operationEntity.signature)
            ),
            identity = UUID.fromString(operationEntity.identity)
        )

    fun toDomainList(operationEntities: List<OperationEntity>) =
        operationEntities.map { toDomain(it) }
}