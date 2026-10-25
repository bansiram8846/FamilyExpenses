package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ExpenseEntity
import com.example.data.local.FamilyWiseDatabase
import com.example.data.local.HouseholdBudgetEntity
import com.example.data.local.MemberEntity
import com.example.data.repository.AISettingsRepository
import com.example.data.repository.AISettingsState
import com.example.data.repository.ExpenseRepository
import com.example.data.repository.OperatingMode
import com.example.domain.budget.BudgetCalculator
import com.example.domain.budget.MonthlyBudgetSummary
import com.example.domain.insights.InsightResult
import com.example.domain.insights.LocalInsightEngine
import com.example.domain.report.CategorySpendItem
import com.example.domain.report.MemberContribution
import com.example.domain.report.MonthComparisonSummary
import com.example.domain.report.PeakSpendingDay
import com.example.domain.report.ReportCalculator
import com.example.domain.report.WeeklyBarData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class MainUiState(
  val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
  val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
  val currentExpenses: List<ExpenseEntity> = emptyList(),
  val previousExpenses: List<ExpenseEntity> = emptyList(),
  val budgetSummary: MonthlyBudgetSummary = MonthlyBudgetSummary(
    totalSpentPaise = 0L,
    totalBudgetPaise = 3500000L,
    remainingPaise = 3500000L,
    percentageUsed = 0f,
    daysLeftInMonth = 7,
    isOverBudget = false,
    isAbove75Percent = false,
    paceStatus = "Safe pace"
  ),
  val monthComparison: MonthComparisonSummary = MonthComparisonSummary(
    currentMonthTotalPaise = 0L,
    previousMonthTotalPaise = 0L,
    absoluteVariancePaise = 0L,
    percentageVariance = null,
    isHigher = false,
    formattedVarianceText = "No previous month data"
  ),
  val insight: InsightResult = InsightResult(
    title = "Welcome to FamilyWise AI",
    body = "Your household ledger is clean and ready. Tap the '+' button below to log your family's first expense!",
    keyFinding = "0 expenses recorded this month.",
    primaryCategory = "All",
    secondaryCategory = "",
    potentialSavingsPaise = 0L
  ),
  val categoryBreakdown: List<CategorySpendItem> = emptyList(),
  val memberContributions: List<MemberContribution> = emptyList(),
  val weeklyRunRate: List<WeeklyBarData> = emptyList(),
  val peakSpendingDay: PeakSpendingDay = PeakSpendingDay("No expenses logged yet", 0L, "Tap + to add your first expense"),
  val aiSettings: AISettingsState = AISettingsState(),
  val isLoading: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val database = FamilyWiseDatabase.getDatabase(application, viewModelScope)
  val expenseRepository = ExpenseRepository(database.expenseDao())
  val aiSettingsRepository = AISettingsRepository(application)

  private val cal = Calendar.getInstance()
  val activeYear = cal.get(Calendar.YEAR)
  val activeMonth = cal.get(Calendar.MONTH) + 1

  init {
    viewModelScope.launch {
      val settings = aiSettingsRepository.settingsFlow.first()
      // If onboarding is not completed yet, wipe any lingering test data for a completely clean start
      if (!settings.isOnboardingCompleted) {
        database.expenseDao().clearAllExpenses()
        database.expenseDao().clearAllMembers()
        database.expenseDao().clearAllBudgets()
        database.expenseDao().clearAllCategoryBudgets()
        database.expenseDao().clearAllRecurring()
      }
    }
  }

  val uiState: StateFlow<MainUiState> = combine(
    expenseRepository.allExpenses,
    expenseRepository.allCategories,
    expenseRepository.allMembers,
    expenseRepository.getHouseholdBudget(activeYear, activeMonth),
    aiSettingsRepository.settingsFlow
  ) { allExp, categories, members, budgetEntity, settings ->

    // Current month expenses
    val (curStart, curEnd) = ExpenseRepository.getMonthBounds(activeYear, activeMonth)
    val curExpenses = allExp.filter { it.dateEpochMillis in curStart..curEnd }

    // Previous month expenses
    val prevMonth = if (activeMonth == 1) 12 else activeMonth - 1
    val prevYear = if (activeMonth == 1) activeYear - 1 else activeYear
    val (prevStart, prevEnd) = ExpenseRepository.getMonthBounds(prevYear, prevMonth)
    val prevExpenses = allExp.filter { it.dateEpochMillis in prevStart..prevEnd }

    val budgetPaise = budgetEntity?.totalBudgetPaise ?: 3500000L

    val budgetSummary = BudgetCalculator.calculateSummary(curExpenses, budgetPaise, activeYear, activeMonth)
    val comparison = ReportCalculator.calculateComparison(curExpenses, prevExpenses)
    val insight = if (curExpenses.isEmpty()) {
      InsightResult(
        title = "Welcome to FamilyWise AI",
        body = "Your household ledger is clean and ready. Tap the '+' button below to log your family's first expense!",
        keyFinding = "0 expenses recorded this month.",
        primaryCategory = "All",
        secondaryCategory = "",
        potentialSavingsPaise = 0L,
        attribution = "Local Analysis"
      )
    } else {
      LocalInsightEngine.generateInsight(curExpenses, prevExpenses)
    }

    val catBreakdown = ReportCalculator.calculateCategoryBreakdown(curExpenses, prevExpenses, categories)
    val memberContribs = ReportCalculator.calculateMemberContributions(curExpenses, members)
    val weekly = ReportCalculator.calculateWeeklyRunRate(curExpenses)
    val peak = ReportCalculator.findPeakSpendingDay(curExpenses)

    MainUiState(
      currentYear = activeYear,
      currentMonth = activeMonth,
      currentExpenses = curExpenses,
      previousExpenses = prevExpenses,
      budgetSummary = budgetSummary,
      monthComparison = comparison,
      insight = insight,
      categoryBreakdown = catBreakdown,
      memberContributions = memberContribs,
      weeklyRunRate = weekly,
      peakSpendingDay = peak,
      aiSettings = settings,
      isLoading = false
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = MainUiState()
  )

  fun setupHousehold(
    householdName: String,
    primaryMember: MemberEntity,
    otherMembers: List<MemberEntity>,
    monthlyBudgetPaise: Long,
    operatingMode: OperatingMode
  ) {
    viewModelScope.launch {
      // Clear any lingering records
      database.expenseDao().clearAllExpenses()
      database.expenseDao().clearAllMembers()
      database.expenseDao().clearAllBudgets()
      database.expenseDao().clearAllCategoryBudgets()
      database.expenseDao().clearAllRecurring()

      // Insert configured members
      val allMembers = listOf(primaryMember) + otherMembers
      database.expenseDao().insertMembers(allMembers)

      // Insert user's monthly budget
      database.expenseDao().insertHouseholdBudget(
        HouseholdBudgetEntity(year = activeYear, month = activeMonth, totalBudgetPaise = monthlyBudgetPaise)
      )

      // Save onboarding preferences
      aiSettingsRepository.completeOnboarding(
        householdName = householdName,
        primaryUserName = primaryMember.name,
        primaryUserRelation = primaryMember.relation,
        operatingMode = operatingMode
      )
    }
  }

  fun resetAppDataAndRerunSetup() {
    viewModelScope.launch {
      database.expenseDao().clearAllExpenses()
      database.expenseDao().clearAllMembers()
      database.expenseDao().clearAllBudgets()
      database.expenseDao().clearAllCategoryBudgets()
      database.expenseDao().clearAllRecurring()
      aiSettingsRepository.resetOnboarding()
    }
  }

  fun addOrUpdateExpense(expense: ExpenseEntity) {
    viewModelScope.launch {
      expenseRepository.insertExpense(expense)
    }
  }

  fun deleteExpense(id: String) {
    viewModelScope.launch {
      expenseRepository.deleteExpense(id)
    }
  }

  fun updateHouseholdBudget(amountPaise: Long) {
    viewModelScope.launch {
      expenseRepository.saveHouseholdBudget(activeYear, activeMonth, amountPaise)
    }
  }

  fun updateCategoryBudget(categoryId: String, amountPaise: Long) {
    viewModelScope.launch {
      expenseRepository.saveCategoryBudget(categoryId, activeYear, activeMonth, amountPaise)
    }
  }

  fun setOnlineAIEnabled(enabled: Boolean) {
    viewModelScope.launch {
      aiSettingsRepository.setOnlineAIEnabled(enabled)
    }
  }

  fun setOperatingMode(mode: OperatingMode) {
    viewModelScope.launch {
      aiSettingsRepository.setOperatingMode(mode)
    }
  }

  fun clearChatHistory() {
    viewModelScope.launch {
      aiSettingsRepository.resetQuestionsUsed()
    }
  }

  fun updateProxyEndpoint(url: String) {
    viewModelScope.launch {
      aiSettingsRepository.setProxyEndpoint(url)
    }
  }
}
