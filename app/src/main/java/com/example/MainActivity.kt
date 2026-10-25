package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.backup.ExportManager
import com.example.data.local.ExpenseEntity
import com.example.data.repository.OperatingMode
import com.example.ui.MainViewModel
import com.example.ui.assistant.AssistantScreen
import com.example.ui.budgets.BudgetsScreen
import com.example.ui.components.FamilyWiseBottomNavigation
import com.example.ui.components.FamilyWiseHeader
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.expense.AddExpenseScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.reports.ReportsScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.Background
import com.example.ui.theme.FamilyWiseTheme
import com.example.ui.transactions.TransactionsScreen

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      FamilyWiseTheme {
        FamilyWiseApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun FamilyWiseApp(viewModel: MainViewModel) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsState()

  // First time installation onboarding flow
  if (!uiState.aiSettings.isOnboardingCompleted) {
    OnboardingScreen(
      onCompleteSetup = { householdName, primaryMember, otherMembers, monthlyBudgetPaise, mode ->
        viewModel.setupHousehold(householdName, primaryMember, otherMembers, monthlyBudgetPaise, mode)
      }
    )
    return
  }

  // Navigation state: "home", "transactions", "budgets", "reports", "assistant", "settings"
  var currentRoute by remember { mutableStateOf("home") }
  var showAddExpenseModal by remember { mutableStateOf(false) }
  var editingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }

  // Handle Android system back button
  BackHandler(enabled = currentRoute != "home" || showAddExpenseModal) {
    if (showAddExpenseModal) {
      showAddExpenseModal = false
      editingExpense = null
    } else {
      currentRoute = "home"
    }
  }

  val isMainTab = currentRoute in listOf("home", "transactions", "budgets", "reports")

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(Background)
      .windowInsetsPadding(WindowInsets.statusBars),
    topBar = {
      if (isMainTab && !showAddExpenseModal) {
        FamilyWiseHeader(
          householdName = uiState.aiSettings.householdName.ifBlank { "My Family" },
          periodOrSubtitle = when (currentRoute) {
            "home" -> "Ledger Active"
            "transactions" -> "Ledger Active"
            "budgets" -> "Monthly Limits"
            "reports" -> "Reports & Analysis"
            else -> "Ledger Active"
          },
          onNotificationsClick = {
            Toast.makeText(context, "No unread alerts • Household pace is safe", Toast.LENGTH_SHORT).show()
          },
          onProfileClick = {
            currentRoute = "settings"
          },
          showOfflineSubheader = (currentRoute == "home")
        )
      }
    },
    bottomBar = {
      if (isMainTab && !showAddExpenseModal) {
        Box(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
          FamilyWiseBottomNavigation(
            currentRoute = currentRoute,
            onNavigate = { currentRoute = it },
            onAddClick = {
              editingExpense = null
              showAddExpenseModal = true
            }
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("app_main_container")
    ) {
      when (currentRoute) {
        "home" -> {
          DashboardScreen(
            budgetSummary = uiState.budgetSummary,
            comparison = uiState.monthComparison,
            insight = uiState.insight,
            categoryBreakdown = uiState.categoryBreakdown,
            recentExpenses = uiState.currentExpenses,
            householdName = uiState.aiSettings.householdName.ifBlank { "My Household" },
            primaryUserName = uiState.aiSettings.primaryUserName.ifBlank { "Admin" },
            onAskAI = { currentRoute = "assistant" },
            onViewBreakdown = { currentRoute = "reports" },
            onSeeAllTransactions = { currentRoute = "transactions" },
            onExpenseClick = { id ->
              val exp = uiState.currentExpenses.find { it.id == id }
              if (exp != null) {
                editingExpense = exp
                showAddExpenseModal = true
              }
            }
          )
        }

        "transactions" -> {
          TransactionsScreen(
            expenses = uiState.currentExpenses,
            onEditExpense = { exp ->
              editingExpense = exp
              showAddExpenseModal = true
            },
            onDeleteExpense = { id ->
              viewModel.deleteExpense(id)
              Toast.makeText(context, "Expense deleted", Toast.LENGTH_SHORT).show()
            },
            onExportCsv = {
              val csvContent = ExportManager.generateCsv(
                expenses = uiState.currentExpenses,
                categoryMap = uiState.categoryBreakdown.associateBy { it.categoryId }.mapValues { 
                  com.example.data.local.CategoryEntity(it.key, it.value.categoryName, it.value.iconName, it.value.colorHex)
                },
                memberMap = uiState.memberContributions.associateBy { it.memberId }.mapValues {
                  com.example.data.local.MemberEntity(it.key, it.value.name, it.value.relation, it.value.avatarInitial)
                }
              )
              ExportManager.shareExport(context, "familywise_october_passbook.csv", csvContent, "text/csv")
            }
          )
        }

        "budgets" -> {
          BudgetsScreen(
            budgetSummary = uiState.budgetSummary,
            categories = uiState.categoryBreakdown,
            onUpdateTotalBudget = { newPaise ->
              viewModel.updateHouseholdBudget(newPaise)
              Toast.makeText(context, "Monthly budget updated", Toast.LENGTH_SHORT).show()
            },
            onUpdateCategoryBudget = { catId, newPaise ->
              viewModel.updateCategoryBudget(catId, newPaise)
              Toast.makeText(context, "Category target updated", Toast.LENGTH_SHORT).show()
            }
          )
        }

        "reports" -> {
          ReportsScreen(
            budgetSummary = uiState.budgetSummary,
            comparison = uiState.monthComparison,
            categories = uiState.categoryBreakdown,
            members = uiState.memberContributions,
            weeklyRunRate = uiState.weeklyRunRate,
            peakSpendingDay = uiState.peakSpendingDay,
            onAskAI = { currentRoute = "assistant" },
            onDownloadPdf = {
              val statement = ExportManager.generateTextStatement(
                period = "October 2024",
                expenses = uiState.currentExpenses,
                categoryMap = uiState.categoryBreakdown.associateBy { it.categoryId }.mapValues { 
                  com.example.data.local.CategoryEntity(it.key, it.value.categoryName, it.value.iconName, it.value.colorHex)
                },
                memberMap = uiState.memberContributions.associateBy { it.memberId }.mapValues {
                  com.example.data.local.MemberEntity(it.key, it.value.name, it.value.relation, it.value.avatarInitial)
                },
                budgetPaise = uiState.budgetSummary.totalBudgetPaise
              )
              ExportManager.shareExport(context, "familywise_october_statement.txt", statement, "text/plain")
            },
            onExportCsv = {
              val csvContent = ExportManager.generateCsv(
                expenses = uiState.currentExpenses,
                categoryMap = uiState.categoryBreakdown.associateBy { it.categoryId }.mapValues { 
                  com.example.data.local.CategoryEntity(it.key, it.value.categoryName, it.value.iconName, it.value.colorHex)
                },
                memberMap = uiState.memberContributions.associateBy { it.memberId }.mapValues {
                  com.example.data.local.MemberEntity(it.key, it.value.name, it.value.relation, it.value.avatarInitial)
                }
              )
              ExportManager.shareExport(context, "familywise_october_passbook.csv", csvContent, "text/csv")
            }
          )
        }

        "assistant" -> {
          AssistantScreen(
            currentExpenses = uiState.currentExpenses,
            previousExpenses = uiState.previousExpenses,
            budgetPaise = uiState.budgetSummary.totalBudgetPaise,
            operatingMode = uiState.aiSettings.operatingMode,
            isOnlineAIEnabled = uiState.aiSettings.onlineAIEnabled,
            onNavigateBack = { currentRoute = "home" },
            onOpenPrivacySettings = { currentRoute = "settings" },
            onNavigateToTransactions = { currentRoute = "transactions" },
            onToggleOperatingMode = {
              val newMode = if (uiState.aiSettings.operatingMode == OperatingMode.HYBRID) {
                OperatingMode.OFFLINE
              } else {
                OperatingMode.HYBRID
              }
              viewModel.setOperatingMode(newMode)
            }
          )
        }

        "settings" -> {
          SettingsScreen(
            settings = uiState.aiSettings,
            onNavigateBack = { currentRoute = "home" },
            onToggleOnlineAI = { enabled ->
              viewModel.setOnlineAIEnabled(enabled)
            },
            onSelectOperatingMode = { mode ->
              viewModel.setOperatingMode(mode)
            },
            onClearHistory = {
              viewModel.clearChatHistory()
            },
            onUpdateProxyEndpoint = { url ->
              viewModel.updateProxyEndpoint(url)
            },
            onResetHouseholdSetup = {
              viewModel.resetAppDataAndRerunSetup()
            }
          )
        }
      }

      // Add / Edit Expense Fullscreen Modal
      AnimatedVisibility(
        visible = showAddExpenseModal,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
      ) {
        AddExpenseScreen(
          existingExpense = editingExpense,
          onClose = {
            showAddExpenseModal = false
            editingExpense = null
          },
          onSaveExpense = { expense, addAnother ->
            viewModel.addOrUpdateExpense(expense)
            Toast.makeText(context, "Expense saved to Room database", Toast.LENGTH_SHORT).show()
            if (!addAnother) {
              showAddExpenseModal = false
              editingExpense = null
            }
          }
        )
      }
    }
  }
}
