package com.expense_management.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.expense_management.core.common.OperationResult
import com.expense_management.data.DatabaseTestSuite
import com.expense_management.data.dao.ExpenseDao
import com.expense_management.data.model.ExpenseEntity
import com.expense_management.data.model.ExpenseShareEntity
import com.expense_management.data.model.GroupEntity
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
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class ExpenseRepositoryTest : DatabaseTestSuite() {
    private lateinit var repository: ExpenseRepository
    private lateinit var dao: ExpenseDao

    @Before
    fun createRepository() {
        dao = db.expenseDao()
        repository = ExpenseRepository(dao)
    }

    @Test
    fun shouldSaveNewExpense() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        val expected = TEST_EXPENSE.copy(id = 1)

        assertThat(
            repository.insert(TEST_EXPENSE),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getByIdentity(EXPENSE_IDENTITY).firstOrNull()

        assertThat(result, equalTo(expected))
    }

    @Test
    fun shouldDeleteExpense() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        val toRemove = TEST_EXPENSE.copy(id = 1)
        dao.insert(TEST_EXPENSE)

        assertThat(
            repository.delete(toRemove),
            instanceOf(OperationResult.Success::class.java)
        )

        assertThat(dao.getAll().first().size, equalTo(0))
    }

    @Test
    fun shouldUpdateExpense() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_EXPENSE)

        val updated = TEST_EXPENSE.copy(
            id = 1,
            name = "updated_expense",
            minorUnits = 9999L
        )

        assertThat(
            repository.update(updated),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getByIdentity(EXPENSE_IDENTITY).firstOrNull()
        assertThat(result, equalTo(updated))
    }

    @Test
    fun shouldGetAllExpenses() = runTest(timeout = 1.seconds) {
        val identity1 = "75ab6318-6abe-406a-9246-89422aa729d3"
        val identity2 = "78fedbbf-2989-435e-b458-687f0069fa75"
        val identity3 = "62b5ecc9-64fc-4f88-b093-a197b7d87784"
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_EXPENSE.copy(name = "expense1", createdAt = CREATED_AT + 1, identity = identity1))
        dao.insert(TEST_EXPENSE.copy(name = "expense2", createdAt = CREATED_AT + 2, identity = identity2))
        dao.insert(TEST_EXPENSE.copy(name = "expense3", createdAt = CREATED_AT + 3, identity = identity3))

        val expected = listOf(
            TEST_EXPENSE.copy(id = 1, name = "expense1", createdAt = CREATED_AT + 1, identity = identity1),
            TEST_EXPENSE.copy(id = 2, name = "expense2", createdAt = CREATED_AT + 2, identity = identity2),
            TEST_EXPENSE.copy(id = 3, name = "expense3", createdAt = CREATED_AT + 3, identity = identity3)
        )

        val result = repository.getAll()
            .first { it is OperationResult.Success } as OperationResult.Success<List<ExpenseEntity>>

        assertThat(result.data, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun shouldGetExpenseByIdentity() = runTest(timeout = 1.seconds) {
        val identity1 = "75ab6318-6abe-406a-9246-89422aa729d3"
        val identity2 = "78fedbbf-2989-435e-b458-687f0069fa75"
        val identity3 = "62b5ecc9-64fc-4f88-b093-a197b7d87784"
        val notExisting = "62b5ecc9-64fc-4f88-b093-a197b7d87785"

        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        dao.insert(TEST_EXPENSE.copy(name = "expense1", createdAt = CREATED_AT + 1, identity = identity1))
        dao.insert(TEST_EXPENSE.copy(name = "expense2", createdAt = CREATED_AT + 2, identity = identity2))
        dao.insert(TEST_EXPENSE.copy(name = "expense3", createdAt = CREATED_AT + 3, identity = identity3))

        val result1 = repository.getByIdentity(UUID.fromString(identity1))
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseEntity?>
        val result2 = repository.getByIdentity(UUID.fromString(identity2))
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseEntity?>
        val result3 = repository.getByIdentity(UUID.fromString(identity3))
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseEntity?>
        val emptyResult = repository.getByIdentity(UUID.fromString(notExisting))
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseEntity?>

        assertThat(
            result1.data,
            equalTo(
                TEST_EXPENSE.copy(
                    id = 1,
                    name = "expense1",
                    createdAt = CREATED_AT + 1,
                    identity = identity1
                )
            )
        )
        assertThat(
            result2.data,
            equalTo(
                TEST_EXPENSE.copy(
                    id = 2,
                    name = "expense2",
                    createdAt = CREATED_AT + 2,
                    identity = identity2
                )
            )
        )
        assertThat(
            result3.data,
            equalTo(
                TEST_EXPENSE.copy(
                    id = 3,
                    name = "expense3",
                    createdAt = CREATED_AT + 3,
                    identity = identity3
                )
            )
        )
        assertThat(emptyResult.data, equalTo(null))
    }

    @Test
    fun shouldReturnExpensesAndExpenseShares() = runTest(timeout = 1.seconds) {
        val identity1 = "75ab6318-6abe-406a-9246-89422aa729d3"
        val identity2 = "78fedbbf-2989-435e-b458-687f0069fa75"
        val identity3 = "62b5ecc9-64fc-4f88-b093-a197b7d87784"
        val identity4 = "62b5ecc9-64fc-4f88-b093-a197b7d87785"

        val groupDao = db.groupDao()
        val shareDao = db.expenseShareDao()

        groupDao.insert(TEST_GROUP)

        val expense1 = TEST_EXPENSE.copy(id = 1, name = "expense1")
        val expense2 = TEST_EXPENSE.copy(id = 2, name = "expense2", minorUnits = MINOR_UNITS + 100)

        val share1 = ExpenseShareEntity(
            expenseId = 1,
            memberId = 1,
            minorUnits = 500,
            currency = CURRENCY,
            identity = identity1
        )
        val share2 = ExpenseShareEntity(
            expenseId = 1,
            memberId = 2,
            minorUnits = 700,
            currency = CURRENCY,
            identity = identity2
        )
        val share3 = ExpenseShareEntity(
            expenseId = 2,
            memberId = 3,
            minorUnits = 300,
            currency = CURRENCY,
            identity = identity3
        )
        val share4 = ExpenseShareEntity(
            expenseId = 2,
            memberId = 4,
            minorUnits = 800,
            currency = CURRENCY,
            identity = identity4
        )

        dao.insert(expense1.copy(id = 0))
        dao.insert(expense2.copy(id = 0))
        shareDao.insert(share1)
        shareDao.insert(share2)
        shareDao.insert(share3)
        shareDao.insert(share4)

        val result = repository.getExpenseAndExpenseShares()
            .first { it is OperationResult.Success } as OperationResult.Success<Map<ExpenseEntity, List<ExpenseShareEntity>>>

        assertThat(result.data.size, equalTo(2))
        assertThat(result.data.keys, containsInAnyOrder(expense1, expense2))
        assertThat(result.data[expense1]?.size, equalTo(2))
        assertThat(result.data[expense2]?.size, equalTo(2))
    }

    @Test
    fun shouldGetExpensesByGroupId() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)
        groupDao.insert(TEST_GROUP.copy(name = "group2", createdAt = CREATED_AT + 100))

        val expense1 = TEST_EXPENSE.copy(name = "expense1", groupId = 1)
        val expense2 = TEST_EXPENSE.copy(name = "expense2", groupId = 1)
        val expense3 = TEST_EXPENSE.copy(name = "expense3", groupId = 2)

        dao.insert(expense1)
        dao.insert(expense2)
        dao.insert(expense3)

        val result = repository.getExpensesByGroupId(1)
            .first { it is OperationResult.Success } as OperationResult.Success<List<ExpenseEntity>>

        assertThat(
            result.data,
            containsInAnyOrder(
                expense1.copy(id = 1),
                expense2.copy(id = 2)
            )
        )
    }

    @Test
    fun shouldGetExpensesByGroupMemberId() = runTest(timeout = 1.seconds) {
        val groupDao = db.groupDao()
        groupDao.insert(TEST_GROUP)

        val expense1 = TEST_EXPENSE.copy(name = "expense1", paidByMemberId = 11)
        val expense2 = TEST_EXPENSE.copy(name = "expense2", paidByMemberId = 11)
        val expense3 = TEST_EXPENSE.copy(name = "expense3", paidByMemberId = 22)

        dao.insert(expense1)
        dao.insert(expense2)
        dao.insert(expense3)

        val result = repository.getExpensesByGroupMemberId(11)
            .first { it is OperationResult.Success } as OperationResult.Success<List<ExpenseEntity>>

        assertThat(
            result.data,
            containsInAnyOrder(
                expense1.copy(id = 1),
                expense2.copy(id = 2)
            )
        )
    }

    companion object {
        private const val GROUP_NAME = "test_group"
        private const val EXPENSE_NAME = "test_expense"
        private const val CREATED_AT = 12345L
        private const val MINOR_UNITS = 1234L
        private const val CURRENCY = "PLN"
        private const val GROUP_ID = 1
        private const val PAID_BY_MEMBER_ID = 11
        private const val GROUP_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c37"
        private const val EXPENSE_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c38"

        private val TEST_GROUP = GroupEntity(
            createdAt = CREATED_AT,
            name = GROUP_NAME,
            identity = GROUP_IDENTITY
        )

        private val TEST_EXPENSE = ExpenseEntity(
            groupId = GROUP_ID,
            paidByMemberId = PAID_BY_MEMBER_ID,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME,
            minorUnits = MINOR_UNITS,
            currency = CURRENCY,
            identity = EXPENSE_IDENTITY
        )
    }
}
