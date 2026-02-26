package com.expense_management.domain.usecase.balance

import com.expense_management.domain.model.BalanceLine
import com.expense_management.domain.model.CurrencyCode
import com.expense_management.domain.model.Expense
import com.expense_management.domain.model.ExpenseShare
import com.expense_management.domain.model.MonetaryAmount
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.util.UUID

class ComputeBalanceUseCaseTest {

    private val computeBalance = ComputeBalanceUseCase()
    private val groupId = 1

    private val memberA = 1
    private val memberB = 2
    private val memberC = 3
    private val memberD = 4

    private fun pln(minorUnits: Long) =
        MonetaryAmount(minorUnits = minorUnits, currency = CurrencyCode.PLN)

    private fun List<BalanceLine>.normalized(): List<Triple<String, String, Long>> =
        this.map {
            Triple(
                it.fromMemberId.toString(),
                it.toMemberId.toString(),
                it.amount.minorUnits
            )
        }.sortedWith(compareBy({ it.first }, { it.second }, { it.third }))

    @Test
    fun `empty inputs returns empty list`() {
        val result = computeBalance(
            expenses = emptyList(),
            expenseShares = emptyList()
        )
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `two members - single expense split equally`() {
        val expense = Expense(
            id = 1,
            groupId = groupId,
            paidByMemberId = memberA,
            createdAt = Instant.now(),
            name = "Test",
            amount = pln(10_000)
        )

        val shares = listOf(
            ExpenseShare(
                id = 1,
                expenseId = expense.id,
                memberId = memberA,
                sharedAmount = pln(5_000)
            ),
            ExpenseShare(
                id = 2,
                expenseId = expense.id,
                memberId = memberB,
                sharedAmount = pln(5_000)
            )
        )

        val result = computeBalance(
            expenses = listOf(expense),
            expenseShares = shares
        ).normalized()

        val expected = listOf(
            Triple(memberB.toString(), memberA.toString(), 5_000L)
        ).sortedWith(compareBy({ it.first }, { it.second }, { it.third }))

        Assert.assertEquals(expected, result)
    }

    @Test
    fun `member can be debtor only - appears only in shares`() {
        val expense = Expense(
            id = 1,
            groupId = groupId,
            paidByMemberId = memberA,
            createdAt = Instant.now(),
            name = "Test Expense",
            amount = pln(12_000)
        )

        val shares = listOf(
            ExpenseShare(
                id = 1,
                expenseId = expense.id,
                memberId = memberA,
                sharedAmount = pln(4_000)
            ),
            ExpenseShare(
                id = 2,
                expenseId = expense.id,
                memberId = memberB,
                sharedAmount = pln(4_000)
            ),
            ExpenseShare(
                id = 3,
                expenseId = expense.id,
                memberId = memberD,
                sharedAmount = pln(4_000)
            ),
        )

        val result = computeBalance(
            expenses = listOf(expense),
            expenseShares = shares
        ).normalized()

        val expected = listOf(
            Triple(memberB.toString(), memberA.toString(), 4_000L),
            Triple(memberD.toString(), memberA.toString(), 4_000L)
        ).sortedWith(compareBy({ it.first }, { it.second }, { it.third }))

        Assert.assertEquals(expected, result)
    }

    @Test
    fun `three members - multiple expenses - netting produces valid transfers`() {
        /*
          Scenario:
          - A paid 90.00 split A=30 B=30 C=30
          - B paid 30.00 split A=10 B=10 C=10
          Totals:
            paid: A=90, B=30, C=0
            owed: A=40, B=40, C=40
            net:  A=+50, B=-10, C=-40
          Expected transfers:
            C -> A : 40
            B -> A : 10
         */

        val e1 = Expense(
            id = 1,
            groupId = groupId,
            paidByMemberId = memberA,
            createdAt = Instant.now(),
            name = "e1",
            amount = pln(9_000)
        )
        val e2 = Expense(
            id = 2,
            groupId = groupId,
            paidByMemberId = memberB,
            createdAt = Instant.now(),
            name = "e2",
            amount = pln(3_000)
        )

        val shares = listOf(
            ExpenseShare(id = 1, expenseId = e1.id, memberId = memberA, sharedAmount = pln(3_000)),
            ExpenseShare(id = 2, expenseId = e1.id, memberId = memberB, sharedAmount = pln(3_000)),
            ExpenseShare(id = 3, expenseId = e1.id, memberId = memberC, sharedAmount = pln(3_000)),
            ExpenseShare(id = 4, expenseId = e2.id, memberId = memberA, sharedAmount = pln(1_000)),
            ExpenseShare(id = 5, expenseId = e2.id, memberId = memberB, sharedAmount = pln(1_000)),
            ExpenseShare(id = 6, expenseId = e2.id, memberId = memberC, sharedAmount = pln(1_000)),
        )

        val result = computeBalance(
            expenses = listOf(e1, e2),
            expenseShares = shares
        ).normalized()

        val expected = listOf(
            Triple(memberB.toString(), memberA.toString(), 1_000L),
            Triple(memberC.toString(), memberA.toString(), 4_000L),
        ).sortedWith(compareBy({ it.first }, { it.second }, { it.third }))

        Assert.assertEquals(expected, result)
    }

    @Test
    fun `multiple creditors and debtors - sums settle to zero`() {
        /*
          Setup:
           - A paid 100 (A=20, B=20, C=20, D=40)
           - B paid 60  (A=20, B=20, C=20)
          Totals:
            paid: A=100, B=60, C=0, D=0
            owed: A=40, B=40, C=40, D=40
            net:  A=+60, B=+20, C=-40, D=-40
         */
        val e1 = Expense(
            id = 1,
            groupId = groupId,
            paidByMemberId = memberA,
            createdAt = Instant.now(),
            name = "e1",
            amount = pln(10_000)
        )
        val e2 = Expense(
            id = 2,
            groupId = groupId,
            paidByMemberId = memberB,
            createdAt = Instant.now(),
            name = "e2",
            amount = pln(6_000)
        )

        val shares = listOf(
            ExpenseShare(id = 1, expenseId = e1.id, memberId = memberA, sharedAmount = pln(2_000)),
            ExpenseShare(id = 2, expenseId = e1.id, memberId = memberB, sharedAmount = pln(2_000)),
            ExpenseShare(id = 3, expenseId = e1.id, memberId = memberC, sharedAmount = pln(2_000)),
            ExpenseShare(id = 4, expenseId = e1.id, memberId = memberD, sharedAmount = pln(4_000)),
            ExpenseShare(id = 5, expenseId = e2.id, memberId = memberA, sharedAmount = pln(2_000)),
            ExpenseShare(id = 6, expenseId = e2.id, memberId = memberB, sharedAmount = pln(2_000)),
            ExpenseShare(id = 7, expenseId = e2.id, memberId = memberC, sharedAmount = pln(2_000)),
        )

        val lines = computeBalance(
            expenses = listOf(e1, e2),
            expenseShares = shares
        )

        val totalTransferred = lines.sumOf { it.amount.minorUnits }
        Assert.assertEquals(8_000L, totalTransferred)

        val net = HashMap<Int, Long>()

        net.merge(memberA, 10_000, Long::plus)
        net.merge(memberB, 6_000, Long::plus)

        net.merge(memberA, -4_000, Long::plus)
        net.merge(memberB, -4_000, Long::plus)
        net.merge(memberC, -4_000, Long::plus)
        net.merge(memberD, -4_000, Long::plus)

        for (l in lines) {
            net.merge(l.fromMemberId, l.amount.minorUnits, Long::plus)
            net.merge(l.toMemberId, -l.amount.minorUnits, Long::plus)
        }

        Assert.assertEquals(0L, net[memberA])
        Assert.assertEquals(0L, net[memberB])
        Assert.assertEquals(0L, net[memberC])
        Assert.assertEquals(0L, net[memberD])

        Assert.assertTrue(lines.all { it.amount.minorUnits > 0 })
        Assert.assertTrue(lines.all { it.amount.currency == CurrencyCode.PLN })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when total paid differs from total owed`() {
        val expense = Expense(
            id = 1,
            groupId = groupId,
            paidByMemberId = memberA,
            createdAt = Instant.now(),
            name = "test",
            amount = pln(10_000)
        )

        val shares = listOf(
            ExpenseShare(
                id = 1,
                expenseId = expense.id,
                memberId = memberA,
                sharedAmount = pln(4_000)
            ),
            ExpenseShare(
                id = 2,
                expenseId = expense.id,
                memberId = memberB,
                sharedAmount = pln(4_000)
            ),
        )

        computeBalance(expenses = listOf(expense), expenseShares = shares)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when mixed currencies provided`() {
        val expense = Expense(
            id = 1,
            groupId = groupId,
            paidByMemberId = memberA,
            createdAt = Instant.now(),
            name = "test",
            amount = pln(10_000)
        )

        val shares = listOf(
            ExpenseShare(
                id = 1,
                expenseId = expense.id,
                memberId = memberA,
                sharedAmount = MonetaryAmount(5_000, CurrencyCode.EUR)
            ),
            ExpenseShare(
                id = 2,
                expenseId = expense.id,
                memberId = memberB,
                sharedAmount = MonetaryAmount(5_000, CurrencyCode.EUR)
            ),
        )

        computeBalance(expenses = listOf(expense), expenseShares = shares)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when expenses belong to more than one group`() {
        val g2 = UUID.fromString("00000000-0000-0000-0000-000000000002")

        val e1 = Expense(
            id = 1,
            groupId = groupId,
            paidByMemberId = memberA,
            createdAt = Instant.now(),
            name = "e1",
            amount = pln(5_000)
        )
        val e2 = Expense(
            id = 2,
            groupId = 2,
            paidByMemberId = memberA,
            createdAt = Instant.now(),
            name = "e2",
            amount = pln(5_000)
        )

        val shares = listOf(
            ExpenseShare(id = 1, expenseId = e1.id, memberId = memberA, sharedAmount = pln(2_500)),
            ExpenseShare(id = 2, expenseId = e1.id, memberId = memberB, sharedAmount = pln(2_500)),
            ExpenseShare(id = 3, expenseId = e2.id, memberId = memberA, sharedAmount = pln(2_500)),
            ExpenseShare(id = 4, expenseId = e2.id, memberId = memberB, sharedAmount = pln(2_500)),
        )

        computeBalance(expenses = listOf(e1, e2), expenseShares = shares)
    }
}