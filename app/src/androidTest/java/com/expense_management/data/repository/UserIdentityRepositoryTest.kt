package com.expense_management.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.expense_management.core.common.OperationResult
import com.expense_management.core.common.OperationResult.Error
import com.expense_management.core.common.OperationResult.Success
import com.expense_management.data.DatabaseTestSuite
import com.expense_management.data.dao.UserIdentityDao
import com.expense_management.data.model.UserIdentityEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class UserIdentityRepositoryTest : DatabaseTestSuite() {
    private lateinit var repository: UserIdentityRepository
    private lateinit var dao: UserIdentityDao

    @Before
    fun createRepository() {
        dao = db.userIdentityDao()
        repository = UserIdentityRepository(dao)
    }

    @Test
    fun shouldSaveUserIdentityWhenNotExists() = runTest(timeout = 1.seconds) {
        assertThat(
            repository.insertIfNotExists(TEST_USER_IDENTITY),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getUserIdentity().firstOrNull()
        assertUserIdentityEquals(TEST_USER_IDENTITY, result)
    }

    @Test
    fun shouldReturnErrorWhenUserIdentityAlreadyExists() = runTest(timeout = 1.seconds) {
        dao.insertIfNotExists(TEST_USER_IDENTITY)

        val result = repository.insertIfNotExists(TEST_USER_IDENTITY)

        assertThat(result, instanceOf(Error::class.java))
    }

    @Test
    fun shouldGetUserIdentity() = runTest(timeout = 1.seconds) {
        dao.insertIfNotExists(TEST_USER_IDENTITY)

        val result = repository.getUserIdentity()
            .first { it is Success } as Success<UserIdentityEntity?>

        assertUserIdentityEquals(TEST_USER_IDENTITY, result.data)
    }

    @Test
    fun shouldReturnNullWhenUserIdentityDoesNotExist() = runTest(timeout = 1.seconds) {
        val result = repository.getUserIdentity()
            .first { it is Success } as Success<UserIdentityEntity?>

        assertThat(result.data, equalTo(null))
    }

    @Test
    fun shouldUpdateUserIdentity() = runTest(timeout = 1.seconds) {
        dao.insertIfNotExists(TEST_USER_IDENTITY)

        val updated = TEST_USER_IDENTITY.copy(
            createdAt = CREATED_AT + 100,
            identity = UPDATED_IDENTITY,
            name = "updated_user",
            publicKey = byteArrayOf(9, 8, 7, 6)
        )

        assertThat(
            repository.update(updated),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getUserIdentity().firstOrNull()
        assertUserIdentityEquals(updated, result)
    }

    private fun assertUserIdentityEquals(expected: UserIdentityEntity?, actual: UserIdentityEntity?) {
        if (expected == null || actual == null) {
            assertThat(actual, equalTo(expected))
            return
        }

        assertThat(actual.id, equalTo(expected.id))
        assertThat(actual.createdAt, equalTo(expected.createdAt))
        assertThat(actual.identity, equalTo(expected.identity))
        assertThat(actual.name, equalTo(expected.name))
        assertThat(actual.publicKey.contentEquals(expected.publicKey), equalTo(true))
    }

    companion object {
        private const val CREATED_AT = 12345L
        private const val IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99d00"
        private const val UPDATED_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99d01"
        private const val NAME = "test_user"

        private val TEST_USER_IDENTITY = UserIdentityEntity(
            createdAt = CREATED_AT,
            identity = IDENTITY,
            name = NAME,
            publicKey = byteArrayOf(1, 2, 3, 4)
        )
    }
}
