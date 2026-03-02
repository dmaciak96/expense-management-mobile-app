package com.expense_management.domain.mapper

import com.expense_management.data.model.OperationEntity
import com.expense_management.domain.model.Operation
import com.expense_management.domain.model.OperationType
import com.expense_management.domain.model.Payload
import com.expense_management.domain.model.Signature
import com.expense_management.domain.model.SignedPayload
import java.time.Instant

object OperationMapper {
    fun toEntity(operation: Operation) =
        OperationEntity(
            id = operation.id,
            groupId = operation.groupId,
            operationAuthorId = operation.operationAuthorId,
            createdAt = operation.createdAt.toEpochMilli(),
            lamportClock = operation.lamportClock,
            type = operation.type.name,
            payload = operation.signedPayload.payload.toByteArray(),
            signature = operation.signedPayload.signature.toByteArray()
        )

    fun toDomain(operationEntity: OperationEntity) =
        Operation(
            id = operationEntity.id,
            groupId = operationEntity.groupId,
            operationAuthorId = operationEntity.operationAuthorId,
            createdAt = Instant.ofEpochMilli(operationEntity.createdAt),
            lamportClock = operationEntity.lamportClock,
            type = OperationType.valueOf(operationEntity.type),
            signedPayload = SignedPayload(
                payload = Payload.from(operationEntity.payload),
                signature = Signature.from(operationEntity.signature)
            )
        )

    fun toDomainList(operationEntities: List<OperationEntity>) =
        operationEntities.map { toDomain(it) }
}