package com.example.ai

import com.example.data.local.CategoryEntity
import com.example.data.local.ExpenseEntity

object AIContextBuilder {

  /**
   * Builds safe, anonymized, aggregated financial summary payload.
   * Strips all PII, real names, passwords, PINs, and account numbers.
   */
  fun buildAggregatedContext(
    period: String,
    currentExpenses: List<ExpenseEntity>,
    previousExpenses: List<ExpenseEntity>,
    budgetPaise: Long,
    categories: List<CategoryEntity>
  ): String {
    val currentTotal = currentExpenses.sumOf { it.amountPaise }
    val previousTotal = previousExpenses.sumOf { it.amountPaise }

    val catMap = categories.associateBy { it.id }
    val currentByCat = currentExpenses.groupBy { it.categoryId }.mapValues { it.value.sumOf { e -> e.amountPaise } }
    val prevByCat = previousExpenses.groupBy { it.categoryId }.mapValues { it.value.sumOf { e -> e.amountPaise } }

    val catEntries = currentByCat.map { (catId, cPaise) ->
      val pPaise = prevByCat[catId] ?: 0L
      val catName = catMap[catId]?.name ?: catId
      val diff = cPaise - pPaise
      """    {
      "category": "$catName",
      "currentMonthPaise": $cPaise,
      "currentMonthRupees": ${cPaise / 100},
      "previousMonthPaise": $pPaise,
      "previousMonthRupees": ${pPaise / 100},
      "variancePaise": $diff
    }"""
    }.joinToString(",\n")

    return """{
  "currency": "INR",
  "period": "$period",
  "currentMonthTotalPaise": $currentTotal,
  "currentMonthTotalRupees": ${currentTotal / 100},
  "previousMonthTotalPaise": $previousTotal,
  "previousMonthTotalRupees": ${previousTotal / 100},
  "monthlyBudgetPaise": $budgetPaise,
  "monthlyBudgetRupees": ${budgetPaise / 100},
  "anonymizationProtocol": "FamilyWise-ZeroPII-v1",
  "categories": [
$catEntries
  ]
}"""
  }
}
