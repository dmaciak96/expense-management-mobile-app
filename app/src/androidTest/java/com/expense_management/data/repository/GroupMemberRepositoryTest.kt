package com.expense_management.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.expense_management.core.common.OperationResult
import com.expense_management.data.DatabaseTestSuite
import com.expense_management.data.dao.GroupMemberDao
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.data.model.GroupEntity
import com.expense_management.data.model.GroupMemberEntity
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
class GroupMemberRepositoryTest : DatabaseTestSuite() {
    private lateinit var repository: GroupMemberRepository
    private lateinit var dao: GroupMemberDao

    @Before
    fun createRepository() {
        dao = db.groupMemberDao()
        repository = GroupMemberRepository(dao)
    }

    @Test
    fun shouldSaveNewGroupMember() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        assertThat(
            repository.insert(TEST_MEMBER),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getById(1).firstOrNull()
        assertGroupMemberEquals(TEST_MEMBER.copy(id = 1), result)
    }

    @Test
    fun shouldDeleteGroupMember() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_MEMBER)

        assertThat(
            repository.delete(TEST_MEMBER.copy(id = 1)),
            instanceOf(OperationResult.Success::class.java)
        )

        assertThat(dao.getAll().first().size, equalTo(0))
    }

    @Test
    fun shouldUpdateGroupMember() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_MEMBER)

        val updated = TEST_MEMBER.copy(
            id = 1,
            displayName = "updated_member",
            publicKey = byteArrayOf(9, 9, 9),
            role = "updated_role"
        )

        assertThat(
            repository.update(updated),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getById(1).firstOrNull()
        assertGroupMemberEquals(updated, result)
    }

    @Test
    fun shouldGetAllGroupMembers() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_MEMBER.copy(displayName = "member1", publicKey = byteArrayOf(1)))
        dao.insert(TEST_MEMBER.copy(displayName = "member2", publicKey = byteArrayOf(2)))
        dao.insert(TEST_MEMBER.copy(displayName = "member3", publicKey = byteArrayOf(3)))

        val result = repository.getAll()
            .first { it is OperationResult.Success } as OperationResult.Success<List<GroupMemberEntity>>

        assertThat(result.data.size, equalTo(3))
        assertContainsGroupMember(result.data, TEST_MEMBER.copy(id = 1, displayName = "member1", publicKey = byteArrayOf(1)))
        assertContainsGroupMember(result.data, TEST_MEMBER.copy(id = 2, displayName = "member2", publicKey = byteArrayOf(2)))
        assertContainsGroupMember(result.data, TEST_MEMBER.copy(id = 3, displayName = "member3", publicKey = byteArrayOf(3)))
    }

    @Test
    fun shouldGetGroupMemberById() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_MEMBER.copy(displayName = "member1", publicKey = byteArrayOf(1)))
        dao.insert(TEST_MEMBER.copy(displayName = "member2", publicKey = byteArrayOf(2)))
        dao.insert(TEST_MEMBER.copy(displayName = "member3", publicKey = byteArrayOf(3)))

        val result1 = repository.getById(1)
            .first { it is OperationResult.Success } as OperationResult.Success<GroupMemberEntity?>
        val result2 = repository.getById(2)
            .first { it is OperationResult.Success } as OperationResult.Success<GroupMemberEntity?>
        val result3 = repository.getById(3)
            .first { it is OperationResult.Success } as OperationResult.Success<GroupMemberEntity?>
        val emptyResult = repository.getById(999)
            .first { it is OperationResult.Success } as OperationResult.Success<GroupMemberEntity?>

        assertGroupMemberEquals(TEST_MEMBER.copy(id = 1, displayName = "member1", publicKey = byteArrayOf(1)), result1.data)
        assertGroupMemberEquals(TEST_MEMBER.copy(id = 2, displayName = "member2", publicKey = byteArrayOf(2)), result2.data)
        assertGroupMemberEquals(TEST_MEMBER.copy(id = 3, displayName = "member3", publicKey = byteArrayOf(3)), result3.data)
        assertThat(emptyResult.data, equalTo(null))
    }

    @Test
    fun shouldGetGroupMembersByGroupId() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)
        groupDao.insert(TEST_GROUP.copy(name = "group2", createdAt = CREATED_AT + 100))

        dao.insert(TEST_MEMBER.copy(groupId = 1, displayName = "member1", publicKey = byteArrayOf(1)))
        dao.insert(TEST_MEMBER.copy(groupId = 1, displayName = "member2", publicKey = byteArrayOf(2)))
        dao.insert(TEST_MEMBER.copy(groupId = 2, displayName = "member3", publicKey = byteArrayOf(3)))

        val result = repository.getGroupMembersByGroupId(1)
            .first { it is OperationResult.Success } as OperationResult.Success<List<GroupMemberEntity>>

        assertThat(result.data.size, equalTo(2))
        assertContainsGroupMember(result.data, TEST_MEMBER.copy(id = 1, groupId = 1, displayName = "member1", publicKey = byteArrayOf(1)))
        assertContainsGroupMember(result.data, TEST_MEMBER.copy(id = 2, groupId = 1, displayName = "member2", publicKey = byteArrayOf(2)))
    }

    @Test
    fun shouldReturnGroupMemberAndExpenses() = runTest(timeout = 1.seconds) {
        val expenseDao = db.expenseDao()
        dao.insert(TEST_MEMBER.copy(id = 0, displayName = "member1", publicKey = byteArrayOf(1)))
        dao.insert(TEST_MEMBER.copy(id = 0, displayName = "member2", publicKey = byteArrayOf(2)))

        expenseDao.insert(
            ExpenseEntity(
                groupId = 1,
                paidByMemberId = 1,
                createdAt = CREATED_AT,
                name = "expense1",
                minorUnits = 100,
                currency = CURRENCY
            )
        )
        expenseDao.insert(
            ExpenseEntity(
                groupId = 1,
                paidByMemberId = 1,
                createdAt = CREATED_AT + 1,
                name = "expense2",
                minorUnits = 200,
                currency = CURRENCY
            )
        )
        expenseDao.insert(
            ExpenseEntity(
                groupId = 1,
                paidByMemberId = 2,
                createdAt = CREATED_AT + 2,
                name = "expense3",
                minorUnits = 300,
                currency = CURRENCY
            )
        )

        val result = repository.getGroupMemberAndExpenses()
            .first { it is OperationResult.Success } as OperationResult.Success<Map<GroupMemberEntity, List<ExpenseEntity>>>

        assertThat(result.data.size, equalTo(2))

        val member1Entry = result.data.entries.first { it.key.id == 1 }
        val member2Entry = result.data.entries.first { it.key.id == 2 }

        assertThat(member1Entry.value.size, equalTo(2))
        assertThat(member2Entry.value.size, equalTo(1))
    }

    @Test
    fun shouldReturnGroupMemberAndExpenseShares() = runTest(timeout = 1.seconds) {
        val shareDao = db.expenseShareDao()
        dao.insert(TEST_MEMBER.copy(id = 0, displayName = "member1", publicKey = byteArrayOf(1)))
        dao.insert(TEST_MEMBER.copy(id = 0, displayName = "member2", publicKey = byteArrayOf(2)))

        shareDao.insert(ExpenseShareEntity(expenseId = 1, memberId = 1, minorUnits = 50, currency = CURRENCY))
        shareDao.insert(ExpenseShareEntity(expenseId = 2, memberId = 1, minorUnits = 70, currency = CURRENCY))
        shareDao.insert(ExpenseShareEntity(expenseId = 1, memberId = 2, minorUnits = 30, currency = CURRENCY))

        val result = repository.getGroupMemberAndExpenseShares()
            .first { it is OperationResult.Success } as OperationResult.Success<Map<GroupMemberEntity, List<ExpenseShareEntity>>>

        assertThat(result.data.size, equalTo(2))

        val member1Entry = result.data.entries.first { it.key.id == 1 }
        val member2Entry = result.data.entries.first { it.key.id == 2 }

        assertThat(member1Entry.value.size, equalTo(2))
        assertThat(member2Entry.value.size, equalTo(1))
    }

    @Test
    fun shouldReturnGroupMemberAndOperations() = runTest(timeout = 1.seconds) {
        val operationDao = db.operationDao()
        dao.insert(TEST_MEMBER.copy(id = 0, displayName = "member1", publicKey = byteArrayOf(1)))
        dao.insert(TEST_MEMBER.copy(id = 0, displayName = "member2", publicKey = byteArrayOf(2)))

        operationDao.insert(
            OperationEntity(
                groupId = 1,
                operationAuthorId = 1,
                createdAt = CREATED_AT,
                lamportClock = 1,
                type = "type1",
                payload = byteArrayOf(1),
                signature = byteArrayOf(11)
            )
        )
        operationDao.insert(
            OperationEntity(
                groupId = 1,
                operationAuthorId = 1,
                createdAt = CREATED_AT + 1,
                lamportClock = 2,
                type = "type2",
                payload = byteArrayOf(2),
                signature = byteArrayOf(22)
            )
        )
        operationDao.insert(
            OperationEntity(
                groupId = 1,
                operationAuthorId = 2,
                createdAt = CREATED_AT + 2,
                lamportClock = 3,
                type = "type3",
                payload = byteArrayOf(3),
                signature = byteArrayOf(33)
            )
        )

        val result = repository.getGroupMemberAndOperations()
            .first { it is OperationResult.Success } as OperationResult.Success<Map<GroupMemberEntity, List<OperationEntity>>>

        assertThat(result.data.size, equalTo(2))

        val member1Entry = result.data.entries.first { it.key.id == 1 }
        val member2Entry = result.data.entries.first { it.key.id == 2 }

        assertThat(member1Entry.value.size, equalTo(2))
        assertThat(member2Entry.value.size, equalTo(1))
    }

    private fun assertContainsGroupMember(actual: List<GroupMemberEntity>, expected: GroupMemberEntity) {
        val found = actual.any {
            it.id == expected.id &&
                    it.groupId == expected.groupId &&
                    it.displayName == expected.displayName &&
                    it.role == expected.role &&
                    it.publicKey.contentEquals(expected.publicKey)
        }
        assertThat(found, equalTo(true))
    }

    private fun assertGroupMemberEquals(expected: GroupMemberEntity?, actual: GroupMemberEntity?) {
        if (expected == null || actual == null) {
            assertThat(actual, equalTo(expected))
            return
        }

        assertThat(actual.id, equalTo(expected.id))
        assertThat(actual.groupId, equalTo(expected.groupId))
        assertThat(actual.displayName, equalTo(expected.displayName))
        assertThat(actual.role, equalTo(expected.role))
        assertThat(actual.publicKey.contentEquals(expected.publicKey), equalTo(true))
    }

    companion object {
        private const val GROUP_NAME = "test_group"
        private const val MEMBER_NAME = "test_member"
        private const val ROLE = "test_role"
        private const val CREATED_AT = 12345L
        private const val CURRENCY = "PLN"

        private val TEST_GROUP = GroupEntity(
            createdAt = CREATED_AT,
            name = GROUP_NAME
        )

        private val TEST_MEMBER = GroupMemberEntity(
            groupId = 1,
            displayName = MEMBER_NAME,
            publicKey = byteArrayOf(1, 2, 3, 4),
            role = ROLE
        )
    }
}
