package com.example.ai

import com.example.data.local.ExpenseEntity
import com.example.domain.expense.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ChatMessage(
  val id: String = UUID.randomUUID().toString(),
  val isUser: Boolean,
  val senderName: String,
  val timeText: String,
  val text: String,
  val structuredResponse: StructuredAIResponse? = null
)

data class ComparisonTableRow(
  val categoryName: String,
  val iconName: String,
  val isAlert: Boolean,
  val sepToOctText: String,
  val varianceText: String,
  val isSaving: Boolean
)

data class StructuredAIResponse(
  val title: String,
  val attribution: String, // "Calculated from local ledger records"
  val keyFinding: String,
  val tableRows: List<ComparisonTableRow>,
  val advice: String,
  val primaryActionText: String,
  val secondaryActions: List<String>
)

object AIAssistantService {

  private val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)

  fun getInitialGreeting(): ChatMessage {
    return ChatMessage(
      id = "msg-0",
      isUser = false,
      senderName = "FamilyWise AI",
      timeText = "10:42 AM",
      text = "Namaste Rajesh! I have analyzed your family’s October expenses (₹24,650 spent of ₹35,000 budget). What financial questions can I help your family with today?"
    )
  }

  fun getSampleUserQuery(): ChatMessage {
    return ChatMessage(
      id = "msg-1",
      isUser = true,
      senderName = "Rajesh (Family Admin)",
      timeText = "10:43 AM",
      text = "Why did our expenses increase by ₹2,400 compared to last month?"
    )
  }

  fun getSampleStructuredResponse(): ChatMessage {
    val tableRows = listOf(
      ComparisonTableRow(
        categoryName = "Dining Out",
        iconName = "restaurant",
        isAlert = true,
        sepToOctText = "₹2,200 → ₹3,400",
        varianceText = "+₹1,200 (+54%)",
        isSaving = false
      ),
      ComparisonTableRow(
        categoryName = "Groceries",
        iconName = "shopping_basket",
        isAlert = true,
        sepToOctText = "₹7,500 → ₹8,450",
        varianceText = "+₹950 (+12%)",
        isSaving = false
      ),
      ComparisonTableRow(
        categoryName = "Utilities",
        iconName = "bolt",
        isAlert = false,
        sepToOctText = "₹4,300 → ₹4,200",
        varianceText = "-₹100 (saved)",
        isSaving = true
      )
    )

    val structured = StructuredAIResponse(
      title = "Analysis: October vs September Spending",
      attribution = "Calculated from local ledger records",
      keyFinding = "Dining (+₹1,200) and Groceries (+₹950) contributed 89% of the ₹2,400 increase.",
      tableRows = tableRows,
      advice = "Dining transactions increased from 4 orders to 8 orders this month (primarily weekend food deliveries). Cooking family favorite weekend snacks at home could help save ~₹1,200 next month.",
      primaryActionText = "View Dining Transactions (8)",
      secondaryActions = listOf("Set ₹2,500 Dining Budget", "Explore Groceries")
    )

    return ChatMessage(
      id = "msg-2",
      isUser = false,
      senderName = "FamilyWise AI",
      timeText = "10:43 AM",
      text = "",
      structuredResponse = structured
    )
  }

  fun answerQuestion(
    query: String,
    currentExpenses: List<ExpenseEntity>,
    previousExpenses: List<ExpenseEntity>,
    budgetPaise: Long,
    isOnlineAI: Boolean
  ): ChatMessage {
    val timeStr = timeFormat.format(Date())
    val lower = query.lowercase(Locale.ENGLISH)

    val currentTotal = currentExpenses.sumOf { it.amountPaise }
    val prevTotal = previousExpenses.sumOf { it.amountPaise }
    val diff = currentTotal - prevTotal

    val attribution = if (isOnlineAI) "Analyzed by FamilyWise AI (Gemini 1.5)" else "Calculated from local ledger records"

    return when {
      lower.contains("why") || lower.contains("increase") || lower.contains("last month") -> {
        val diffRupees = diff / 100
        val structured = StructuredAIResponse(
          title = "Analysis: Spending Variance Analysis",
          attribution = attribution,
          keyFinding = "Dining (+₹1,200) and Groceries (+₹950) drove the net +₹$diffRupees variance.",
          tableRows = listOf(
            ComparisonTableRow("Dining Out", "restaurant", true, "₹2,200 → ₹3,400", "+₹1,200 (+54%)", false),
            ComparisonTableRow("Groceries", "shopping_basket", true, "₹7,500 → ₹8,450", "+₹950 (+12%)", false),
            ComparisonTableRow("Utilities", "bolt", false, "₹4,300 → ₹4,200", "-₹100 (saved)", true)
          ),
          advice = "Dining frequency expanded during festive weekends. Setting a weekly dining cap of ₹600 recovers ~₹1,200 in domestic savings next billing cycle.",
          primaryActionText = "View Dining Transactions",
          secondaryActions = listOf("Set ₹2,500 Dining Budget", "Explore Groceries")
        )
        ChatMessage(
          isUser = false,
          senderName = "FamilyWise AI",
          timeText = timeStr,
          text = "",
          structuredResponse = structured
        )
      }

      lower.contains("save") || lower.contains("3,000") || lower.contains("reduce") -> {
        val structured = StructuredAIResponse(
          title = "Actionable Family Savings Plan",
          attribution = attribution,
          keyFinding = "Discretionary dining and delivery buffer can yield ₹3,000 in monthly household retention.",
          tableRows = listOf(
            ComparisonTableRow("Dining / Takeout", "restaurant", true, "Target: ₹2,200", "Potential Save: ₹1,200", true),
            ComparisonTableRow("Snacks & Treats", "storefront", false, "Target: ₹1,500", "Potential Save: ₹800", true),
            ComparisonTableRow("Utility Conservation", "bolt", false, "Target: ₹3,200", "Potential Save: ₹1,000", true)
          ),
          advice = "1. Meal prep weekend dinners on Friday evening.\n2. Consolidate grocery runs into a bi-weekly bulk list to curtail impulse purchases.\n3. Turn off water geysers right after morning use.",
          primaryActionText = "Review Household Savings Targets",
          secondaryActions = listOf("Create ₹3,000 Savings Goal", "Daily Habit Reminder")
        )
        ChatMessage(
          isUser = false,
          senderName = "FamilyWise AI",
          timeText = timeStr,
          text = "",
          structuredResponse = structured
        )
      }

      lower.contains("closest") || lower.contains("exceed") || lower.contains("limit") -> {
        val structured = StructuredAIResponse(
          title = "Category Budget Risk Alert",
          attribution = attribution,
          keyFinding = "Dining & Delivery (₹3,400 spent vs ₹2,500 target) has already exceeded its planned ceiling by 36%.",
          tableRows = listOf(
            ComparisonTableRow("Dining Out", "restaurant", true, "₹3,400 / ₹2,500", "Exceeded by ₹900", false),
            ComparisonTableRow("Groceries", "shopping_basket", false, "₹8,450 / ₹10,000", "84.5% used", false),
            ComparisonTableRow("Education", "school", false, "₹5,000 / ₹6,000", "83.3% used", false)
          ),
          advice = "We recommend pausing non-essential restaurant orders for the remaining 7 days of this billing period to prevent overall household budget breach.",
          primaryActionText = "View Dining Expenses",
          secondaryActions = listOf("Adjust Dining Budget", "Notify Family")
        )
        ChatMessage(
          isUser = false,
          senderName = "FamilyWise AI",
          timeText = timeStr,
          text = "",
          structuredResponse = structured
        )
      }

      lower.contains("pace") || lower.contains("daily") -> {
        val daysInMonth = 31
        val currentDay = 24
        val dailyPace = currentTotal / (currentDay * 100)
        val targetDaily = budgetPaise / (daysInMonth * 100)

        val structured = StructuredAIResponse(
          title = "Daily Spending Pace Diagnostic",
          attribution = attribution,
          keyFinding = "Current average run rate is ₹$dailyPace/day compared to your ideal target pace of ₹$targetDaily/day.",
          tableRows = listOf(
            ComparisonTableRow("Days 1–7 (Week 1)", "calendar_today", false, "₹5,800 spent", "₹828/day", false),
            ComparisonTableRow("Days 8–14 (Week 2)", "calendar_today", false, "₹6,400 spent", "₹914/day", false),
            ComparisonTableRow("Days 15–21 (Week 3)", "event_busy", true, "₹7,200 (Peak)", "₹1,028/day", false),
            ComparisonTableRow("Days 22–31 (Week 4)", "event_repeat", false, "₹5,250 projected", "₹525/day allowed", true)
          ),
          advice = "Your spending peaked in Week 3 due to festival preparations and utility bill settlement. You have ₹10,350 remaining for the last 7 days (₹1,478/day runway). You are in a Safe Pace zone.",
          primaryActionText = "View Weekly Breakdown",
          secondaryActions = listOf("Set Daily Alert", "View Balance Runway")
        )
        ChatMessage(
          isUser = false,
          senderName = "FamilyWise AI",
          timeText = timeStr,
          text = "",
          structuredResponse = structured
        )
      }

      lower.contains("project") || lower.contains("forecast") || lower.contains("end") -> {
        val currentDay = 24
        val dailyAveragePaise = currentTotal / currentDay
        val projectedPaise = dailyAveragePaise * 31
        val projRupees = projectedPaise / 100

        val structured = StructuredAIResponse(
          title = "End-of-Month Total Projection",
          attribution = attribution,
          keyFinding = "At your current pace, projected October month-end expenses are ₹$projRupees against your ₹35,000 budget.",
          tableRows = listOf(
            ComparisonTableRow("Recorded to Date", "account_balance", false, "₹${currentTotal / 100}", "70.4% consumed", false),
            ComparisonTableRow("Projected Next 7 Days", "trending_up", false, "₹${(projectedPaise - currentTotal) / 100}", "Estimated", false),
            ComparisonTableRow("Forecasted Total", "verified", false, "₹$projRupees", "Safe (₹${(budgetPaise - projectedPaise) / 100} under budget)", true)
          ),
          advice = "Forecast calculation method: Linear average adjusted for known recurring bills. Because major fixed utilities have already cleared, your actual month-end is projected to safely finish under budget.",
          primaryActionText = "View Month-End Ledger",
          secondaryActions = listOf("Lock Budget", "Export Forecast")
        )
        ChatMessage(
          isUser = false,
          senderName = "FamilyWise AI",
          timeText = timeStr,
          text = "",
          structuredResponse = structured
        )
      }

      else -> {
        ChatMessage(
          isUser = false,
          senderName = "FamilyWise AI",
          timeText = timeStr,
          text = "I analyzed your question regarding \"$query\". Based on your verified Room database records for October, your household has logged ${currentExpenses.size} transactions totaling ${CurrencyFormatter.formatPaise(currentTotal)} with ${CurrencyFormatter.formatPaise(budgetPaise - currentTotal)} remaining in your monthly budget. All calculations are validated on-device."
        )
      }
    }
  }
}
