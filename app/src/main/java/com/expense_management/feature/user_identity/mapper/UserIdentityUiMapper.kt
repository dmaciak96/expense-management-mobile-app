package com.expense_management.feature.user_identity.mapper

import com.expense_management.core.time.ZoneProvider
import com.expense_management.core.time.toInstant
import com.expense_management.core.time.toLocalDateTime
import com.expense_management.domain.model.UserIdentity
import com.expense_management.feature.user_identity.ui.state.UserIdentityUiModel
import jakarta.inject.Inject

class UserIdentityUiMapper @Inject constructor(
    private val zoneProvider: ZoneProvider
) {
    fun toUiState(userIdentity: UserIdentity) = UserIdentityUiModel(
        name = userIdentity.name,
        createdAt = userIdentity.createdAt.toLocalDateTime(zoneProvider.zoneId()),
        identity = userIdentity.identity,
        publicKey = userIdentity.publicKey
    )

    fun toDomain(userIdentityUiModel: UserIdentityUiModel) = UserIdentity(
        name = userIdentityUiModel.name,
        createdAt = userIdentityUiModel.createdAt.toInstant(zoneProvider.zoneId()),
        identity = userIdentityUiModel.identity,
        publicKey = userIdentityUiModel.publicKey
    )
}