package com.expense_management.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.expense_management.core.common.OperationResult
import com.expense_management.data.DatabaseTestSuite
import com.expense_management.data.dao.GroupDao
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.GroupEntity
import com.expense_management.data.model.GroupMemberEntity
import com.expense_management.data.model.OperationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.seconds
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class GroupRepositoryTest : DatabaseTestSuite() {
    private lateinit var repository: GroupRepository
    private lateinit var dao: GroupDao

    @Before
    fun createRepository() {
        dao = db.groupDao()
        repository = GroupRepository(dao)
    }

    @Test
    fun shouldSaveNewGroup() = runTest(timeout = 1.seconds) {
        val expected = GroupEntity(
            id = 1,
            createdAt = TEST_GROUP.createdAt,
            name = TEST_GROUP.name,
            identity = TEST_GROUP.identity
        )

        assertThat(
            repository.insert(TEST_GROUP),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getByIdentity(GROUP_IDENTITY).firstOrNull()
        assertThat(result, equalTo(expected))
    }

    @Test
    fun shouldDeleteGroup() = runTest(timeout = 1.seconds) {
        val toRemove = GroupEntity(
            id = 1,
            createdAt = TEST_GROUP.createdAt,
            name = TEST_GROUP.name,
            identity = TEST_GROUP.identity
        )

        dao.insert(TEST_GROUP)

        assertThat(
            repository.delete(toRemove),
            instanceOf(OperationResult.Success::class.java)
        )

        assertThat(dao.getAll().first().size, equalTo(0))
    }

    @Test
    fun shouldUpdateGroup() = runTest(timeout = 1.seconds) {
        val expectedName = "new_group_name"
        val toUpdate = GroupEntity(
            id = 1,
            createdAt = TEST_GROUP.createdAt,
            name = expectedName,
            identity = TEST_GROUP.identity
        )

        dao.insert(TEST_GROUP)

        assertThat(
            repository.update(toUpdate),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getByIdentity(GROUP_IDENTITY).firstOrNull()
        assertThat(result, equalTo(toUpdate))
    }

    @Test
    fun shouldGetAllGroups() = runTest(timeout = 1.seconds) {
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "1",
                createdAt = TEST_GROUP.createdAt + 1,
                identity = GROUP_IDENTITY_1
            )
        )
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "2",
                createdAt = TEST_GROUP.createdAt + 2,
                identity = GROUP_IDENTITY_2
            )
        )
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "3",
                createdAt = TEST_GROUP.createdAt + 3,
                identity = GROUP_IDENTITY_3
            )
        )

        val expectedList = listOf(
            GroupEntity(
                id = 1,
                createdAt = TEST_GROUP.createdAt + 1,
                name = TEST_GROUP.name + "1",
                identity = GROUP_IDENTITY_1
            ),
            GroupEntity(
                id = 2,
                createdAt = TEST_GROUP.createdAt + 2,
                name = TEST_GROUP.name + "2",
                identity = GROUP_IDENTITY_2
            ),
            GroupEntity(
                id = 3,
                createdAt = TEST_GROUP.createdAt + 3,
                name = TEST_GROUP.name + "3",
                identity = GROUP_IDENTITY_3
            )
        )

        val result = repository.getAll()
            .first { it is OperationResult.Success } as OperationResult.Success<List<GroupEntity>>

        assertThat(result.data, containsInAnyOrder(*expectedList.toTypedArray()))
    }

    @Test
    fun shouldGetGroupByIdentity() = runTest(timeout = 1.seconds) {
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "1",
                createdAt = TEST_GROUP.createdAt + 1,
                identity = GROUP_IDENTITY_1
            )
        )
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "2",
                createdAt = TEST_GROUP.createdAt + 2,
                identity = GROUP_IDENTITY_2
            )
        )
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "3",
                createdAt = TEST_GROUP.createdAt + 3,
                identity = GROUP_IDENTITY_3
            )
        )

        val expectedOne = GroupEntity(
            id = 1,
            createdAt = TEST_GROUP.createdAt + 1,
            name = TEST_GROUP.name + "1",
            identity = GROUP_IDENTITY_1
        )
        val expectedTwo = GroupEntity(
            id = 2,
            createdAt = TEST_GROUP.createdAt + 2,
            name = TEST_GROUP.name + "2",
            identity = GROUP_IDENTITY_2
        )
        val expectedThree = GroupEntity(
            id = 3,
            createdAt = TEST_GROUP.createdAt + 3,
            name = TEST_GROUP.name + "3",
            identity = GROUP_IDENTITY_3
        )

        val resultOne = repository.getByIdentity(UUID.fromString(GROUP_IDENTITY_1))
            .first { it is OperationResult.Success } as OperationResult.Success<GroupEntity?>
        val resultTwo = repository.getByIdentity(UUID.fromString(GROUP_IDENTITY_2))
            .first { it is OperationResult.Success } as OperationResult.Success<GroupEntity?>
        val resultThree = repository.getByIdentity(UUID.fromString(GROUP_IDENTITY_3))
            .first { it is OperationResult.Success } as OperationResult.Success<GroupEntity?>
        val emptyResult = repository.getByIdentity(UUID.fromString(NOT_EXISTING_GROUP_IDENTITY))
            .first { it is OperationResult.Success } as OperationResult.Success<GroupEntity?>

        assertThat(resultOne.data, equalTo(expectedOne))
        assertThat(resultTwo.data, equalTo(expectedTwo))
        assertThat(resultThree.data, equalTo(expectedThree))
        assertThat(emptyResult.data, equalTo(null))
    }

    @Test
    fun shouldReturnGroupsAndMembers() = runTest(timeout = 1.seconds) {
        val memberDao = db.groupMemberDao()
        val group1 = GroupEntity(
            id = 1,
            name = TEST_GROUP.name + "1",
            createdAt = TEST_GROUP.createdAt + 1,
            identity = GROUP_IDENTITY_1
        )
        val group2 = GroupEntity(
            id = 2,
            name = TEST_GROUP.name + "2",
            createdAt = TEST_GROUP.createdAt + 2,
            identity = GROUP_IDENTITY_2
        )
        val member1 = GroupMemberEntity(
            id = 1,
            groupIdentity = GROUP_IDENTITY_1,
            displayName = MEMBER_NAME + 1,
            publicKey = byteArrayOf(1, 2, 3, 4),
            role = ROLE + 1,
            identity = MEMBER_IDENTITY_1
        )
        val member2 = GroupMemberEntity(
            id = 2,
            groupIdentity = GROUP_IDENTITY_1,
            displayName = MEMBER_NAME + 11,
            publicKey = byteArrayOf(1, 2, 3, 4, 5),
            role = ROLE + 11,
            identity = MEMBER_IDENTITY_2
        )
        val member3 = GroupMemberEntity(
            id = 3,
            groupIdentity = GROUP_IDENTITY_2,
            displayName = MEMBER_NAME + 2,
            publicKey = byteArrayOf(1, 2, 3, 4, 5, 6),
            role = ROLE + 2,
            identity = MEMBER_IDENTITY_3
        )
        val member4 = GroupMemberEntity(
            id = 4,
            groupIdentity = GROUP_IDENTITY_2,
            displayName = MEMBER_NAME + 22,
            publicKey = byteArrayOf(1, 2, 3, 4, 5, 6, 7),
            role = ROLE + 22,
            identity = MEMBER_IDENTITY_4
        )

        dao.insert(group1.copy(id = 0))
        dao.insert(group2.copy(id = 0))
        memberDao.insert(member1.copy(id = 0))
        memberDao.insert(member2.copy(id = 0))
        memberDao.insert(member3.copy(id = 0))
        memberDao.insert(member4.copy(id = 0))

        val result = repository.getGroupAndGroupMembers()
            .first { it is OperationResult.Success } as OperationResult.Success<Map<GroupEntity, List<GroupMemberEntity>>>

        assertThat(result.data.size, equalTo(2))
        assertThat(result.data.keys, containsInAnyOrder(group1, group2))
        assertThat(result.data[group1]?.size, equalTo(2))
        assertThat(result.data[group2]?.size, equalTo(2))
    }

    @Test
    fun shouldReturnGroupsAndExpenses() = runTest(timeout = 1.seconds) {
        val expenseDao = db.expenseDao()
        val group1 = GroupEntity(
            id = 1,
            name = TEST_GROUP.name + "1",
            createdAt = TEST_GROUP.createdAt + 1,
            identity = GROUP_IDENTITY_1
        )
        val group2 = GroupEntity(
            id = 2,
            name = TEST_GROUP.name + "2",
            createdAt = TEST_GROUP.createdAt + 2,
            identity = GROUP_IDENTITY_2
        )

        val expense1 = ExpenseEntity(
            id = 1,
            groupIdentity = GROUP_IDENTITY_1,
            paidByMemberIdentity = MEMBER_IDENTITY_1,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME + "1",
            minorUnits = MINOR_UNITS + 1,
            currency = CURRENCY,
            identity = EXPENSE_IDENTITY_1
        )
        val expense2 = ExpenseEntity(
            id = 2,
            groupIdentity = GROUP_IDENTITY_1,
            paidByMemberIdentity = MEMBER_IDENTITY_2,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME + "2",
            minorUnits = MINOR_UNITS + 2,
            currency = CURRENCY,
            identity = EXPENSE_IDENTITY_2
        )
        val expense3 = ExpenseEntity(
            id = 3,
            groupIdentity = GROUP_IDENTITY_2,
            paidByMemberIdentity = MEMBER_IDENTITY_3,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME + "3",
            minorUnits = MINOR_UNITS + 3,
            currency = CURRENCY,
            identity = EXPENSE_IDENTITY_3
        )
        val expense4 = ExpenseEntity(
            id = 4,
            groupIdentity = GROUP_IDENTITY_2,
            paidByMemberIdentity = MEMBER_IDENTITY_4,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME + "4",
            minorUnits = MINOR_UNITS + 4,
            currency = CURRENCY,
            identity = EXPENSE_IDENTITY_4
        )

        dao.insert(group1.copy(id = 0))
        dao.insert(group2.copy(id = 0))
        expenseDao.insert(expense1.copy(id = 0))
        expenseDao.insert(expense2.copy(id = 0))
        expenseDao.insert(expense3.copy(id = 0))
        expenseDao.insert(expense4.copy(id = 0))

        val result = repository.getGroupAndExpenses()
            .first { it is OperationResult.Success } as OperationResult.Success<Map<GroupEntity, List<ExpenseEntity>>>

        assertThat(result.data.size, equalTo(2))
        assertThat(result.data.keys, containsInAnyOrder(group1, group2))
        assertThat(result.data[group1], containsInAnyOrder(expense1, expense2))
        assertThat(result.data[group2], containsInAnyOrder(expense3, expense4))
    }

    @Test
    fun shouldReturnGroupsAndOperations() = runTest(timeout = 1.seconds) {
        val operationDao = db.operationDao()
        val group1 = GroupEntity(
            id = 1,
            name = TEST_GROUP.name + "1",
            createdAt = TEST_GROUP.createdAt + 1,
            identity = GROUP_IDENTITY_1
        )
        val group2 = GroupEntity(
            id = 2,
            name = TEST_GROUP.name + "2",
            createdAt = TEST_GROUP.createdAt + 2,
            identity = GROUP_IDENTITY_2
        )

        val operation1 = OperationEntity(
            id = 1,
            groupIdentity = GROUP_IDENTITY_1,
            operationAuthorIdentity = MEMBER_IDENTITY_1,
            createdAt = CREATED_AT + 1,
            lamportClock = LAMPORT_CLOCK + 1,
            type = OPERATION_TYPE,
            payload = byteArrayOf(1, 2, 3, 4),
            signature = byteArrayOf(1, 2, 3, 4),
            identity = OPERATION_IDENTITY_1
        )
        val operation2 = OperationEntity(
            id = 2,
            groupIdentity = GROUP_IDENTITY_1,
            operationAuthorIdentity = MEMBER_IDENTITY_2,
            createdAt = CREATED_AT + 2,
            lamportClock = LAMPORT_CLOCK + 2,
            type = OPERATION_TYPE,
            payload = byteArrayOf(1, 2, 3, 4),
            signature = byteArrayOf(1, 2, 3, 4),
            identity = OPERATION_IDENTITY_2
        )
        val operation3 = OperationEntity(
            id = 3,
            groupIdentity = GROUP_IDENTITY_2,
            operationAuthorIdentity = MEMBER_IDENTITY_3,
            createdAt = CREATED_AT + 3,
            lamportClock = LAMPORT_CLOCK + 3,
            type = OPERATION_TYPE,
            payload = byteArrayOf(1, 2, 3, 4),
            signature = byteArrayOf(1, 2, 3, 4),
            identity = OPERATION_IDENTITY_3
        )
        val operation4 = OperationEntity(
            id = 4,
            groupIdentity = GROUP_IDENTITY_2,
            operationAuthorIdentity = MEMBER_IDENTITY_4,
            createdAt = CREATED_AT + 4,
            lamportClock = LAMPORT_CLOCK + 4,
            type = OPERATION_TYPE,
            payload = byteArrayOf(1, 2, 3, 4),
            signature = byteArrayOf(1, 2, 3, 4),
            identity = OPERATION_IDENTITY_4
        )

        dao.insert(group1.copy(id = 0))
        dao.insert(group2.copy(id = 0))
        operationDao.insert(operation1.copy(id = 0))
        operationDao.insert(operation2.copy(id = 0))
        operationDao.insert(operation3.copy(id = 0))
        operationDao.insert(operation4.copy(id = 0))

        val result = repository.getGroupAndOperations()
            .first { it is OperationResult.Success } as OperationResult.Success<Map<GroupEntity, List<OperationEntity>>>

        assertThat(result.data.size, equalTo(2))
        assertThat(result.data.keys, containsInAnyOrder(group1, group2))
        assertThat(result.data[group1]?.size, equalTo(2))
        assertThat(result.data[group2]?.size, equalTo(2))
    }

    companion object {
        private const val NAME = "test_group"
        private const val MEMBER_NAME = "group_member"
        private const val EXPENSE_NAME = "expense"
        private const val ROLE = "role"
        private const val CREATED_AT = 12345L
        private const val MINOR_UNITS = 1234L
        private const val CURRENCY = "test_currency"
        private const val OPERATION_TYPE = "test_type"
        private const val LAMPORT_CLOCK = 1234L

        private const val GROUP_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c70"
        private const val GROUP_IDENTITY_1 = "dfa4e836-190a-4292-a3fe-c516c1d99c71"
        private const val GROUP_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c72"
        private const val GROUP_IDENTITY_3 = "dfa4e836-190a-4292-a3fe-c516c1d99c73"
        private const val NOT_EXISTING_GROUP_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c74"

        private const val MEMBER_IDENTITY_1 = "dfa4e836-190a-4292-a3fe-c516c1d99c75"
        private const val MEMBER_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c76"
        private const val MEMBER_IDENTITY_3 = "dfa4e836-190a-4292-a3fe-c516c1d99c77"
        private const val MEMBER_IDENTITY_4 = "dfa4e836-190a-4292-a3fe-c516c1d99c78"

        private const val EXPENSE_IDENTITY_1 = "dfa4e836-190a-4292-a3fe-c516c1d99c79"
        private const val EXPENSE_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c80"
        private const val EXPENSE_IDENTITY_3 = "dfa4e836-190a-4292-a3fe-c516c1d99c81"
        private const val EXPENSE_IDENTITY_4 = "dfa4e836-190a-4292-a3fe-c516c1d99c82"

        private const val OPERATION_IDENTITY_1 = "dfa4e836-190a-4292-a3fe-c516c1d99c83"
        private const val OPERATION_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c84"
        private const val OPERATION_IDENTITY_3 = "dfa4e836-190a-4292-a3fe-c516c1d99c85"
        private const val OPERATION_IDENTITY_4 = "dfa4e836-190a-4292-a3fe-c516c1d99c86"

        private val TEST_GROUP = GroupEntity(
            createdAt = CREATED_AT,
            name = NAME,
            identity = GROUP_IDENTITY
        )
    }
}