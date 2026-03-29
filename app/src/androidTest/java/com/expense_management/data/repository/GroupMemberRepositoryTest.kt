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
import java.util.UUID
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

        val result = dao.getByIdentity(MEMBER_IDENTITY).firstOrNull()
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

        val result = dao.getByIdentity(MEMBER_IDENTITY).firstOrNull()
        assertGroupMemberEquals(updated, result)
    }

    @Test
    fun shouldGetAllGroupMembers() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_MEMBER.copy(displayName = "member1", publicKey = byteArrayOf(1), identity = MEMBER_IDENTITY_1))
        dao.insert(TEST_MEMBER.copy(displayName = "member2", publicKey = byteArrayOf(2), identity = MEMBER_IDENTITY_2))
        dao.insert(TEST_MEMBER.copy(displayName = "member3", publicKey = byteArrayOf(3), identity = MEMBER_IDENTITY_3))

        val result = repository.getAll()
            .first { it is OperationResult.Success } as OperationResult.Success<List<GroupMemberEntity>>

        assertThat(result.data.size, equalTo(3))
        assertContainsGroupMember(
            result.data,
            TEST_MEMBER.copy(id = 1, displayName = "member1", publicKey = byteArrayOf(1), identity = MEMBER_IDENTITY_1)
        )
        assertContainsGroupMember(
            result.data,
            TEST_MEMBER.copy(id = 2, displayName = "member2", publicKey = byteArrayOf(2), identity = MEMBER_IDENTITY_2)
        )
        assertContainsGroupMember(
            result.data,
            TEST_MEMBER.copy(id = 3, displayName = "member3", publicKey = byteArrayOf(3), identity = MEMBER_IDENTITY_3)
        )
    }

    @Test
    fun shouldGetGroupMemberByIdentity() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_MEMBER.copy(displayName = "member1", publicKey = byteArrayOf(1), identity = MEMBER_IDENTITY_1))
        dao.insert(TEST_MEMBER.copy(displayName = "member2", publicKey = byteArrayOf(2), identity = MEMBER_IDENTITY_2))
        dao.insert(TEST_MEMBER.copy(displayName = "member3", publicKey = byteArrayOf(3), identity = MEMBER_IDENTITY_3))

        val result1 = repository.getByIdentity(UUID.fromString(MEMBER_IDENTITY_1))
            .first { it is OperationResult.Success } as OperationResult.Success<GroupMemberEntity?>
        val result2 = repository.getByIdentity(UUID.fromString(MEMBER_IDENTITY_2))
            .first { it is OperationResult.Success } as OperationResult.Success<GroupMemberEntity?>
        val result3 = repository.getByIdentity(UUID.fromString(MEMBER_IDENTITY_3))
            .first { it is OperationResult.Success } as OperationResult.Success<GroupMemberEntity?>
        val emptyResult = repository.getByIdentity(UUID.fromString(NOT_EXISTING_MEMBER_IDENTITY))
            .first { it is OperationResult.Success } as OperationResult.Success<GroupMemberEntity?>

        assertGroupMemberEquals(
            TEST_MEMBER.copy(id = 1, displayName = "member1", publicKey = byteArrayOf(1), identity = MEMBER_IDENTITY_1),
            result1.data
        )
        assertGroupMemberEquals(
            TEST_MEMBER.copy(id = 2, displayName = "member2", publicKey = byteArrayOf(2), identity = MEMBER_IDENTITY_2),
            result2.data
        )
        assertGroupMemberEquals(
            TEST_MEMBER.copy(id = 3, displayName = "member3", publicKey = byteArrayOf(3), identity = MEMBER_IDENTITY_3),
            result3.data
        )
        assertThat(emptyResult.data, equalTo(null))
    }

    @Test
    fun shouldGetGroupMembersByGroupIdentity() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)
        groupDao.insert(
            TEST_GROUP.copy(
                name = "group2",
                createdAt = CREATED_AT + 100,
                identity = GROUP_IDENTITY_2
            )
        )

        dao.insert(
            TEST_MEMBER.copy(
                groupIdentity = GROUP_IDENTITY,
                displayName = "member1",
                publicKey = byteArrayOf(1),
                identity = MEMBER_IDENTITY_1
            )
        )
        dao.insert(
            TEST_MEMBER.copy(
                groupIdentity = GROUP_IDENTITY,
                displayName = "member2",
                publicKey = byteArrayOf(2),
                identity = MEMBER_IDENTITY_2
            )
        )
        dao.insert(
            TEST_MEMBER.copy(
                groupIdentity = GROUP_IDENTITY_2,
                displayName = "member3",
                publicKey = byteArrayOf(3),
                identity = MEMBER_IDENTITY_3
            )
        )

        val result = repository.getGroupMembersByGroupIdentity(UUID.fromString(GROUP_IDENTITY))
            .first { it is OperationResult.Success } as OperationResult.Success<List<GroupMemberEntity>>

        assertThat(result.data.size, equalTo(2))
        assertContainsGroupMember(
            result.data,
            TEST_MEMBER.copy(
                id = 1,
                groupIdentity = GROUP_IDENTITY,
                displayName = "member1",
                publicKey = byteArrayOf(1),
                identity = MEMBER_IDENTITY_1
            )
        )
        assertContainsGroupMember(
            result.data,
            TEST_MEMBER.copy(
                id = 2,
                groupIdentity = GROUP_IDENTITY,
                displayName = "member2",
                publicKey = byteArrayOf(2),
                identity = MEMBER_IDENTITY_2
            )
        )
    }

    @Test
    fun shouldReturnGroupMemberAndExpenses() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        val expenseDao = db.expenseDao()

        groupDao.insert(TEST_GROUP)
        dao.insert(
            TEST_MEMBER.copy(
                id = 0,
                displayName = "member1",
                publicKey = byteArrayOf(1),
                identity = MEMBER_IDENTITY_1
            )
        )
        dao.insert(
            TEST_MEMBER.copy(
                id = 0,
                displayName = "member2",
                publicKey = byteArrayOf(2),
                identity = MEMBER_IDENTITY_2
            )
        )

        expenseDao.insert(
            ExpenseEntity(
                groupIdentity = GROUP_IDENTITY,
                paidByMemberIdentity = MEMBER_IDENTITY_1,
                createdAt = CREATED_AT,
                name = "expense1",
                minorUnits = 100,
                currency = CURRENCY,
                identity = EXPENSE_IDENTITY_1
            )
        )
        expenseDao.insert(
            ExpenseEntity(
                groupIdentity = GROUP_IDENTITY,
                paidByMemberIdentity = MEMBER_IDENTITY_1,
                createdAt = CREATED_AT + 1,
                name = "expense2",
                minorUnits = 200,
                currency = CURRENCY,
                identity = EXPENSE_IDENTITY_2
            )
        )
        expenseDao.insert(
            ExpenseEntity(
                groupIdentity = GROUP_IDENTITY,
                paidByMemberIdentity = MEMBER_IDENTITY_2,
                createdAt = CREATED_AT + 2,
                name = "expense3",
                minorUnits = 300,
                currency = CURRENCY,
                identity = EXPENSE_IDENTITY_3
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
        val groupDao = db.groupDao()
        val expenseDao = db.expenseDao()
        val shareDao = db.expenseShareDao()

        groupDao.insert(TEST_GROUP)
        expenseDao.insert(TEST_EXPENSE)
        expenseDao.insert(TEST_EXPENSE.copy(name = "expense2", identity = EXPENSE_IDENTITY_2))

        dao.insert(
            TEST_MEMBER.copy(
                id = 0,
                displayName = "member1",
                publicKey = byteArrayOf(1),
                identity = MEMBER_IDENTITY_1
            )
        )
        dao.insert(
            TEST_MEMBER.copy(
                id = 0,
                displayName = "member2",
                publicKey = byteArrayOf(2),
                identity = MEMBER_IDENTITY_2
            )
        )

        shareDao.insert(
            ExpenseShareEntity(
                expenseIdentity = EXPENSE_IDENTITY,
                memberIdentity = MEMBER_IDENTITY_1,
                minorUnits = 50,
                currency = CURRENCY,
                identity = SHARE_IDENTITY_1
            )
        )
        shareDao.insert(
            ExpenseShareEntity(
                expenseIdentity = EXPENSE_IDENTITY_2,
                memberIdentity = MEMBER_IDENTITY_1,
                minorUnits = 70,
                currency = CURRENCY,
                identity = SHARE_IDENTITY_2
            )
        )
        shareDao.insert(
            ExpenseShareEntity(
                expenseIdentity = EXPENSE_IDENTITY,
                memberIdentity = MEMBER_IDENTITY_2,
                minorUnits = 30,
                currency = CURRENCY,
                identity = SHARE_IDENTITY_3
            )
        )

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
        val groupDao = db.groupDao()
        val operationDao = db.operationDao()

        groupDao.insert(TEST_GROUP)
        dao.insert(
            TEST_MEMBER.copy(
                id = 0,
                displayName = "member1",
                publicKey = byteArrayOf(1),
                identity = MEMBER_IDENTITY_1
            )
        )
        dao.insert(
            TEST_MEMBER.copy(
                id = 0,
                displayName = "member2",
                publicKey = byteArrayOf(2),
                identity = MEMBER_IDENTITY_2
            )
        )

        operationDao.insert(
            OperationEntity(
                groupIdentity = GROUP_IDENTITY,
                operationAuthorIdentity = MEMBER_IDENTITY_1,
                createdAt = CREATED_AT,
                lamportClock = 1,
                type = "type1",
                payload = byteArrayOf(1),
                signature = byteArrayOf(11),
                identity = OPERATION_IDENTITY_1
            )
        )
        operationDao.insert(
            OperationEntity(
                groupIdentity = GROUP_IDENTITY,
                operationAuthorIdentity = MEMBER_IDENTITY_1,
                createdAt = CREATED_AT + 1,
                lamportClock = 2,
                type = "type2",
                payload = byteArrayOf(2),
                signature = byteArrayOf(22),
                identity = OPERATION_IDENTITY_2
            )
        )
        operationDao.insert(
            OperationEntity(
                groupIdentity = GROUP_IDENTITY,
                operationAuthorIdentity = MEMBER_IDENTITY_2,
                createdAt = CREATED_AT + 2,
                lamportClock = 3,
                type = "type3",
                payload = byteArrayOf(3),
                signature = byteArrayOf(33),
                identity = OPERATION_IDENTITY_3
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
                    it.groupIdentity == expected.groupIdentity &&
                    it.displayName == expected.displayName &&
                    it.role == expected.role &&
                    it.identity == expected.identity &&
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
        assertThat(actual.groupIdentity, equalTo(expected.groupIdentity))
        assertThat(actual.displayName, equalTo(expected.displayName))
        assertThat(actual.role, equalTo(expected.role))
        assertThat(actual.identity, equalTo(expected.identity))
        assertThat(actual.publicKey.contentEquals(expected.publicKey), equalTo(true))
    }

    companion object {
        private const val GROUP_NAME = "test_group"
        private const val MEMBER_NAME = "test_member"
        private const val EXPENSE_NAME = "test_expense"
        private const val ROLE = "test_role"
        private const val CREATED_AT = 12345L
        private const val CURRENCY = "PLN"

        private const val GROUP_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c50"
        private const val GROUP_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c51"

        private const val MEMBER_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c52"
        private const val MEMBER_IDENTITY_1 = "dfa4e836-190a-4292-a3fe-c516c1d99c53"
        private const val MEMBER_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c54"
        private const val MEMBER_IDENTITY_3 = "dfa4e836-190a-4292-a3fe-c516c1d99c55"
        private const val NOT_EXISTING_MEMBER_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c56"

        private const val EXPENSE_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c57"
        private const val EXPENSE_IDENTITY_1 = "dfa4e836-190a-4292-a3fe-c516c1d99c58"
        private const val EXPENSE_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c59"
        private const val EXPENSE_IDENTITY_3 = "dfa4e836-190a-4292-a3fe-c516c1d99c60"

        private const val SHARE_IDENTITY_1 = "dfa4e836-190a-4292-a3fe-c516c1d99c61"
        private const val SHARE_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c62"
        private const val SHARE_IDENTITY_3 = "dfa4e836-190a-4292-a3fe-c516c1d99c63"

        private const val OPERATION_IDENTITY_1 = "dfa4e836-190a-4292-a3fe-c516c1d99c64"
        private const val OPERATION_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c65"
        private const val OPERATION_IDENTITY_3 = "dfa4e836-190a-4292-a3fe-c516c1d99c66"

        private val TEST_GROUP = GroupEntity(
            createdAt = CREATED_AT,
            name = GROUP_NAME,
            identity = GROUP_IDENTITY
        )

        private val TEST_MEMBER = GroupMemberEntity(
            groupIdentity = GROUP_IDENTITY,
            displayName = MEMBER_NAME,
            publicKey = byteArrayOf(1, 2, 3, 4),
            role = ROLE,
            identity = MEMBER_IDENTITY
        )

        private val TEST_EXPENSE = ExpenseEntity(
            groupIdentity = GROUP_IDENTITY,
            paidByMemberIdentity = MEMBER_IDENTITY_1,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME,
            minorUnits = 100,
            currency = CURRENCY,
            identity = EXPENSE_IDENTITY
        )
    }
}