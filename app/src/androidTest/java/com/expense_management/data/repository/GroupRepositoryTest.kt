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
            name = TEST_GROUP.name
        )

        assertThat(
            repository.insert(TEST_GROUP),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getById(1).firstOrNull()

        assertThat(
            result, equalTo(
                expected
            )
        )
    }

    @Test
    fun shouldDeleteGroup() = runTest(timeout = 1.seconds) {
        val toRemove = GroupEntity(
            id = 1,
            createdAt = TEST_GROUP.createdAt,
            name = TEST_GROUP.name
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
            name = expectedName
        )

        dao.insert(TEST_GROUP)

        assertThat(
            repository.update(
                toUpdate
            ), instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getById(1).firstOrNull()
        assertThat(result, equalTo(toUpdate))
    }

    @Test
    fun shouldGetAllGroups() = runTest(timeout = 1.seconds) {
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "1",
                createdAt = TEST_GROUP.createdAt + 1
            )
        )
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "2",
                createdAt = TEST_GROUP.createdAt + 2
            )
        )
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "3",
                createdAt = TEST_GROUP.createdAt + 3
            )
        )

        val expectedList = listOf(
            GroupEntity(
                id = 1,
                createdAt = TEST_GROUP.createdAt + 1,
                name = TEST_GROUP.name + "1"
            ),
            GroupEntity(
                id = 2,
                createdAt = TEST_GROUP.createdAt + 2,
                name = TEST_GROUP.name + "2"
            ),
            GroupEntity(
                id = 3,
                createdAt = TEST_GROUP.createdAt + 3,
                name = TEST_GROUP.name + "3"
            )
        )

        val result = repository.getAll()
            .first { it is OperationResult.Success } as OperationResult.Success<List<GroupEntity>>

        assertThat(result.data, containsInAnyOrder(*expectedList.toTypedArray()))
    }

    @Test
    fun shouldGetGroupById() = runTest(timeout = 1.seconds) {
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "1",
                createdAt = TEST_GROUP.createdAt + 1
            )
        )
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "2",
                createdAt = TEST_GROUP.createdAt + 2
            )
        )
        dao.insert(
            TEST_GROUP.copy(
                name = TEST_GROUP.name + "3",
                createdAt = TEST_GROUP.createdAt + 3
            )
        )

        val expectedOne = GroupEntity(
            id = 1,
            createdAt = TEST_GROUP.createdAt + 1,
            name = TEST_GROUP.name + "1"
        )
        val expectedTwo = GroupEntity(
            id = 2,
            createdAt = TEST_GROUP.createdAt + 2,
            name = TEST_GROUP.name + "2"
        )
        val expectedThree = GroupEntity(
            id = 3,
            createdAt = TEST_GROUP.createdAt + 3,
            name = TEST_GROUP.name + "3"
        )

        val resultOne = repository.getById(1)
            .first { it is OperationResult.Success } as OperationResult.Success<GroupEntity?>
        val resultTwo = repository.getById(2)
            .first { it is OperationResult.Success } as OperationResult.Success<GroupEntity?>
        val resultThree = repository.getById(3)
            .first { it is OperationResult.Success } as OperationResult.Success<GroupEntity?>
        val emptyResult = repository.getById(123)
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
            createdAt = TEST_GROUP.createdAt + 1
        )
        val group2 = GroupEntity(
            id = 2,
            name = TEST_GROUP.name + "2",
            createdAt = TEST_GROUP.createdAt + 2
        )
        val member1 = GroupMemberEntity(
            id = 1,
            groupId = 1,
            displayName = MEMBER_NAME + 1,
            publicKey = byteArrayOf(1, 2, 3, 4),
            role = ROLE + 1
        )
        val member2 = GroupMemberEntity(
            id = 2,
            groupId = 1,
            displayName = MEMBER_NAME + 11,
            publicKey = byteArrayOf(1, 2, 3, 4, 5),
            role = ROLE + 11
        )
        val member3 = GroupMemberEntity(
            id = 3,
            groupId = 2,
            displayName = MEMBER_NAME + 2,
            publicKey = byteArrayOf(1, 2, 3, 4, 5, 6),
            role = ROLE + 2
        )
        val member4 = GroupMemberEntity(
            id = 4,
            groupId = 2,
            displayName = MEMBER_NAME + 22,
            publicKey = byteArrayOf(1, 2, 3, 4, 5, 6, 7),
            role = ROLE + 22
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
            createdAt = TEST_GROUP.createdAt + 1
        )
        val group2 = GroupEntity(
            id = 2,
            name = TEST_GROUP.name + "2",
            createdAt = TEST_GROUP.createdAt + 2
        )

        val expense1 = ExpenseEntity(
            id = 1,
            groupId = group1.id,
            paidByMemberId = PAID_BY + 1,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME + "1",
            minorUnits = MINOR_UNITS + 1,
            currency = CURRENCY
        )
        val expense2 = ExpenseEntity(
            id = 2,
            groupId = group1.id,
            paidByMemberId = PAID_BY + 2,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME + "2",
            minorUnits = MINOR_UNITS + 2,
            currency = CURRENCY
        )
        val expense3 = ExpenseEntity(
            id = 3,
            groupId = group2.id,
            paidByMemberId = PAID_BY + 3,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME + "3",
            minorUnits = MINOR_UNITS + 3,
            currency = CURRENCY
        )
        val expense4 = ExpenseEntity(
            id = 4,
            groupId = group2.id,
            paidByMemberId = PAID_BY + 4,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME + "4",
            minorUnits = MINOR_UNITS + 4,
            currency = CURRENCY
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
            createdAt = TEST_GROUP.createdAt + 1
        )
        val group2 = GroupEntity(
            id = 2,
            name = TEST_GROUP.name + "2",
            createdAt = TEST_GROUP.createdAt + 2
        )

        val operation1 = OperationEntity(
            id = 1,
            groupId = group1.id,
            operationAuthorId = OPERATION_AUTHOR_ID + 1,
            CREATED_AT + 1,
            lamportClock = LAMPORT_CLOCK + 1,
            type = OPERATION_TYPE,
            payload = byteArrayOf(1, 2, 3, 4),
            signature = byteArrayOf(1, 2, 3, 4)
        )
        val operation2 = OperationEntity(
            id = 2,
            groupId = group1.id,
            operationAuthorId = OPERATION_AUTHOR_ID + 2,
            CREATED_AT + 2,
            lamportClock = LAMPORT_CLOCK + 2,
            type = OPERATION_TYPE,
            payload = byteArrayOf(1, 2, 3, 4),
            signature = byteArrayOf(1, 2, 3, 4)
        )
        val operation3 = OperationEntity(
            id = 3,
            groupId = group2.id,
            operationAuthorId = OPERATION_AUTHOR_ID + 3,
            CREATED_AT + 3,
            lamportClock = LAMPORT_CLOCK + 3,
            type = OPERATION_TYPE,
            payload = byteArrayOf(1, 2, 3, 4),
            signature = byteArrayOf(1, 2, 3, 4)
        )
        val operation4 = OperationEntity(
            id = 4,
            groupId = group2.id,
            operationAuthorId = OPERATION_AUTHOR_ID + 4,
            CREATED_AT + 4,
            lamportClock = LAMPORT_CLOCK + 4,
            type = OPERATION_TYPE,
            payload = byteArrayOf(1, 2, 3, 4),
            signature = byteArrayOf(1, 2, 3, 4)
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
        private const val PAID_BY = 11
        private const val MINOR_UNITS = 1234L
        private const val CURRENCY = "test_currency"
        private const val OPERATION_TYPE = "test_type"
        private const val LAMPORT_CLOCK = 1234L
        private const val OPERATION_AUTHOR_ID = 11
        private val TEST_GROUP = GroupEntity(
            createdAt = CREATED_AT,
            name = NAME
        )
    }
}