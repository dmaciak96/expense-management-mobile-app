package com.expense_management.domain.usecase.balance

import com.expense_management.domain.model.BalanceLine
import com.expense_management.domain.model.CurrencyCode
import com.expense_management.domain.model.Expense
import com.expense_management.domain.model.ExpenseShare
import com.expense_management.domain.model.MonetaryAmount
import kotlin.collections.iterator

// TODO: Add this class as DI component
class ComputeBalanceUseCase {
    operator fun invoke(
        expenses: List<Expense>,
        expenseShares: List<ExpenseShare>
    ): List<BalanceLine> {
        checkIfExpensesAreFromSingleGroup(expenses)
        checkIfAllExpenseSharesPointingToExpense(expenses, expenseShares)
        val currency = getCurrency(expenses, expenseShares)

        val netByMemberId = mutableMapOf<Int, Long>()
        for (expense in expenses) {
            netByMemberId.merge(expense.paidByMemberId, expense.amount.minorUnits, Long::plus)
        }

        for (expenseShare in expenseShares) {
            netByMemberId.merge(
                expenseShare.memberId,
                -expenseShare.sharedAmount.minorUnits,
                Long::plus
            )
        }

        val creditors = mutableListOf<ParticipantNet>()
        val debtors = mutableListOf<ParticipantNet>()
        for ((memberId, net) in netByMemberId) {
            when {
                net > 0L -> creditors.add(ParticipantNet(memberId, net))
                net < 0L -> debtors.add(ParticipantNet(memberId, -net))
                else -> Unit
            }
        }

        if (creditors.isEmpty() || debtors.isEmpty()) return emptyList()

        creditors.sortByDescending { it.amount }
        debtors.sortByDescending { it.amount }

        val result = mutableListOf<BalanceLine>()

        var ci = 0
        var di = 0
        while (ci < creditors.size && di < debtors.size) {
            val creditor = creditors[ci]
            val debtor = debtors[di]

            val transferMinor = minOf(creditor.amount, debtor.amount)
            require(transferMinor > 0L)

            result.add(
                BalanceLine(
                    fromMemberId = debtor.memberId,
                    toMemberId = creditor.memberId,
                    amount = MonetaryAmount(transferMinor, currency)
                )
            )

            creditor.amount -= transferMinor
            debtor.amount -= transferMinor

            if (creditor.amount == 0L) ci++
            if (debtor.amount == 0L) di++
        }

        val remainingCredit = creditors.drop(ci).sumOf { it.amount }
        val remainingDebt = debtors.drop(di).sumOf { it.amount }
        require(remainingCredit == 0L && remainingDebt == 0L) {
            "Settlement failed: remainingCredit=$remainingCredit, remainingDebt=$remainingDebt"
        }

        return result
    }

    private fun checkIfExpensesAreFromSingleGroup(expenses: List<Expense>) {
        val firstGroupId = expenses.firstOrNull()?.groupId ?: return
        require(expenses.all { it.groupId == firstGroupId }) {
            "Expected expenses from exactly one group, but found more than 1"
        }
    }

    private fun checkIfAllExpenseSharesPointingToExpense(
        expenses: List<Expense>,
        expenseShares: List<ExpenseShare>
    ) {
        if (expenses.isEmpty() && expenseShares.isEmpty()) return

        val expenseIds = expenses.asSequence().map { it.id }.toHashSet()
        require(expenseShares.all { it.expenseId in expenseIds }) {
            "Expected expenseShares to reference existing expenses"
        }
    }

    private fun getCurrency(
        expenses: List<Expense>,
        expenseShares: List<ExpenseShare>
    ): CurrencyCode {
        val firstCurrency = expenses.firstOrNull()?.amount?.currency
            ?: expenseShares.firstOrNull()?.sharedAmount?.currency
            ?: return CurrencyCode.PLN

        require(expenses.all { it.amount.currency == firstCurrency } &&
                expenseShares.all { it.sharedAmount.currency == firstCurrency }) {
            "Expected all entries to use a single currency code"
        }

        return firstCurrency
    }

    private data class ParticipantNet(
        val memberId: Int,
        var amount: Long
    )
}