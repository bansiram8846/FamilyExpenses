package com.example.ui.reports

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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.domain.budget.MonthlyBudgetSummary
import com.example.domain.expense.CurrencyFormatter
import com.example.domain.report.CategorySpendItem
import com.example.domain.report.MemberContribution
import com.example.domain.report.MonthComparisonSummary
import com.example.domain.report.PeakSpendingDay
import com.example.domain.report.WeeklyBarData
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
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.SecondaryFixedDim
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.Tertiary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
  budgetSummary: MonthlyBudgetSummary,
  comparison: MonthComparisonSummary,
  categories: List<CategorySpendItem>,
  members: List<MemberContribution>,
  weeklyRunRate: List<WeeklyBarData>,
  peakSpendingDay: PeakSpendingDay,
  onAskAI: () -> Unit,
  onDownloadPdf: () -> Unit,
  onExportCsv: () -> Unit
) {
  val scrollState = rememberScrollState()

  val currentPeriod = remember {
    SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(Date())
  }
  val prevPeriod = remember {
    val prevCal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
    SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(prevCal.time)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Background)
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Month Selector Bar (Header Subbar)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 2.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier
          .clip(CircleShape)
          .background(SurfaceContainerLow)
          .border(1.dp, OutlineVariant, CircleShape)
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
          Text(currentPeriod, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = OnSurface)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
      }

      Row(
        modifier = Modifier
          .clip(CircleShape)
          .background(SurfaceContainerHighest)
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text("vs", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Text(prevPeriod, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Secondary)
        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
      }
    }

    // 1. Period Comparison Hero Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
        .testTag("reports_period_comparison_card"),
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
            Icon(Icons.Default.CompareArrows, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
            Text("Monthly Spend Comparison", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
          }

          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(SurfaceContainerLow)
              .border(1.dp, OutlineVariant, CircleShape)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Secondary, modifier = Modifier.size(12.dp))
              Text("Room DB Local", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
          }
        }

        // Metric Grid (2 columns: This Month vs Last Month)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceContainerLow)
              .border(1.dp, OutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text("This Month (Oct)", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
              Text(
                text = CurrencyFormatter.formatPaise(comparison.currentMonthTotalPaise),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OnSurface
              )
              Text("28 Transactions", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceContainerLowest)
              .border(1.dp, OutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text("Last Month (Sep)", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
              Text(
                text = CurrencyFormatter.formatPaise(comparison.previousMonthTotalPaise),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant
              )
              Text("25 Transactions", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
          }
        }

        // Net Variance & Budget Status box
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow.copy(alpha = 0.6f))
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Net Variance:", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(ErrorContainer)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                  Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = OnErrorContainer, modifier = Modifier.size(13.dp))
                  Text(
                    text = "+₹${comparison.absoluteVariancePaise / 100} (+${String.format(Locale.ENGLISH, "%.1f", comparison.percentageVariance ?: 0.0)}%)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = OnErrorContainer
                  )
                }
              }
              Text("Higher", style = MaterialTheme.typography.labelSmall, color = Error, fontWeight = FontWeight.Bold)
            }
          }

          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Budget Utilization", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
              Text(
                text = "${CurrencyFormatter.formatPaise(budgetSummary.totalSpentPaise)} / ${CurrencyFormatter.formatPaise(budgetSummary.totalBudgetPaise)} (${String.format(Locale.ENGLISH, "%.1f", budgetSummary.percentageUsed)}%)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = OnSurface
              )
            }

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(SurfaceContainer)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(fraction = (budgetSummary.percentageUsed / 100f).coerceIn(0f, 1f))
                  .height(8.dp)
                  .clip(CircleShape)
                  .background(Secondary)
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("${CurrencyFormatter.formatPaise(budgetSummary.remainingPaise)} remaining", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Secondary, modifier = Modifier.size(12.dp))
                Text("On track (7 days left)", style = MaterialTheme.typography.labelSmall, color = OnSecondaryContainer, fontWeight = FontWeight.Medium)
              }
            }
          }
        }
      }
    }

    // 2. FamilyWise Report Insight Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp)),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SecondaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(18.dp))
            }
            Column {
              Text("FamilyWise Report Insight", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
              Text("Analyzed by Local AI Engine", style = MaterialTheme.typography.labelSmall, color = OnSecondaryContainer, fontWeight = FontWeight.Medium)
            }
          }

          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(SurfaceContainer)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text("Deterministic", fontSize = 10.5.sp, color = OnSurfaceVariant)
          }
        }

        // Quote Box with teal left accent
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainerLow)
            .border(width = 0.dp, color = Color.Transparent)
            .padding(12.dp)
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
              modifier = Modifier
                .width(3.dp)
                .height(60.dp)
                .background(Secondary)
            )
            Text(
              text = "\"Your overall spending increased by ₹2,400. Exactly 89% of the variance came from Dining Out (+₹1,200) and Groceries (+₹950). School fees remained static at ₹5,000.\"",
              style = MaterialTheme.typography.bodyMedium,
              color = OnSurface,
              lineHeight = 20.sp
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = onAskAI,
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("reports_ask_ai_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(16.dp))
              Text("Ask AI Assistant", color = OnPrimary, fontWeight = FontWeight.SemiBold)
            }
          }

          OutlinedButton(
            onClick = onAskAI,
            modifier = Modifier
              .weight(1f)
              .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceContainer)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text("Detailed Breakdown", color = OnSurface, style = MaterialTheme.typography.labelMedium)
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = OnSurface, modifier = Modifier.size(15.dp))
            }
          }
        }
      }
    }

    // 3. Category Comparison (Sep vs Oct)
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
            Icon(Icons.Default.Category, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
            Text("Category Comparison", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
          }

          Text("Sep vs Oct", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }

        // List of compared categories
        categories.take(5).forEachIndexed { idx, cat ->
          if (idx > 0) {
            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(OutlineVariant.copy(alpha = 0.4f)))
          }

          Column(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (cat.isExceedingTarget) ErrorContainer else SurfaceContainer),
                  contentAlignment = Alignment.Center
                ) {
                  CategoryIcon(
                    iconName = cat.iconName,
                    tint = if (cat.isExceedingTarget) OnErrorContainer else OnSurface,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Column {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Text(cat.categoryName, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    if (cat.isExceedingTarget) {
                      Icon(Icons.Default.Warning, contentDescription = null, tint = Error, modifier = Modifier.size(13.dp))
                    }
                  }
                  Text(
                    text = "Last month: ${CurrencyFormatter.formatPaise(cat.previousMonthPaise)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = CurrencyFormatter.formatPaise(cat.currentMonthPaise),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = OnSurface
                )
                val diffRupees = cat.variancePaise / 100
                val pctStr = String.format(Locale.ENGLISH, "%.1f", cat.percentageChange ?: 0.0)
                val isSaving = cat.variancePaise < 0
                val text = when {
                  cat.variancePaise == 0L -> "₹0 (0% change)"
                  isSaving -> "-₹${Math.abs(diffRupees)} (-$pctStr% saved)"
                  else -> "+₹$diffRupees (+$pctStr%)"
                }

                Text(
                  text = text,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (cat.isExceedingTarget) Error else if (isSaving) Secondary else OnSurfaceVariant
                )
              }
            }

            // Dual bar
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(SurfaceContainer)
            ) {
              Row(modifier = Modifier.fillMaxSize()) {
                val baseFraction = (cat.previousMonthPaise.toFloat() / 1000000L).coerceIn(0.1f, 0.7f)
                Box(
                  modifier = Modifier
                    .weight(baseFraction)
                    .fillMaxSize()
                    .background(if (cat.isExceedingTarget) Outline else Secondary)
                )
                if (cat.variancePaise > 0) {
                  val extraFraction = (cat.variancePaise.toFloat() / 1000000L).coerceIn(0.05f, 0.3f)
                  Box(
                    modifier = Modifier
                      .weight(extraFraction)
                      .fillMaxSize()
                      .background(Error)
                  )
                }
              }
            }
          }
        }
      }
    }

    // 4. Member Contributions
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
            Icon(Icons.Default.Group, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
            Text("Member Contributions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
          }

          Text("3 Active Members", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }

        // Segmented distribution bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(CircleShape)
            .background(SurfaceContainer)
        ) {
          Box(modifier = Modifier.weight(0.576f).fillMaxSize().background(Primary))
          Box(modifier = Modifier.weight(0.363f).fillMaxSize().background(Secondary))
          Box(modifier = Modifier.weight(0.061f).fillMaxSize().background(SecondaryContainer))
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Primary))
            Text("Rajesh 57.6%", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
          }
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Secondary))
            Text("Priya 36.3%", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
          }
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SecondaryContainer))
            Text("Aarav 6.1%", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
          }
        }

        // Member detailed cards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          members.forEach { m ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceContainerLow)
                .border(1.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(10.dp)
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
                      .clip(CircleShape)
                      .background(
                        when (m.avatarInitial) {
                          "R" -> Primary
                          "P" -> Secondary
                          else -> SecondaryContainer
                        }
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = m.avatarInitial,
                      color = if (m.avatarInitial == "A") OnSecondaryContainer else OnPrimary,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                  }

                  Column {
                    Text(m.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = OnSurface)
                    Text(m.subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, fontSize = 11.sp)
                  }
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(CurrencyFormatter.formatPaise(m.totalPaise), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
                  Text("${String.format(Locale.ENGLISH, "%.1f", m.percentageShare)}% share", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
              }
            }
          }
        }

        Text(
          text = "*Reflects payments logged by member; shared expenses marked accordingly.",
          style = MaterialTheme.typography.labelSmall,
          color = Outline,
          fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
          modifier = Modifier.align(Alignment.CenterHorizontally)
        )
      }
    }

    // 5. Weekly Run Rate & Peak Spending Day
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp)),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
            Icon(Icons.Default.StackedBarChart, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
            Text("Weekly Run Rate", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
          }

          Text("October 2024", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }

        // Weekly run rate bars
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(horizontal = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          weeklyRunRate.forEach { bar ->
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom,
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = CurrencyFormatter.formatCompact(bar.amountPaise),
                style = MaterialTheme.typography.labelSmall,
                color = if (bar.isPeak) Error else OnSurfaceVariant,
                fontWeight = if (bar.isPeak) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.padding(bottom = 4.dp)
              )

              Box(
                modifier = Modifier
                  .width(42.dp)
                  .height((80 * bar.heightFraction).coerceAtLeast(16f).dp)
                  .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                  .background(
                    when {
                      bar.isPeak -> Error
                      bar.isActive -> SecondaryFixedDim
                      else -> Secondary
                    }
                  )
              )

              Text(
                text = bar.label,
                style = MaterialTheme.typography.labelSmall,
                color = if (bar.isPeak) Error else OnSurface,
                fontWeight = if (bar.isPeak) FontWeight.Bold else FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 10.sp
              )
            }
          }
        }

        // Highest Single Spending Day Callout Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow)
            .border(1.dp, OutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ErrorContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.EventBusy, contentDescription = null, tint = OnErrorContainer, modifier = Modifier.size(18.dp))
            }

            Column {
              Text("Highest Single Spending Day", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
              Text(peakSpendingDay.dayLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
              Text(peakSpendingDay.description, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, fontSize = 11.sp)
            }
          }
        }
      }
    }

    // 6. Export Statement & Sheets Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp)),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
          Text("Export Statement & Sheets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
        }

        Text(
          text = "Download verifiable statements for tax planning, domestic accounting, or personal archiving.",
          style = MaterialTheme.typography.bodySmall,
          color = OnSurfaceVariant,
          lineHeight = 16.sp
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceContainer)
              .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
              .clickable { onDownloadPdf() }
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Error, modifier = Modifier.size(18.dp))
              Text("Download PDF", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = OnSurface)
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceContainer)
              .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
              .clickable { onExportCsv() }
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.TableChart, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
              Text("Export CSV", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = OnSurface)
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = Secondary, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Generated 100% on-device • Room DB secured", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
      }
    }

    Spacer(modifier = Modifier.height(60.dp))
  }
}
