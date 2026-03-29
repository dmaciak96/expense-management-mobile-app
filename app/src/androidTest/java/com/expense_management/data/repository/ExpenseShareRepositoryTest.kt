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
import java.util.UUID
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

        val result = dao.getByIdentity(SHARE_IDENTITY).firstOrNull()
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

        val result = dao.getByIdentity(SHARE_IDENTITY).firstOrNull()
        assertThat(result, equalTo(updated))
    }

    @Test
    fun shouldGetAllExpenseShares() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        dao.insert(
            TEST_SHARE.copy(
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 100,
                identity = SHARE_IDENTITY_1
            )
        )
        dao.insert(
            TEST_SHARE.copy(
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 200,
                identity = SHARE_IDENTITY_2
            )
        )
        dao.insert(
            TEST_SHARE.copy(
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 300,
                identity = SHARE_IDENTITY_3
            )
        )

        val expected = listOf(
            TEST_SHARE.copy(
                id = 1,
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 100,
                identity = SHARE_IDENTITY_1
            ),
            TEST_SHARE.copy(
                id = 2,
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 200,
                identity = SHARE_IDENTITY_2
            ),
            TEST_SHARE.copy(
                id = 3,
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 300,
                identity = SHARE_IDENTITY_3
            )
        )

        val result = repository.getAll()
            .first { it is OperationResult.Success } as OperationResult.Success<List<ExpenseShareEntity>>

        assertThat(result.data, containsInAnyOrder(*expected.toTypedArray()))
    }

    @Test
    fun shouldGetExpenseShareByIdentity() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        dao.insert(
            TEST_SHARE.copy(
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 100,
                identity = SHARE_IDENTITY_1
            )
        )
        dao.insert(
            TEST_SHARE.copy(
                memberIdentity = MEMBER_IDENTITY_2,
                minorUnits = 200,
                identity = SHARE_IDENTITY_2
            )
        )
        dao.insert(
            TEST_SHARE.copy(
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 300,
                identity = SHARE_IDENTITY_3
            )
        )

        val result1 = repository.getByIdentity(UUID.fromString(SHARE_IDENTITY_1))
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseShareEntity?>
        val result2 = repository.getByIdentity(UUID.fromString(SHARE_IDENTITY_2))
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseShareEntity?>
        val result3 = repository.getByIdentity(UUID.fromString(SHARE_IDENTITY_3))
            .first { it is OperationResult.Success } as OperationResult.Success<ExpenseShareEntity?>
        val emptyResult =
            repository.getByIdentity(UUID.fromString("dfa4e836-190a-4292-a3fe-c516c1d99c91"))
                .first { it is OperationResult.Success } as OperationResult.Success<ExpenseShareEntity?>

        assertThat(
            result1.data,
            equalTo(
                TEST_SHARE.copy(
                    id = 1,
                    memberIdentity = MEMBER_IDENTITY,
                    minorUnits = 100,
                    identity = SHARE_IDENTITY_1
                )
            )
        )
        assertThat(
            result2.data,
            equalTo(
                TEST_SHARE.copy(
                    id = 2,
                    memberIdentity = MEMBER_IDENTITY_2,
                    minorUnits = 200,
                    identity = SHARE_IDENTITY_2
                )
            )
        )
        assertThat(
            result3.data,
            equalTo(
                TEST_SHARE.copy(
                    id = 3,
                    memberIdentity = MEMBER_IDENTITY,
                    minorUnits = 300,
                    identity = SHARE_IDENTITY_3
                )
            )
        )
        assertThat(emptyResult.data, equalTo(null))
    }

    @Test
    fun shouldGetExpenseSharesByExpenseId() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        dao.insert(
            TEST_SHARE.copy(
                expenseIdentity = EXPENSE_IDENTITY,
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 100,
                identity = SHARE_IDENTITY_1
            )
        )
        dao.insert(
            TEST_SHARE.copy(
                expenseIdentity = EXPENSE_IDENTITY,
                memberIdentity = MEMBER_IDENTITY_2,
                minorUnits = 200,
                identity = SHARE_IDENTITY_2
            )
        )
        dao.insert(
            TEST_SHARE.copy(
                expenseIdentity = EXPENSE_IDENTITY_2,
                memberIdentity = UUID.randomUUID().toString(),
                minorUnits = 300,
                identity = SHARE_IDENTITY_3
            )
        )

        val result = repository.getExpenseSharesByExpenseIdentity(UUID.fromString(EXPENSE_IDENTITY))
            .first { it is OperationResult.Success } as OperationResult.Success<List<ExpenseShareEntity>>

        assertThat(
            result.data,
            containsInAnyOrder(
                TEST_SHARE.copy(
                    id = 1,
                    expenseIdentity = EXPENSE_IDENTITY,
                    memberIdentity = MEMBER_IDENTITY,
                    minorUnits = 100,
                    identity = SHARE_IDENTITY_1
                ),
                TEST_SHARE.copy(
                    id = 2,
                    expenseIdentity = EXPENSE_IDENTITY,
                    memberIdentity = MEMBER_IDENTITY_2,
                    minorUnits = 200,
                    identity = SHARE_IDENTITY_2
                )
            )
        )
    }

    @Test
    fun shouldGetExpenseSharesByGroupMemberIdentity() = runTest(timeout = 1.seconds) {
        prepareRequiredData()

        dao.insert(
            TEST_SHARE.copy(
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 100,
                identity = SHARE_IDENTITY_1
            )
        )
        dao.insert(
            TEST_SHARE.copy(
                memberIdentity = MEMBER_IDENTITY,
                minorUnits = 200,
                identity = SHARE_IDENTITY_2
            )
        )
        dao.insert(
            TEST_SHARE.copy(
                memberIdentity = MEMBER_IDENTITY_2,
                minorUnits = 300,
                identity = SHARE_IDENTITY_3
            )
        )

        val result =
            repository.getExpenseSharesByGroupMemberIdentity(UUID.fromString(MEMBER_IDENTITY))
                .first { it is OperationResult.Success } as OperationResult.Success<List<ExpenseShareEntity>>

        assertThat(
            result.data,
            containsInAnyOrder(
                TEST_SHARE.copy(
                    id = 1,
                    memberIdentity = MEMBER_IDENTITY,
                    minorUnits = 100,
                    identity = SHARE_IDENTITY_1
                ),
                TEST_SHARE.copy(
                    id = 2,
                    memberIdentity = MEMBER_IDENTITY,
                    minorUnits = 200,
                    identity = SHARE_IDENTITY_2
                )
            )
        )
    }

    private suspend fun prepareRequiredData() {
        val groupDao = db.groupDao()
        val expenseDao = db.expenseDao()

        groupDao.insert(TEST_GROUP)
        expenseDao.insert(TEST_EXPENSE)
        expenseDao.insert(
            TEST_EXPENSE.copy(
                name = "expense2",
                paidByMemberIdentity = MEMBER_IDENTITY_2,
                identity = EXPENSE_IDENTITY_2
            )
        )
    }

    companion object {
        private const val GROUP_NAME = "test_group"
        private const val EXPENSE_NAME = "test_expense"
        private const val CREATED_AT = 12345L
        private const val MINOR_UNITS = 1234L
        private const val CURRENCY = "PLN"

        private const val GROUP_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c37"
        private const val MEMBER_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c47"
        private const val MEMBER_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c12"
        private const val EXPENSE_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c38"
        private const val EXPENSE_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c39"
        private const val SHARE_IDENTITY = "dfa4e836-190a-4292-a3fe-c516c1d99c40"
        private const val SHARE_IDENTITY_1 = "dfa4e836-190a-4292-a3fe-c516c1d99c41"
        private const val SHARE_IDENTITY_2 = "dfa4e836-190a-4292-a3fe-c516c1d99c42"
        private const val SHARE_IDENTITY_3 = "dfa4e836-190a-4292-a3fe-c516c1d99c43"

        private val TEST_GROUP = GroupEntity(
            createdAt = CREATED_AT,
            name = GROUP_NAME,
            identity = GROUP_IDENTITY
        )

        private val TEST_EXPENSE = ExpenseEntity(
            groupIdentity = GROUP_IDENTITY,
            paidByMemberIdentity = MEMBER_IDENTITY,
            createdAt = CREATED_AT,
            name = EXPENSE_NAME,
            minorUnits = 999,
            currency = CURRENCY,
            identity = EXPENSE_IDENTITY
        )

        private val TEST_SHARE = ExpenseShareEntity(
            expenseIdentity = EXPENSE_IDENTITY,
            memberIdentity = MEMBER_IDENTITY,
            minorUnits = MINOR_UNITS,
            currency = CURRENCY,
            identity = SHARE_IDENTITY
        )
    }
}
