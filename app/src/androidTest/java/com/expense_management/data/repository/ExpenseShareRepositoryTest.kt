package com.expense_management.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.expense_management.core.common.OperationResult
import com.expense_management.data.DatabaseTestSuite
import com.expense_management.data.dao.ExpenseShareDao
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
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class ExpenseShareRepositoryTest : DatabaseTestSuite() {
    private lateinit var repository: ExpenseShareRepository
    private lateinit var dao: ExpenseShareDao

    @Before
    fun createRepository() {
        dao = db.expenseShareDao()
        repository = ExpenseShareRepository(dao)
    }

    @Test
    fun shouldSaveNewExpenseShare() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        val expected = TEST_SHARE.copy(id = 1)

        assertThat(
            repository.insert(TEST_SHARE),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getById(1).firstOrNull()
        assertThat(result, equalTo(expected))
    }

    @Test
    fun shouldDeleteExpenseShare() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        dao.insert(TEST_SHARE)
        val toRemove = TEST_SHARE.copy(id = 1)

        assertThat(
            repository.delete(toRemove),
            instanceOf(OperationResult.Success::class.java)
        )

        assertThat(dao.getAll().first().size, equalTo(0))
    }

    @Test
    fun shouldUpdateExpenseShare() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        dao.insert(TEST_SHARE)
        val updated = TEST_SHARE.copy(
            id = 1,
            minorUnits = 999,
            currency = "EUR"
        )

        assertThat(
            repository.update(updated),
            instanceOf(OperationResult.Success::class.java)
        )

        val result = dao.getById(1).firstOrNull()
        assertThat(result, equalTo(updated))
    }

    @Test
    fun shouldGetAllExpenseShares() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        dao.insert(TEST_SHARE.copy(memberId = 1, minorUnits = 100))
        dao.insert(TEST_SHARE.copy(memberId = 2, minorUnits = 200))
        dao.insert(TEST_SHARE.copy(memberId = 3, minorUnits = 300))

        val expected = listOf(
            TEST_SHARE.copy(id = 1, memberId = 1, minorUnits = 100),
            TEST_SHARE.copy(id = 2, memberId = 2, minorUnits = 200),
            TEST_SHARE.copy(id = 3, memberId = 3, minorUnits = 300)
        )

        val result = repository.getAll()
            .first { it is OperationResult.Success } as OperationResult.Success<List<ExpenseShareEntity>>

        assertThat(result.data, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun shouldGetExpenseShareById() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        dao.insert(TEST_SHARE.copy(memberId = 1, minorUnits = 100))
        dao.insert(TEST_SHARE.copy(memberId = 2, minorUnits = 200))
        dao.insert(TEST_SHARE.copy(memberId = 3, minorUnits = 300))

        val result1 = repository.getById(1)
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseShareEntity?>
        val result2 = repository.getById(2)
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseShareEntity?>
        val result3 = repository.getById(3)
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseShareEntity?>
        val emptyResult = repository.getById(999)
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseShareEntity?>

        assertThat(result1.data, equalTo(TEST_SHARE.copy(id = 1, memberId = 1, minorUnits = 100)))
        assertThat(result2.data, equalTo(TEST_SHARE.copy(id = 2, memberId = 2, minorUnits = 200)))
        assertThat(result3.data, equalTo(TEST_SHARE.copy(id = 3, memberId = 3, minorUnits = 300)))
        assertThat(emptyResult.data, equalTo(null))
    }

    @Test
    fun shouldGetExpenseSharesByExpenseId() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        dao.insert(TEST_SHARE.copy(expenseId = 1, memberId = 1, minorUnits = 100))
        dao.insert(TEST_SHARE.copy(expenseId = 1, memberId = 2, minorUnits = 200))
        dao.insert(TEST_SHARE.copy(expenseId = 2, memberId = 3, minorUnits = 300))

        val result = repository.getExpenseSharesByExpenseId(1)
            .first { it is OperationResult.Success } as OperationResult.Success<List<ExpenseShareEntity>>

        assertThat(
            result.data,
            containsInAnyOrder(
                TEST_SHARE.copy(id = 1, expenseId = 1, memberId = 1, minorUnits = 100),
                TEST_SHARE.copy(id = 2, expenseId = 1, memberId = 2, minorUnits = 200)
            )
        )
    }

    @Test
    fun shouldGetExpenseSharesByGroupMemberId() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        dao.insert(TEST_SHARE.copy(memberId = 11, minorUnits = 100))
        dao.insert(TEST_SHARE.copy(memberId = 11, minorUnits = 200))
        dao.insert(TEST_SHARE.copy(memberId = 22, minorUnits = 300))

        val result = repository.getExpenseSharesByGroupMemberId(11)
            .first { it is OperationResult.Success } as OperationResult.Success<List<ExpenseShareEntity>>

        assertThat(
            result.data,
            containsInAnyOrder(
                TEST_SHARE.copy(id = 1, memberId = 11, minorUnits = 100),
                TEST_SHARE.copy(id = 2, memberId = 11, minorUnits = 200)
            )
        )
    }

    private suspend fun prepareRequiredData() {
        val groupDao = db.groupDao()
        val expenseDao = db.expenseDao()

        groupDao.insert(TEST_GROUP)
        expenseDao.insert(TEST_EXPENSE)
        expenseDao.insert(TEST_EXPENSE.copy(name = "expense2", paidByMemberId = 22))
    }

    companion object {
        private const val GROUP_NAME = "test_group"
        private const val EXPENSE_NAME = "test_expense"
        private const val CREATED_AT = 12345L
        private const val MINOR_UNITS = 1234L
        private const val CURRENCY = "PLN"

        private val TEST_GROUP = GroupEntity(
            createdAt = CREATED_AT,
            name = GROUP_NAME
        )

        private val TEST_EXPENSE = ExpenseEntity(
            groupId = 1,
            paidByMemberId = 11,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME,
            minorUnits = 999,
            currency = CURRENCY
        )

        private val TEST_SHARE = ExpenseShareEntity(
            expenseId = 1,
            memberId = 11,
            minorUnits = MINOR_UNITS,
            currency = CURRENCY
        )
    }
}
