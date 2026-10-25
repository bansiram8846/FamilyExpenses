package com.example.ui.budgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.budget.MonthlyBudgetSummary
import com.example.domain.expense.CurrencyFormatter
import com.example.domain.report.CategorySpendItem
import com.example.ui.components.CategoryIcon
import com.example.ui.theme.Background
import com.example.ui.theme.Error
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.OnErrorContainer
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSecondaryContainer
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import java.util.Locale

@Composable
fun BudgetsScreen(
  budgetSummary: MonthlyBudgetSummary,
  categories: List<CategorySpendItem>,
  onUpdateTotalBudget: (Long) -> Unit,
  onUpdateCategoryBudget: (String, Long) -> Unit
) {
  var showEditTotalDialog by remember { mutableStateOf(false) }
  var totalBudgetInput by remember { mutableStateOf((budgetSummary.totalBudgetPaise / 100).toString()) }

  var editingCategory by remember { mutableStateOf<CategorySpendItem?>(null) }
  var categoryBudgetInput by remember { mutableStateOf("") }

  val scrollState = rememberScrollState()

  // Edit Total Household Budget Dialog
  if (showEditTotalDialog) {
    AlertDialog(
      onDismissRequest = { showEditTotalDialog = false },
      title = { Text("Set Household Monthly Budget", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Enter total monthly expense limit for Sharma Household:", style = MaterialTheme.typography.bodySmall)
          OutlinedTextField(
            value = totalBudgetInput,
            onValueChange = { totalBudgetInput = it.filter { ch -> ch.isDigit() } },
            label = { Text("Total Budget (₹)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val paise = CurrencyFormatter.parseToPaise(totalBudgetInput)
            if (paise > 0L) {
              onUpdateTotalBudget(paise)
            }
            showEditTotalDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
          Text("Save Budget")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditTotalDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Edit Category Budget Dialog
  if (editingCategory != null) {
    val cat = editingCategory!!
    AlertDialog(
      onDismissRequest = { editingCategory = null },
      title = { Text("Set ${cat.categoryName} Target", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Configure target threshold for ${cat.categoryName}:", style = MaterialTheme.typography.bodySmall)
          OutlinedTextField(
            value = categoryBudgetInput,
            onValueChange = { categoryBudgetInput = it.filter { ch -> ch.isDigit() } },
            label = { Text("Category Target (₹)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val paise = CurrencyFormatter.parseToPaise(categoryBudgetInput)
            if (paise > 0L) {
              onUpdateCategoryBudget(cat.categoryId, paise)
            }
            editingCategory = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
          Text("Save Target")
        }
      },
      dismissButton = {
        TextButton(onClick = { editingCategory = null }) {
          Text("Cancel")
        }
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Background)
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Title
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Budgets & Spending Limits",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = OnSurface
      )

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(Primary)
          .clickable {
            totalBudgetInput = (budgetSummary.totalBudgetPaise / 100).toString()
            showEditTotalDialog = true
          }
          .padding(horizontal = 10.dp, vertical = 6.dp)
          .testTag("edit_household_budget_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(14.dp))
          Text("Edit Budget", color = OnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // Household Overall Budget Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp)),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SecondaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(20.dp))
            }
            Column {
              Text("Overall Household Budget", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
              Text("October 2024", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
          }

          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(if (budgetSummary.isOverBudget) ErrorContainer else SecondaryContainer)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = budgetSummary.paceStatus,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = if (budgetSummary.isOverBudget) OnErrorContainer else OnSecondaryContainer
            )
          }
        }

        // Amount numbers
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Column {
            Text("Total Spent", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            Text(
              text = CurrencyFormatter.formatPaise(budgetSummary.totalSpentPaise),
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold,
              color = OnSurface
            )
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("Monthly Ceiling", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            Text(
              text = CurrencyFormatter.formatPaise(budgetSummary.totalBudgetPaise),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = OnSurfaceVariant
            )
          }
        }

        // Progress track
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(CircleShape)
              .background(SurfaceContainer)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(fraction = (budgetSummary.percentageUsed / 100f).coerceIn(0f, 1f))
                .height(10.dp)
                .clip(CircleShape)
                .background(if (budgetSummary.isOverBudget) Error else Secondary)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "${String.format(Locale.ENGLISH, "%.1f", budgetSummary.percentageUsed)}% used",
              style = MaterialTheme.typography.labelSmall,
              color = OnSurfaceVariant
            )
            Text(
              text = "${CurrencyFormatter.formatPaise(budgetSummary.remainingPaise)} remaining",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Secondary
            )
          }
        }

        // 75% and 100% Alerts configuration notice
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainerLow)
            .padding(10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
            Text(
              text = "Household alerts active at 75% (warning) and 100% (limit reached)",
              style = MaterialTheme.typography.bodySmall,
              fontSize = 11.5.sp,
              color = OnSurfaceVariant
            )
          }
        }
      }
    }

    // Category Budgets List
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "Category Targets & Runways",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = OnSurface
      )

      categories.forEach { cat ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OutlineVariant, RoundedCornerShape(14.dp)),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (cat.isExceedingTarget) ErrorContainer else SurfaceContainer),
                  contentAlignment = Alignment.Center
                ) {
                  CategoryIcon(
                    iconName = cat.iconName,
                    tint = if (cat.isExceedingTarget) OnErrorContainer else Secondary,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Column {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text(cat.categoryName, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = OnSurface)
                    if (cat.isExceedingTarget) {
                      Box(
                        modifier = Modifier
                          .clip(CircleShape)
                          .background(Error)
                          .size(6.dp)
                      )
                    }
                  }
                  Text(
                    text = if (cat.isExceedingTarget) "Exceeds monthly target" else "On track",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (cat.isExceedingTarget) Error else OnSurfaceVariant,
                    fontSize = 11.sp
                  )
                }
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(
                  text = CurrencyFormatter.formatPaise(cat.currentMonthPaise),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = if (cat.isExceedingTarget) Error else OnSurface
                )

                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceContainerLow)
                    .clickable {
                      categoryBudgetInput = (cat.currentMonthPaise / 100).toString()
                      editingCategory = cat
                    },
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Edit, contentDescription = "Edit Target", tint = OnSurfaceVariant, modifier = Modifier.size(15.dp))
                }
              }
            }

            // Category progress
            val targetPaise = when (cat.categoryId) {
              "groceries" -> 1000000L
              "dining" -> 250000L
              "education" -> 600000L
              "utilities" -> 500000L
              "healthcare" -> 400000L
              else -> 400000L
            }
            val pct = (cat.currentMonthPaise.toFloat() / targetPaise.toFloat()).coerceIn(0f, 1f)

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(SurfaceContainer)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(fraction = pct)
                  .height(6.dp)
                  .clip(CircleShape)
                  .background(if (cat.isExceedingTarget) Error else Secondary)
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(60.dp))
  }
}
