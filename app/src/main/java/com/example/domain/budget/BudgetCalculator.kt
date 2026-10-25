package com.example.domain.budget

import com.example.data.local.ExpenseEntity
import java.util.Calendar

data class MonthlyBudgetSummary(
  val totalSpentPaise: Long,
  val totalBudgetPaise: Long,
  val remainingPaise: Long,
  val percentageUsed: Float,
  val daysLeftInMonth: Int,
  val isOverBudget: Boolean,
  val isAbove75Percent: Boolean,
  val paceStatus: String // "Safe pace", "Caution pace", "Over budget"
)

object BudgetCalculator {

  fun calculateSummary(
    expenses: List<ExpenseEntity>,
    budgetPaise: Long,
    year: Int,
    month: Int
  ): MonthlyBudgetSummary {
    val totalSpent = expenses.sumOf { it.amountPaise }
    val remaining = (budgetPaise - totalSpent).coerceAtLeast(0L)
    val percentUsed = if (budgetPaise > 0) {
      (totalSpent.toFloat() / budgetPaise.toFloat()) * 100f
    } else {
      0f
    }

    val cal = Calendar.getInstance()
    val isCurrentMonth = (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) + 1 == month)
    val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val currentDay = if (isCurrentMonth) cal.get(Calendar.DAY_OF_MONTH) else 24 // default 24 as in designs
    val daysLeft = (maxDays - currentDay).coerceAtLeast(0)

    val isOver = totalSpent > budgetPaise
    val is75 = percentUsed >= 75f

    val pace = when {
      isOver -> "Over budget"
      is75 -> "Caution pace"
      else -> "Safe pace"
    }

    return MonthlyBudgetSummary(
      totalSpentPaise = totalSpent,
      totalBudgetPaise = budgetPaise,
      remainingPaise = remaining,
      percentageUsed = percentUsed,
      daysLeftInMonth = if (daysLeft > 0) daysLeft else 7,
      isOverBudget = isOver,
      isAbove75Percent = is75,
      paceStatus = pace
    )
  }
}
