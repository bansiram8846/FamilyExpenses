package com.example.ui.dashboard

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExpenseEntity
import com.example.domain.budget.MonthlyBudgetSummary
import com.example.domain.expense.CurrencyFormatter
import com.example.domain.insights.InsightResult
import com.example.domain.report.CategorySpendItem
import com.example.domain.report.MonthComparisonSummary
import com.example.ui.components.CategoryIcon
import com.example.ui.theme.Background
import com.example.ui.theme.Error
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.OnErrorContainer
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSecondary
import com.example.ui.theme.OnSecondaryContainer
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Outline
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceTint
import com.example.ui.theme.Tertiary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
  budgetSummary: MonthlyBudgetSummary,
  comparison: MonthComparisonSummary,
  insight: InsightResult,
  categoryBreakdown: List<CategorySpendItem>,
  recentExpenses: List<ExpenseEntity>,
  householdName: String = "My Household",
  primaryUserName: String = "Family Member",
  onAskAI: () -> Unit,
  onViewBreakdown: () -> Unit,
  onSeeAllTransactions: () -> Unit,
  onExpenseClick: (String) -> Unit
) {
  val scrollState = rememberScrollState()

  val greeting = remember(primaryUserName) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val timeGreeting = when (hour) {
      in 5..11 -> "Good morning"
      in 12..16 -> "Good afternoon"
      else -> "Good evening"
    }
    if (primaryUserName.isNotBlank()) "$timeGreeting, $primaryUserName!" else "$timeGreeting!"
  }

  val currentPeriod = remember {
    SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(Date())
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Background)
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Greeting Banner
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = greeting,
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = OnSurface,
          letterSpacing = (-0.5).sp
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(top = 2.dp)
        ) {
          Text(
            text = householdName.ifBlank { "My Household" },
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
          )
          Text(
            text = " • ",
            style = MaterialTheme.typography.bodyMedium,
            color = Outline
          )
          Text(
            text = currentPeriod,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
          )
        }
      }

      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(SurfaceContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = "Shield",
          tint = Secondary,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    // 2. Primary Hero Card: Monthly Budget Tracker
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
        .testTag("dashboard_budget_hero_card"),
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
          verticalAlignment = Alignment.Top
        ) {
          Column {
            Text(
              text = "SPENT THIS MONTH",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold,
              color = OnSurfaceVariant,
              letterSpacing = 0.5.sp
            )
            Row(
              verticalAlignment = Alignment.Bottom,
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Text(
                text = CurrencyFormatter.formatPaise(budgetSummary.totalSpentPaise),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurface
              )
              Text(
                text = " / " + CurrencyFormatter.formatPaise(budgetSummary.totalBudgetPaise),
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)
              )
            }
          }

          // Member Avatar Stack: Rajesh, Priya, +2
          Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy((-6).dp)
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Primary)
                .border(2.dp, SurfaceContainerLowest, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text("R", color = OnPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Secondary)
                .border(2.dp, SurfaceContainerLowest, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text("P", color = OnSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(SurfaceContainerHigh)
                .border(2.dp, SurfaceContainerLowest, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text("+2", color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Progress Bar & Analytics
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = String.format(Locale.ENGLISH, "%.1f%% used", budgetSummary.percentageUsed),
              style = MaterialTheme.typography.labelMedium,
              color = OnSurfaceVariant,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = CurrencyFormatter.formatPaise(budgetSummary.remainingPaise) + " remaining",
              style = MaterialTheme.typography.labelMedium,
              color = Secondary,
              fontWeight = FontWeight.SemiBold
            )
          }

          // Progress Track
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
                .background(Secondary)
            )
          }
        }

        // Bottom Micro Details
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(0.dp))
            .padding(top = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.EventRepeat,
              contentDescription = "Days left",
              tint = Secondary,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "${budgetSummary.daysLeftInMonth} days left in billing cycle",
              style = MaterialTheme.typography.labelMedium,
              color = OnSurfaceVariant
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceContainer)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = budgetSummary.paceStatus,
              style = MaterialTheme.typography.labelSmall,
              color = OnSurface,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }

    // 3. Comparison Card: Month-over-Month
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp)),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "vs Last Month (September)",
            style = MaterialTheme.typography.labelMedium,
            color = OnSurfaceVariant,
            fontWeight = FontWeight.Medium
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = CurrencyFormatter.formatPaise(comparison.currentMonthTotalPaise),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = OnSurface
            )
            Text(
              text = "vs",
              style = MaterialTheme.typography.bodySmall,
              color = Outline
            )
            Text(
              text = CurrencyFormatter.formatPaise(comparison.previousMonthTotalPaise),
              style = MaterialTheme.typography.bodyMedium,
              color = Outline
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (comparison.isHigher) ErrorContainer else SecondaryContainer)
              .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.TrendingUp,
              contentDescription = "Trend",
              tint = if (comparison.isHigher) OnErrorContainer else OnSecondaryContainer,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "+₹${comparison.absoluteVariancePaise / 100} (${String.format(Locale.ENGLISH, "%.1f", comparison.percentageVariance ?: 0.0)}%)",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = if (comparison.isHigher) OnErrorContainer else OnSecondaryContainer
            )
          }
          Text(
            text = if (comparison.isHigher) "Higher this period" else "Lower this period",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }
    }

    // 4. FamilyWise AI Insight Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, SecondaryContainer, RoundedCornerShape(16.dp))
        .testTag("dashboard_ai_insight_card"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Secondary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI",
                tint = OnSecondary,
                modifier = Modifier.size(15.dp)
              )
            }
            Text(
              text = insight.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = OnSurface
            )
          }

          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceContainerLowest)
              .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
              .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Psychology,
              contentDescription = "Local",
              tint = Secondary,
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = insight.attribution,
              style = MaterialTheme.typography.labelSmall,
              color = OnSurfaceVariant
            )
          }
        }

        Text(
          text = insight.body,
          style = MaterialTheme.typography.bodyMedium,
          color = OnSurfaceVariant,
          lineHeight = 20.sp
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onAskAI,
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("dashboard_ask_ai_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = SecondaryContainer,
                modifier = Modifier.size(18.dp)
              )
              Text("Ask AI Assistant", color = OnPrimary, fontWeight = FontWeight.SemiBold)
            }
          }

          OutlinedButton(
            onClick = onViewBreakdown,
            modifier = Modifier
              .height(44.dp)
              .testTag("dashboard_view_breakdown_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceContainerLowest)
          ) {
            Text("View Breakdown", color = OnSurface, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }

    // 5. Category Spending Breakdown (Quick Visual Bento)
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
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.PieChart,
              contentDescription = null,
              tint = OnSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Spending by Category",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = OnSurface
            )
          }

          Row(
            modifier = Modifier
              .clickable { onViewBreakdown() }
              .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Details",
              style = MaterialTheme.typography.labelMedium,
              color = Secondary,
              fontWeight = FontWeight.SemiBold
            )
            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = null,
              tint = Secondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        // Multi-segment visual progress bar or empty state
        if (categoryBreakdown.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No categorized spending recorded yet",
              style = MaterialTheme.typography.bodySmall,
              color = OnSurfaceVariant
            )
          }
        } else {
          val totalPaise = categoryBreakdown.sumOf { it.currentMonthPaise }.coerceAtLeast(1L).toFloat()
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(12.dp)
              .clip(CircleShape)
              .background(SurfaceContainer)
          ) {
            categoryBreakdown.take(5).forEachIndexed { index, item ->
              val fraction = (item.currentMonthPaise.toFloat() / totalPaise).coerceIn(0.01f, 1f)
              val color = when (index) {
                0 -> Secondary
                1 -> OnSecondaryContainer
                2 -> Primary
                3 -> SurfaceTint
                else -> Error
              }
              Box(
                modifier = Modifier
                  .weight(fraction)
                  .height(12.dp)
                  .background(color)
              )
            }
          }

          // Category items list
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categoryBreakdown.take(5).forEachIndexed { index, item ->
              if (index > 0) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(OutlineVariant.copy(alpha = 0.5f))
                )
              }
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
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
                      .background(if (item.isExceedingTarget) ErrorContainer else SurfaceContainer),
                    contentAlignment = Alignment.Center
                  ) {
                    CategoryIcon(
                      iconName = item.iconName,
                      tint = if (item.isExceedingTarget) OnErrorContainer else Secondary,
                      modifier = Modifier.size(18.dp)
                    )
                  }

                  Column {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Text(
                        text = item.categoryName,
                        style = MaterialTheme.typography.labelLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Medium
                      )
                      if (item.isExceedingTarget) {
                        Box(
                          modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Error)
                        )
                      }
                    }
                    Text(
                      text = if (item.isExceedingTarget) {
                        String.format(Locale.ENGLISH, "%.0f%% • Exceeds target", item.percentageOfTotal)
                      } else {
                        String.format(Locale.ENGLISH, "%.0f%% of monthly spends", item.percentageOfTotal)
                      },
                      style = MaterialTheme.typography.bodySmall,
                      color = if (item.isExceedingTarget) Error else OnSurfaceVariant,
                      fontWeight = if (item.isExceedingTarget) FontWeight.Medium else FontWeight.Normal
                    )
                  }
                }

                Text(
                  text = CurrencyFormatter.formatPaise(item.currentMonthPaise),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = if (item.isExceedingTarget) Error else OnSurface
                )
              }
            }
          }
        }
      }
    }

    // 6. Recent Household Expenses
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Recent Household Expenses",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = OnSurface
        )

        Text(
          text = "See all",
          style = MaterialTheme.typography.labelMedium,
          color = Secondary,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier
            .clickable { onSeeAllTransactions() }
            .testTag("dashboard_see_all_transactions")
        )
      }

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
      ) {
        if (recentExpenses.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "No expenses logged yet",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
              )
              Text(
                text = "Tap the + button below to add your first expense",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
              )
            }
          }
        } else {
          Column {
            recentExpenses.take(3).forEachIndexed { index, exp ->
              if (index > 0) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(OutlineVariant.copy(alpha = 0.5f))
                )
              }
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onExpenseClick(exp.id) }
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(40.dp)
                      .clip(RoundedCornerShape(12.dp))
                      .background(SurfaceContainer),
                    contentAlignment = Alignment.Center
                  ) {
                    CategoryIcon(
                      iconName = when (exp.categoryId) {
                        "groceries" -> "local_mall"
                        "utilities" -> "bolt"
                        "dining" -> "restaurant"
                        "healthcare" -> "medical_services"
                        else -> "shopping_basket"
                      },
                      tint = if (exp.categoryId == "utilities") Primary else Secondary,
                      modifier = Modifier.size(20.dp)
                    )
                  }

                  Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                      text = exp.title,
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.SemiBold,
                      color = OnSurface
                    )
                    Text(
                      text = "Paid by ${exp.paidByMemberId.replaceFirstChar { it.uppercase() }} • ${exp.paymentMethod}",
                      style = MaterialTheme.typography.bodySmall,
                      color = OnSurfaceVariant
                    )
                  }
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = CurrencyFormatter.formatPaise(exp.amountPaise),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                  )
                  if (exp.splitNote.isNotEmpty()) {
                    Text(
                      text = exp.splitNote,
                      style = MaterialTheme.typography.labelSmall,
                      color = Tertiary,
                      fontWeight = FontWeight.Medium
                    )
                  } else if (exp.tag.isNotEmpty()) {
                    Text(
                      text = exp.tag,
                      style = MaterialTheme.typography.labelSmall,
                      color = OnSurfaceVariant
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 7. Footnote
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = null,
        tint = Outline,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "End-to-end encrypted across household devices",
        style = MaterialTheme.typography.labelSmall,
        color = Outline
      )
    }

    Spacer(modifier = Modifier.height(60.dp))
  }
}
