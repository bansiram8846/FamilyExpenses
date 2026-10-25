package com.example.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExpenseEntity
import com.example.domain.expense.CurrencyFormatter
import com.example.ui.components.CategoryIcon
import com.example.ui.theme.Background
import com.example.ui.theme.Error
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.OnErrorContainer
import com.example.ui.theme.OnPrimary
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun TransactionsScreen(
  expenses: List<ExpenseEntity>,
  onEditExpense: (ExpenseEntity) -> Unit,
  onDeleteExpense: (String) -> Unit,
  onExportCsv: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategoryFilter by remember { mutableStateOf("All") }
  var selectedMemberFilter by remember { mutableStateOf("All") }
  var showDuplicateNotice by remember { mutableStateOf(true) }
  var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

  // Detect genuine duplicate entries dynamically from real data
  val duplicatePair = remember(expenses) {
    var found: Pair<ExpenseEntity, ExpenseEntity>? = null
    for (i in 0 until expenses.size) {
      for (j in i + 1 until expenses.size) {
        val a = expenses[i]
        val b = expenses[j]
        if (a.amountPaise == b.amountPaise &&
          (a.title.equals(b.title, ignoreCase = true) || (a.merchant.isNotBlank() && a.merchant.equals(b.merchant, ignoreCase = true))) &&
          abs(a.dateEpochMillis - b.dateEpochMillis) < 86400000L
        ) {
          found = Pair(a, b)
          break
        }
      }
      if (found != null) break
    }
    found
  }

  val filteredExpenses = expenses.filter { exp ->
    val matchesSearch = searchQuery.isEmpty() ||
      exp.title.contains(searchQuery, ignoreCase = true) ||
      exp.merchant.contains(searchQuery, ignoreCase = true) ||
      exp.description.contains(searchQuery, ignoreCase = true)

    val matchesCat = selectedCategoryFilter == "All" ||
      exp.categoryId.equals(selectedCategoryFilter, ignoreCase = true)

    val matchesMember = selectedMemberFilter == "All" ||
      exp.paidByMemberId.equals(selectedMemberFilter, ignoreCase = true)

    matchesSearch && matchesCat && matchesMember
  }

  val totalSpentPaise = filteredExpenses.sumOf { it.amountPaise }
  val scrollState = rememberScrollState()

  // Confirmation dialog for delete
  if (expenseToDelete != null) {
    AlertDialog(
      onDismissRequest = { expenseToDelete = null },
      title = { Text("Delete Transaction?", fontWeight = FontWeight.Bold) },
      text = { Text("Are you sure you want to delete \"${expenseToDelete?.title}\"? This action cannot be undone.") },
      confirmButton = {
        Button(
          onClick = {
            expenseToDelete?.let { onDeleteExpense(it.id) }
            expenseToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = Error)
        ) {
          Text("Delete", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { expenseToDelete = null }) {
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
    // 1. Title & Export Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Transactions (October 2024)",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = OnSurface
      )

      IconButton(
        onClick = onExportCsv,
        modifier = Modifier.testTag("export_transactions_button")
      ) {
        Icon(
          imageVector = Icons.Default.FileDownload,
          contentDescription = "Export Passbook",
          tint = OnSurfaceVariant,
          modifier = Modifier.size(22.dp)
        )
      }
    }

    // 2. Search Input
    TextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
        .testTag("transactions_search_input"),
      placeholder = { Text("Search grocery, Swiggy, electricity, merchant...", color = Outline, fontSize = 13.sp) },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = "Search", tint = Outline, modifier = Modifier.size(20.dp))
      },
      trailingIcon = {
        Icon(Icons.Default.Mic, contentDescription = "Voice Search", tint = Outline, modifier = Modifier.size(18.dp))
      },
      singleLine = true,
      colors = TextFieldDefaults.colors(
        focusedContainerColor = SurfaceContainerLowest,
        unfocusedContainerColor = SurfaceContainerLow,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
      )
    )

    // 3. Scrollable Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Date Pill
      Row(
        modifier = Modifier
          .clip(CircleShape)
          .background(Primary)
          .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text("Date: This Month", color = OnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(16.dp))
      }

      // Category Pill
      val nextCat = when (selectedCategoryFilter) {
        "All" -> "Groceries"
        "Groceries" -> "Dining"
        "Dining" -> "Utilities"
        else -> "All"
      }
      Row(
        modifier = Modifier
          .clip(CircleShape)
          .background(if (selectedCategoryFilter != "All") SecondaryContainer else SurfaceContainerLow)
          .border(1.dp, OutlineVariant, CircleShape)
          .clickable { selectedCategoryFilter = nextCat }
          .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          "Category: $selectedCategoryFilter",
          color = if (selectedCategoryFilter != "All") OnSecondaryContainer else OnSurfaceVariant,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
      }

      // Paid By Pill
      val nextMember = when (selectedMemberFilter) {
        "All" -> "Rajesh"
        "Rajesh" -> "Priya"
        "Priya" -> "Aarav"
        else -> "All"
      }
      Row(
        modifier = Modifier
          .clip(CircleShape)
          .background(if (selectedMemberFilter != "All") SecondaryContainer else SurfaceContainerLow)
          .border(1.dp, OutlineVariant, CircleShape)
          .clickable { selectedMemberFilter = nextMember }
          .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          "Paid by: $selectedMemberFilter",
          color = if (selectedMemberFilter != "All") OnSecondaryContainer else OnSurfaceVariant,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium
        )
        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
      }

      // Sort Pill
      Row(
        modifier = Modifier
          .clip(CircleShape)
          .background(SurfaceContainerLow)
          .border(1.dp, OutlineVariant, CircleShape)
          .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Icon(Icons.Default.SwapVert, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
        Text("Sort: Newest", color = OnSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Medium)
      }
    }

    // 4. Monthly Aggregation Banner Card
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
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "Monthly Aggregation",
              style = MaterialTheme.typography.labelMedium,
              color = OnSurfaceVariant
            )
            val monthAbbr = remember {
              SimpleDateFormat("MMM", Locale.ENGLISH).format(Date()).uppercase()
            }
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(SurfaceContainerHigh)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(monthAbbr, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary)
            }
          }
          Text(
            text = "${filteredExpenses.size} Transactions Logged",
            style = MaterialTheme.typography.labelLarge,
            color = Outline,
            modifier = Modifier.padding(top = 2.dp)
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "TOTAL SPENT",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Secondary
          )
          Text(
            text = CurrencyFormatter.formatPaise(totalSpentPaise),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = OnSurface
          )
        }
      }
    }

    // 5. Contextual Intelligence / Duplicate Warning Notice Card
    if (duplicatePair != null && showDuplicateNotice) {
      val (dup1, dup2) = duplicatePair
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Error.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
          .testTag("duplicate_warning_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ErrorContainer.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.Top
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Error.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "Warning",
              tint = Error,
              modifier = Modifier.size(18.dp)
            )
          }

          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Possible Duplicate Detected",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = OnErrorContainer
              )
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(SurfaceContainerLowest)
                  .border(0.5.dp, OutlineVariant, RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("AI Guard", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = OnSurfaceVariant)
              }
            }

            Text(
              text = "2 entries of ${CurrencyFormatter.formatPaise(dup1.amountPaise)} for \"${dup1.title}\" logged within 24 hours.",
              style = MaterialTheme.typography.bodySmall,
              color = OnSurfaceVariant,
              lineHeight = 16.sp
            )

            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(SurfaceContainerLowest)
                  .border(1.dp, Error.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                  .clickable {
                    onEditExpense(dup2)
                    showDuplicateNotice = false
                  }
                  .padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Text(
                  "Review Entry",
                  color = Error,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }

              TextButton(
                onClick = { showDuplicateNotice = false },
                modifier = Modifier.height(28.dp)
              ) {
                Text("Dismiss", color = OnSurfaceVariant, fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // 6. Grouped Expenses by Date OR Empty State
    if (filteredExpenses.isEmpty()) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
          .testTag("empty_transactions_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Secondary, modifier = Modifier.size(24.dp))
            }
            Text(
              text = if (searchQuery.isNotEmpty()) "No matching transactions" else "No transactions recorded yet",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = OnSurface
            )
            Text(
              text = if (searchQuery.isNotEmpty()) {
                "Try searching for another keyword or clear your filters"
              } else {
                "Tap the + button below to log your family's first expense"
              },
              style = MaterialTheme.typography.bodySmall,
              color = OnSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }
    } else {
      val sdfDateHeader = SimpleDateFormat("EEEE, d MMM yyyy", Locale.ENGLISH)
      val sdfDayHeader = SimpleDateFormat("EEEE, d MMM", Locale.ENGLISH)
      val nowCal = Calendar.getInstance()
      val groupedExpenses = filteredExpenses.groupBy { exp ->
        val expCal = Calendar.getInstance().apply { timeInMillis = exp.dateEpochMillis }
        val isSameYear = nowCal.get(Calendar.YEAR) == expCal.get(Calendar.YEAR)
        val isToday = isSameYear && nowCal.get(Calendar.DAY_OF_YEAR) == expCal.get(Calendar.DAY_OF_YEAR)
        val isYesterday = isSameYear && (nowCal.get(Calendar.DAY_OF_YEAR) - expCal.get(Calendar.DAY_OF_YEAR) == 1)
        when {
          isToday -> "Today - ${sdfDayHeader.format(Date(exp.dateEpochMillis))}"
          isYesterday -> "Yesterday - ${sdfDayHeader.format(Date(exp.dateEpochMillis))}"
          else -> sdfDateHeader.format(Date(exp.dateEpochMillis))
        }
      }

      for ((dateHeader, dayExpenses) in groupedExpenses) {
        val dayTotal = dayExpenses.sumOf { it.amountPaise }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Group Header
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = dateHeader,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = OnSurface
              )
              if (dateHeader.startsWith("Today")) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Secondary)
                )
              }
            }

            Text(
              text = "Day Total: ${CurrencyFormatter.formatPaise(dayTotal)}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = OnSurfaceVariant
            )
          }

          // Transactions Cards in this Day Group
          for (exp in dayExpenses) {
            TransactionCardItem(
              expense = exp,
              onEdit = { onEditExpense(exp) },
              onDelete = { expenseToDelete = exp }
            )
          }
        }
      }
    }

    // End of visible entries footer
    val currentPeriodStr = remember {
      SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(Date())
    }
    Text(
      text = "Passbook Ledger • $currentPeriodStr",
      style = MaterialTheme.typography.labelSmall,
      color = Outline,
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(vertical = 12.dp)
    )

    Spacer(modifier = Modifier.height(60.dp))
  }
}

@Composable
private fun TransactionCardItem(
  expense: ExpenseEntity,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
  val timeStr = timeFormat.format(Date(expense.dateEpochMillis))

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, OutlineVariant, RoundedCornerShape(14.dp))
      .testTag("transaction_item_${expense.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top
    ) {
      Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLow),
          contentAlignment = Alignment.Center
        ) {
          CategoryIcon(
            iconName = when (expense.categoryId) {
              "groceries" -> if (expense.title.contains("Milk")) "local_drink" else "shopping_basket"
              "utilities" -> "bolt"
              "healthcare" -> "medical_services"
              "transport" -> "directions_car"
              "dining" -> "restaurant"
              else -> "shopping_basket"
            },
            tint = Secondary,
            modifier = Modifier.size(20.dp)
          )
        }

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = expense.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = OnSurface
            )
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(SurfaceContainer)
                .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
              Text(
                text = expense.categoryId.replaceFirstChar { it.uppercase() },
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurfaceVariant
              )
            }
          }

          Text(
            text = "Paid by ${expense.paidByMemberId.replaceFirstChar { it.uppercase() }} (${expense.paymentMethod}) • $timeStr",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
          )

          if (expense.splitNote.isNotEmpty()) {
            Text(
              text = expense.splitNote,
              style = MaterialTheme.typography.labelSmall,
              color = Outline,
              fontSize = 11.sp
            )
          }

          if (expense.tag.isNotEmpty()) {
            Row(
              modifier = Modifier
                .clip(CircleShape)
                .background(if (expense.tag.contains("AI")) SecondaryContainer.copy(alpha = 0.5f) else SurfaceContainerLow)
                .border(0.5.dp, OutlineVariant, CircleShape)
                .padding(horizontal = 8.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = if (expense.tag.contains("AI")) Icons.Default.AutoAwesome else Icons.Default.Settings,
                contentDescription = null,
                tint = if (expense.tag.contains("AI")) OnSecondaryContainer else OnSurfaceVariant,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = expense.tag,
                fontSize = 10.5.sp,
                color = if (expense.tag.contains("AI")) OnSecondaryContainer else OnSurfaceVariant,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = CurrencyFormatter.formatPaise(expense.amountPaise),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = OnSurface
        )

        Row(
          modifier = Modifier.padding(top = 14.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceContainerLow)
              .clickable { onEdit() },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = OnSurfaceVariant, modifier = Modifier.size(15.dp))
          }

          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceContainerLow)
              .clickable { onDelete() },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Error, modifier = Modifier.size(15.dp))
          }
        }
      }
    }
  }
}
