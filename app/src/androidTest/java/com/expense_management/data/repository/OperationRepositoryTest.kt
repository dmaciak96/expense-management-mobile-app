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

        val result = dao.getById(1).firstOrNull()
        assertOperationEquals(TEST_OPERATION.copy(id = 1), result)
    }

    @Test
    fun shouldGetAllOperations() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_OPERATION.copy(type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11)))
        dao.insert(TEST_OPERATION.copy(type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22)))
        dao.insert(TEST_OPERATION.copy(type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33)))

        val result = repository.getAll()
            .first { it is OperationResult.Success } as OperationResult.Success<List<OperationEntity>>

        assertThat(result.data.size, equalTo(3))
        assertContainsOperation(result.data, TEST_OPERATION.copy(id = 1, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11)))
        assertContainsOperation(result.data, TEST_OPERATION.copy(id = 2, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22)))
        assertContainsOperation(result.data, TEST_OPERATION.copy(id = 3, type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33)))
    }

    @Test
    fun shouldGetOperationById() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_OPERATION.copy(type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11)))
        dao.insert(TEST_OPERATION.copy(type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22)))
        dao.insert(TEST_OPERATION.copy(type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33)))

        val result1 = repository.getById(1)
            .first { it is OperationResult.Success } as OperationResult.Success<OperationEntity?>
        val result2 = repository.getById(2)
            .first { it is OperationResult.Success } as OperationResult.Success<OperationEntity?>
        val result3 = repository.getById(3)
            .first { it is OperationResult.Success } as OperationResult.Success<OperationEntity?>
        val emptyResult = repository.getById(999)
            .first { it is OperationResult.Success } as OperationResult.Success<OperationEntity?>

        assertOperationEquals(TEST_OPERATION.copy(id = 1, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11)), result1.data)
        assertOperationEquals(TEST_OPERATION.copy(id = 2, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22)), result2.data)
        assertOperationEquals(TEST_OPERATION.copy(id = 3, type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33)), result3.data)
        assertThat(emptyResult.data, equalTo(null))
    }

    @Test
    fun shouldGetOperationsByGroupId() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)
        groupDao.insert(TEST_GROUP.copy(name = "group2", createdAt = CREATED_AT + 100))

        dao.insert(TEST_OPERATION.copy(groupId = 1, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11)))
        dao.insert(TEST_OPERATION.copy(groupId = 1, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22)))
        dao.insert(TEST_OPERATION.copy(groupId = 2, type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33)))

        val result = repository.getOperationsByGroupId(1)
            .first { it is OperationResult.Success } as OperationResult.Success<List<OperationEntity>>

        assertThat(result.data.size, equalTo(2))
        assertContainsOperation(result.data, TEST_OPERATION.copy(id = 1, groupId = 1, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11)))
        assertContainsOperation(result.data, TEST_OPERATION.copy(id = 2, groupId = 1, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22)))
    }

    @Test
    fun shouldGetOperationsByGroupMemberId() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_OPERATION.copy(operationAuthorId = 11, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11)))
        dao.insert(TEST_OPERATION.copy(operationAuthorId = 11, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22)))
        dao.insert(TEST_OPERATION.copy(operationAuthorId = 22, type = "type3", payload = byteArrayOf(3), signature = byteArrayOf(33)))

        val result = repository.getOperationsByGroupMemberId(11)
            .first { it is OperationResult.Success } as OperationResult.Success<List<OperationEntity>>

        assertThat(result.data.size, equalTo(2))
        assertContainsOperation(result.data, TEST_OPERATION.copy(id = 1, operationAuthorId = 11, type = "type1", payload = byteArrayOf(1), signature = byteArrayOf(11)))
        assertContainsOperation(result.data, TEST_OPERATION.copy(id = 2, operationAuthorId = 11, type = "type2", payload = byteArrayOf(2), signature = byteArrayOf(22)))
    }

    private fun assertContainsOperation(actual: List<OperationEntity>, expected: OperationEntity) {
        val found = actual.any {
            it.id == expected.id &&
                    it.groupId == expected.groupId &&
                    it.operationAuthorId == expected.operationAuthorId &&
                    it.createdAt == expected.createdAt &&
                    it.lamportClock == expected.lamportClock &&
                    it.type == expected.type &&
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
        assertThat(actual.payload.contentEquals(expected.payload), equalTo(true))
        assertThat(actual.signature.contentEquals(expected.signature), equalTo(true))
    }

    companion object {
        private const val GROUP_NAME = "test_group"
        private const val CREATED_AT = 12345L
        private const val OPERATION_TYPE = "test_type"
        private const val LAMPORT_CLOCK = 1234L
        private const val OPERATION_AUTHOR_ID = 11

        private val TEST_GROUP = GroupEntity(
            createdAt = CREATED_AT,
            name = GROUP_NAME
        )

        private val TEST_OPERATION = OperationEntity(
            groupId = 1,
            operationAuthorId = OPERATION_AUTHOR_ID,
            createdAt = CREATED_AT,
            lamportClock = LAMPORT_CLOCK,
            type = OPERATION_TYPE,
            payload = byteArrayOf(1, 2, 3, 4),
            signature = byteArrayOf(5, 6, 7, 8)
        )
    }
}