package com.expense_management.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.expense_management.core.common.OperationResult
import com.expense_management.data.DatabaseTestSuite
import com.expense_management.data.dao.OperationDao
import com.expense_management.data.model.GroupEntity
import com.expense_management.data.model.OperationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class OperationRepositoryTest : DatabaseTestSuite() {
    private lateinit var repository: OperationRepository
    private lateinit var dao: OperationDao

    @Before
    fun createRepository() {
        dao = db.operationDao()
        repository = OperationRepository(dao)
    }

    @Test
    fun shouldSaveNewOperation() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        assertThat(
            repository.insert(TEST_OPERATION),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getByIdentity(OPERATION_IDENTITY).firstOrNull()
        assertOperationEquals(TEST_OPERATION.copy(id = 1), result)
    }

    @Test
    fun shouldGetAllOperations() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_OPERATION.copy(type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11), identity = OPERATION_IDENTITY_1))
        dao.insert(TEST_OPERATION.copy(type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22), identity = OPERATION_IDENTITY_2))
        dao.insert(TEST_OPERATION.copy(type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33), identity = OPERATION_IDENTITY_3))

        val result = repository.getAll()
            .first { it is OperationResult.Success } as OperationResult.Success<List<OperationEntity>>

        assertThat(result.data.size, equalTo(3))
        assertContainsOperation(
            result.data,
            TEST_OPERATION.copy(id = 1, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11), identity = OPERATION_IDENTITY_1)
        )
        assertContainsOperation(
            result.data,
            TEST_OPERATION.copy(id = 2, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22), identity = OPERATION_IDENTITY_2)
        )
        assertContainsOperation(
            result.data,
            TEST_OPERATION.copy(id = 3, type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33), identity = OPERATION_IDENTITY_3)
        )
    }

    @Test
    fun shouldGetOperationByIdentity() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_OPERATION.copy(type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11), identity = OPERATION_IDENTITY_1))
        dao.insert(TEST_OPERATION.copy(type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22), identity = OPERATION_IDENTITY_2))
        dao.insert(TEST_OPERATION.copy(type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33), identity = OPERATION_IDENTITY_3))

        val result1 = repository.getByIdentity(UUID.fromString(OPERATION_IDENTITY_1))
            .first { it is OperationResult.Success } as OperationResult.Success<OperationEntity?>
        val result2 = repository.getByIdentity(UUID.fromString(OPERATION_IDENTITY_2))
            .first { it is OperationResult.Success } as OperationResult.Success<OperationEntity?>
        val result3 = repository.getByIdentity(UUID.fromString(OPERATION_IDENTITY_3))
            .first { it is OperationResult.Success } as OperationResult.Success<OperationEntity?>
        val emptyResult = repository.getByIdentity(UUID.fromString(NOT_EXISTING_OPERATION_IDENTITY))
            .first { it is OperationResult.Success } as OperationResult.Success<OperationEntity?>

        assertOperationEquals(
            TEST_OPERATION.copy(id = 1, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11), identity = OPERATION_IDENTITY_1),
            result1.data
        )
        assertOperationEquals(
            TEST_OPERATION.copy(id = 2, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22), identity = OPERATION_IDENTITY_2),
            result2.data
        )
        assertOperationEquals(
            TEST_OPERATION.copy(id = 3, type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33), identity = OPERATION_IDENTITY_3),
            result3.data
        )
        assertThat(emptyResult.data, equalTo(null))
    }

    @Test
    fun shouldGetOperationsByGroupId() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)
        groupDao.insert(TEST_GROUP.copy(name = "group2", createdAt = CREATED_AT + 100, identity = GROUP_IDENTITY_2))

        dao.insert(TEST_OPERATION.copy(groupId = 1, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11), identity = OPERATION_IDENTITY_1))
        dao.insert(TEST_OPERATION.copy(groupId = 1, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22), identity = OPERATION_IDENTITY_2))
        dao.insert(TEST_OPERATION.copy(groupId = 2, type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33), identity = OPERATION_IDENTITY_3))

        val result = repository.getOperationsByGroupId(1)
            .first { it is OperationResult.Success } as OperationResult.Success<List<OperationEntity>>

        assertThat(result.data.size, equalTo(2))
        assertContainsOperation(
            result.data,
            TEST_OPERATION.copy(id = 1, groupId = 1, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11), identity = OPERATION_IDENTITY_1)
        )
        assertContainsOperation(
            result.data,
            TEST_OPERATION.copy(id = 2, groupId = 1, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22), identity = OPERATION_IDENTITY_2)
        )
    }

    @Test
    fun shouldGetOperationsByGroupMemberId() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_OPERATION.copy(operationAuthorId = 11, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11), identity = OPERATION_IDENTITY_1))
        dao.insert(TEST_OPERATION.copy(operationAuthorId = 11, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22), identity = OPERATION_IDENTITY_2))
        dao.insert(TEST_OPERATION.copy(operationAuthorId = 22, type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33), identity = OPERATION_IDENTITY_3))

        val result = repository.getOperationsByGroupMemberId(11)
            .first { it is OperationResult.Success } as OperationResult.Success<List<OperationEntity>>

        assertThat(result.data.size, equalTo(2))
        assertContainsOperation(
            result.data,
            TEST_OPERATION.copy(id = 1, operationAuthorId = 11, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11), identity = OPERATION_IDENTITY_1)
        )
        assertContainsOperation(
            result.data,
            TEST_OPERATION.copy(id = 2, operationAuthorId = 11, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22), identity = OPERATION_IDENTITY_2)
        )
    }

    private fun assertContainsOperation(actual: List<OperationEntity>, expected: OperationEntity) {
        val found = actual.any {
            it.id == expected.id &&
                    it.groupId == expected.groupId &&
                    it.operationAuthorId == expected.operationAuthorId &&
                    it.createdAt == expected.createdAt &&
                    it.lamportClock == expected.lamportClock &&
                    it.type == expected.type &&
                    it.identity == expected.identity &&
                    it.payload.contentEquals(expected.payload) &&
                    it.signature.contentEquals(expected.signature)
        }
        assertThat(found, equalTo(true))
    }

    private fun assertOperationEquals(expected: OperationEntity?, actual: OperationEntity?) {
        if (expected == null || actual == null) {
            assertThat(actual, equalTo(expected))
            return
        }

        assertThat(actual.id, equalTo(expected.id))
        assertThat(actual.groupId, equalTo(expected.groupId))
        assertThat(actual.operationAuthorId, equalTo(expected.operationAuthorId))
        assertThat(actual.createdAt, equalTo(expected.createdAt))
        assertThat(actual.lamportClock, equalTo(expected.lamportClock))
        assertThat(actual.type, equalTo(expected.type))
        assertThat(actual.identity, equalTo(expected.identity))
        assertThat(actual.payload.contentEquals(expected.payload), equalTo(true))
        assertThat(actual.signature.contentEquals(expected.signature), equalTo(true))
    }

    companion object {
        private const val GROUP_NAME = "test_group"
        private const val CREATED_AT = 12345L
        private const val OPERATION_TYPE = "test_type"
        private const val LAMPORT_CLOCK = 1234L
        private const val OPERATION_AUTHOR_ID = 11

        private const val GROUP_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c90"
        private const val GROUP_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c91"

        private const val OPERATION_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c92"
        private const val OPERATION_IDENTITY_1 = "dfa4e836-190a-4292-a3fe-c516c1d99c93"
        private const val OPERATION_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c94"
        private const val OPERATION_IDENTITY_3 = "dfa4e836-190a-4292-a3fe-c516c1d99c95"
        private const val NOT_EXISTING_OPERATION_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c96"

        private val TEST_GROUP = GroupEntity(
            createdAt = CREATED_AT,
            name = GROUP_NAME,
            identity = GROUP_IDENTITY
        )

        private val TEST_OPERATION = OperationEntity(
            groupId = 1,
            operationAuthorId = OPERATION_AUTHOR_ID,
            createdAt = CREATED_AT,
            lamportClock = LAMPORT_CLOCK,
            type = OPERATION_TYPE,
            payload = byteArrayOf(1, 2, 3, 4),
            signature = byteArrayOf(5, 6, 7, 8),
            identity = OPERATION_IDENTITY
        )
    }
}
