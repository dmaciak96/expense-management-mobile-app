package com.expense_management.domain.mapper

import com.expense_management.data.model.UserIdentityEntity
import com.expense_management.domain.model.UserIdentity
import com.expense_management.domain.model.binary.PublicKey
import java.time.Instant
import java.util.UUID

object UserIdentityMapper {
    fun toEntity(userIdentity: UserIdentity) =
        UserIdentityEntity(
            identity = userIdentity.identity.toString(),
            createdAt = userIdentity.createdAt.toEpochMilli(),
            name = userIdentity.name,
            publicKey = userIdentity.publicKey.toByteArray(),
        )

    fun toDomain(userIdentityEntity: UserIdentityEntity) =
        UserIdentity(
            createdAt = Instant.ofEpochMilli(userIdentityEntity.createdAt),
            name = userIdentityEntity.name,
            publicKey = PublicKey.from(userIdentityEntity.publicKey),
            identity = UUID.fromString(userIdentityEntity.identity)
        )
}