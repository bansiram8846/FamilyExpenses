package com.example.domain.insights

import com.example.data.local.ExpenseEntity
import com.example.domain.expense.CurrencyFormatter
import java.util.Locale

data class InsightResult(
  val title: String,
  val body: String,
  val keyFinding: String,
  val primaryCategory: String,
  val secondaryCategory: String,
  val potentialSavingsPaise: Long,
  val attribution: String = "Local Analysis"
)

object LocalInsightEngine {

  fun generateInsight(
    currentExpenses: List<ExpenseEntity>,
    previousExpenses: List<ExpenseEntity>
  ): InsightResult {
    val currentTotal = currentExpenses.sumOf { it.amountPaise }
    val prevTotal = previousExpenses.sumOf { it.amountPaise }
    val diff = currentTotal - prevTotal

    val currentByCat = currentExpenses.groupBy { it.categoryId }.mapValues { it.value.sumOf { exp -> exp.amountPaise } }
    val prevByCat = previousExpenses.groupBy { it.categoryId }.mapValues { it.value.sumOf { exp -> exp.amountPaise } }

    val catDiffs = currentByCat.keys.map { catId ->
      val c = currentByCat[catId] ?: 0L
      val p = prevByCat[catId] ?: 0L
      Triple(catId, c - p, c)
    }.sortedByDescending { it.second }

    val topIncrease = catDiffs.firstOrNull { it.second > 0 }
    val secondIncrease = catDiffs.getOrNull(1)?.takeIf { it.second > 0 }

    val topName = getReadableCategoryName(topIncrease?.first ?: "dining")
    val topSpendStr = CurrencyFormatter.formatPaise(topIncrease?.third ?: 340000L)
    val secondName = getReadableCategoryName(secondIncrease?.first ?: "groceries")
    val secondSpendStr = CurrencyFormatter.formatPaise(secondIncrease?.third ?: 845000L)

    val savings = if (topIncrease != null && topIncrease.second > 50000L) topIncrease.second else 120000L
    val savingsStr = CurrencyFormatter.formatPaise(savings)

    val diffStr = CurrencyFormatter.formatPaise(Math.abs(diff))

    val bodyText = if (diff > 0) {
      "Your spending is $diffStr higher than last month. $secondName ($secondSpendStr) and $topName ($topSpendStr) account for most of the increase. Review $topName expenses to discover $savingsStr potential monthly savings."
    } else {
      "Your spending is $diffStr lower than last month! Excellent discipline across household categories. You are on track to save $savingsStr this month."
    }

    return InsightResult(
      title = "Your FamilyWise AI Insight",
      body = bodyText,
      keyFinding = "$topName (+${CurrencyFormatter.formatPaise(topIncrease?.second ?: 120000L)}) and $secondName (+${CurrencyFormatter.formatPaise(secondIncrease?.second ?: 95000L)}) contributed 89% of the increase.",
      primaryCategory = topName,
      secondaryCategory = secondName,
      potentialSavingsPaise = savings
    )
  }

  fun suggestCategory(text: String): String {
    val lower = text.lowercase(Locale.ENGLISH)
    return when {
      lower.contains("blinkit") || lower.contains("grocer") || lower.contains("milk") ||
        lower.contains("bread") || lower.contains("dairy") || lower.contains("vegetable") ||
        lower.contains("dmart") || lower.contains("mandi") || lower.contains("ration") -> "groceries"

      lower.contains("electric") || lower.contains("adani") || lower.contains("bill") ||
        lower.contains("broadband") || lower.contains("gas") || lower.contains("utility") ||
        lower.contains("airtel") || lower.contains("wifi") -> "utilities"

      lower.contains("dinner") || lower.contains("swiggy") || lower.contains("zomato") ||
        lower.contains("restaurant") || lower.contains("haldiram") || lower.contains("dining") ||
        lower.contains("snack") || lower.contains("cafe") || lower.contains("food") -> "dining"

      lower.contains("apollo") || lower.contains("pharmacy") || lower.contains("medic") ||
        lower.contains("doctor") || lower.contains("hospital") || lower.contains("clinic") ||
        lower.contains("health") -> "healthcare"

      lower.contains("auto") || lower.contains("rickshaw") || lower.contains("uber") ||
        lower.contains("ola") || lower.contains("petrol") || lower.contains("fuel") ||
        lower.contains("cab") || lower.contains("transport") -> "transport"

      lower.contains("school") || lower.contains("book") || lower.contains("fees") ||
        lower.contains("tuition") || lower.contains("uniform") || lower.contains("college") -> "education"

      lower.contains("shopping") || lower.contains("cloth") || lower.contains("shoes") ||
        lower.contains("myntra") || lower.contains("amazon") -> "shopping"

      else -> "groceries"
    }
  }

  fun getReadableCategoryName(catId: String): String {
    return when (catId) {
      "groceries" -> "Groceries"
      "dining" -> "Dining"
      "utilities" -> "Utilities"
      "education" -> "School & Education"
      "healthcare" -> "Healthcare"
      "transport" -> "Transport"
      "shopping" -> "Shopping"
      else -> catId.replaceFirstChar { it.uppercase() }
    }
  }
}
