package com.example

import com.example.ai.AIContextBuilder
import com.example.ai.AIAssistantService
import com.example.data.local.CategoryEntity
import com.example.data.local.ExpenseEntity
import com.example.data.local.MemberEntity
import com.example.domain.budget.BudgetCalculator
import com.example.domain.expense.CurrencyFormatter
import com.example.domain.insights.LocalInsightEngine
import com.example.domain.report.ReportCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FamilyWiseUnitTest {

  @Test
  fun testCurrencyFormattingAndPaiseConversion() {
    // ₹24,650 is 2465000 paise
    assertEquals("₹24,650", CurrencyFormatter.formatPaise(2465000L))
    assertEquals("₹1,850", CurrencyFormatter.formatPaise(185000L))
    assertEquals("₹500", CurrencyFormatter.formatPaise(50000L))

    // Parse input back to paise
    assertEquals(2465000L, CurrencyFormatter.parseToPaise("24,650"))
    assertEquals(145000L, CurrencyFormatter.parseToPaise("1450"))
    assertEquals(10000L, CurrencyFormatter.parseToPaise("₹100"))

    // Compact format
    assertEquals("₹24.7k", CurrencyFormatter.formatCompact(2465000L))
    assertEquals("₹5.8k", CurrencyFormatter.formatCompact(580000L))
  }

  @Test
  fun testBudgetSummaryCalculations() {
    val sampleExpenses = listOf(
      ExpenseEntity(id = "1", title = "Groceries", amountPaise = 1000000L, dateEpochMillis = 0L, categoryId = "groceries", paidByMemberId = "rajesh", enteredByMemberId = "rajesh", paymentMethod = "UPI"),
      ExpenseEntity(id = "2", title = "Utilities", amountPaise = 500000L, dateEpochMillis = 0L, categoryId = "utilities", paidByMemberId = "rajesh", enteredByMemberId = "rajesh", paymentMethod = "UPI"),
      ExpenseEntity(id = "3", title = "Dining", amountPaise = 965000L, dateEpochMillis = 0L, categoryId = "dining", paidByMemberId = "priya", enteredByMemberId = "priya", paymentMethod = "UPI")
    )
    val totalBudget = 3500000L // ₹35,000

    val summary = BudgetCalculator.calculateSummary(sampleExpenses, totalBudget, 2024, 10)

    assertEquals(2465000L, summary.totalSpentPaise)
    assertEquals(1035000L, summary.remainingPaise)
    assertEquals(70.42857f, summary.percentageUsed, 0.1f)
    assertFalse(summary.isOverBudget)
    assertFalse(summary.isAbove75Percent)
    assertEquals("Safe pace", summary.paceStatus)
  }

  @Test
  fun testBudgetExceededAndAlertThresholds() {
    val overExpenses = listOf(
      ExpenseEntity(id = "1", title = "Big Event", amountPaise = 3600000L, dateEpochMillis = 0L, categoryId = "other", paidByMemberId = "rajesh", enteredByMemberId = "rajesh", paymentMethod = "UPI")
    )
    val totalBudget = 3500000L

    val summary = BudgetCalculator.calculateSummary(overExpenses, totalBudget, 2024, 10)

    assertTrue(summary.isOverBudget)
    assertTrue(summary.isAbove75Percent)
    assertEquals("Over budget", summary.paceStatus)
    assertEquals(0L, summary.remainingPaise)
  }

  @Test
  fun testMonthComparisonWithZeroPreviousMonth() {
    val currentExpenses = listOf(
      ExpenseEntity(id = "1", title = "Rent", amountPaise = 1500000L, dateEpochMillis = 0L, categoryId = "utilities", paidByMemberId = "rajesh", enteredByMemberId = "rajesh", paymentMethod = "UPI")
    )
    val emptyPrevious = emptyList<ExpenseEntity>()

    val comparison = ReportCalculator.calculateComparison(currentExpenses, emptyPrevious)

    assertEquals(1500000L, comparison.currentMonthTotalPaise)
    assertEquals(0L, comparison.previousMonthTotalPaise)
    assertEquals(1500000L, comparison.absoluteVariancePaise)
    assertNull(comparison.percentageVariance)
    assertEquals("New spending", comparison.formattedVarianceText)
  }

  @Test
  fun testMonthComparisonVarianceMath() {
    val currentExpenses = listOf(
      ExpenseEntity(id = "1", title = "Oct", amountPaise = 2465000L, dateEpochMillis = 0L, categoryId = "groceries", paidByMemberId = "rajesh", enteredByMemberId = "rajesh", paymentMethod = "UPI")
    )
    val prevExpenses = listOf(
      ExpenseEntity(id = "2", title = "Sep", amountPaise = 2225000L, dateEpochMillis = 0L, categoryId = "groceries", paidByMemberId = "rajesh", enteredByMemberId = "rajesh", paymentMethod = "UPI")
    )

    val comparison = ReportCalculator.calculateComparison(currentExpenses, prevExpenses)

    assertEquals(240000L, comparison.absoluteVariancePaise)
    assertTrue(comparison.isHigher)
    assertNotNull(comparison.percentageVariance)
    assertEquals(10.7865, comparison.percentageVariance!!, 0.05)
  }

  @Test
  fun testCategoryCategorizationHeuristic() {
    assertEquals("groceries", LocalInsightEngine.suggestCategory("Blinkit organic vegetables"))
    assertEquals("groceries", LocalInsightEngine.suggestCategory("Mother Dairy Milk & Bread"))
    assertEquals("utilities", LocalInsightEngine.suggestCategory("Adani Electricity Bill"))
    assertEquals("dining", LocalInsightEngine.suggestCategory("Haldiram Family Dinner"))
    assertEquals("healthcare", LocalInsightEngine.suggestCategory("Apollo Pharmacy prescription"))
    assertEquals("education", LocalInsightEngine.suggestCategory("School books & semester fees"))
    assertEquals("transport", LocalInsightEngine.suggestCategory("Auto Rickshaw college trip"))
  }

  @Test
  fun testAIContextZeroPII() {
    val expenses = listOf(
      ExpenseEntity(id = "1", title = "Secret Private Text", amountPaise = 845000L, dateEpochMillis = 0L, categoryId = "groceries", paidByMemberId = "rajesh", enteredByMemberId = "rajesh", paymentMethod = "UPI", merchant = "Confidential Bank XYZ")
    )
    val categories = listOf(
      CategoryEntity(id = "groceries", name = "Groceries", iconName = "shopping_basket", colorHex = "#006A61")
    )

    val contextJson = AIContextBuilder.buildAggregatedContext("October 2024", expenses, emptyList(), 3500000L, categories)

    // Context must include aggregated figures
    assertTrue(contextJson.contains("845000"))
    assertTrue(contextJson.contains("Groceries"))
    // Context must NOT include personal bank or merchant names
    assertFalse(contextJson.contains("Confidential Bank XYZ"))
    assertFalse(contextJson.contains("Secret Private Text"))
  }

  @Test
  fun testAIAssistantStructuredAnswers() {
    val octExpenses = listOf(
      ExpenseEntity(id = "1", title = "Dining", amountPaise = 340000L, dateEpochMillis = 0L, categoryId = "dining", paidByMemberId = "rajesh", enteredByMemberId = "rajesh", paymentMethod = "UPI"),
      ExpenseEntity(id = "2", title = "Groceries", amountPaise = 845000L, dateEpochMillis = 0L, categoryId = "groceries", paidByMemberId = "priya", enteredByMemberId = "priya", paymentMethod = "UPI")
    )
    val sepExpenses = listOf(
      ExpenseEntity(id = "3", title = "Dining", amountPaise = 220000L, dateEpochMillis = 0L, categoryId = "dining", paidByMemberId = "rajesh", enteredByMemberId = "rajesh", paymentMethod = "UPI"),
      ExpenseEntity(id = "4", title = "Groceries", amountPaise = 750000L, dateEpochMillis = 0L, categoryId = "groceries", paidByMemberId = "priya", enteredByMemberId = "priya", paymentMethod = "UPI")
    )

    val answer = AIAssistantService.answerQuestion(
      query = "Why did our expenses increase compared to last month?",
      currentExpenses = octExpenses,
      previousExpenses = sepExpenses,
      budgetPaise = 3500000L,
      isOnlineAI = false
    )

    assertNotNull(answer.structuredResponse)
    assertTrue(answer.structuredResponse!!.title.contains("Spending Variance"))
    assertEquals(3, answer.structuredResponse!!.tableRows.size)
  }
}
