package com.example.ui.expense

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExpenseEntity
import com.example.domain.expense.CurrencyFormatter
import com.example.domain.insights.LocalInsightEngine
import com.example.ui.theme.Background
import com.example.ui.theme.Error
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
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.SurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AddExpenseScreen(
  existingExpense: ExpenseEntity? = null,
  onClose: () -> Unit,
  onSaveExpense: (ExpenseEntity, Boolean) -> Unit // (expense, addAnother)
) {
  var amountInput by remember {
    mutableStateOf(if (existingExpense != null) (existingExpense.amountPaise / 100).toString() else "1450")
  }
  var selectedCategory by remember {
    mutableStateOf(existingExpense?.categoryId ?: "groceries")
  }
  var selectedMember by remember {
    mutableStateOf(existingExpense?.paidByMemberId ?: "rajesh")
  }
  var selectedPaymentMethod by remember {
    mutableStateOf(existingExpense?.paymentMethod ?: "UPI")
  }
  var merchantDesc by remember {
    mutableStateOf(existingExpense?.title ?: "Blinkit organic milk and veggies")
  }
  var isRecurring by remember {
    mutableStateOf(existingExpense?.isRecurring ?: false)
  }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  // Auto-category heuristic suggestion
  val suggestedCat = remember(merchantDesc) {
    LocalInsightEngine.suggestCategory(merchantDesc)
  }

  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(SurfaceContainerLowest)
  ) {
    // Top drag handle & header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(SurfaceContainerLowest)
        .border(0.5.dp, OutlineVariant)
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(40.dp, 4.dp)
            .clip(CircleShape)
            .background(OutlineVariant)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = onClose,
            modifier = Modifier
              .size(36.dp)
              .testTag("close_add_expense_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = OnSurface)
          }

          Column {
            Text(
              text = if (existingExpense != null) "Edit Household Expense" else "Add Household Expense",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = OnSurface
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(Secondary)
              )
              Text(
                text = "Saved directly to local Room database • 100% offline",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
              )
            }
          }
        }

        // Quick Presets Pill
        Row(
          modifier = Modifier
            .clip(CircleShape)
            .background(SurfaceContainer)
            .clickable {
              amountInput = "500"
              merchantDesc = "Mother Dairy milk & bread"
              selectedCategory = "groceries"
            }
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.Bolt, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
          Text("Quick Presets", style = MaterialTheme.typography.labelMedium, color = Secondary, fontWeight = FontWeight.SemiBold)
        }
      }
    }

    // Scrollable Form Body
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Prominent Amount Display & Denominations
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Total Amount (₹)",
            style = MaterialTheme.typography.labelMedium,
            color = OnSurfaceVariant,
            fontWeight = FontWeight.Medium
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "₹",
              style = MaterialTheme.typography.displayMedium,
              fontWeight = FontWeight.Bold,
              color = Secondary,
              modifier = Modifier.padding(end = 6.dp)
            )

            TextField(
              value = amountInput,
              onValueChange = { input ->
                val filtered = input.filter { it.isDigit() }
                amountInput = filtered
              },
              modifier = Modifier
                .width(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                .testTag("expense_amount_input"),
              textStyle = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                color = OnSurface
              ),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceContainerLowest,
                unfocusedContainerColor = SurfaceContainerLowest,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
              )
            )
          }

          // Quick Denominations (+₹100, +₹500, +₹1,000, +₹2,000)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(100, 500, 1000, 2000).forEach { addVal ->
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(SurfaceContainerLowest)
                  .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                  .clickable {
                    val currentVal = amountInput.toLongOrNull() ?: 0L
                    amountInput = (currentVal + addVal).toString()
                  }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "+₹$addVal",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.SemiBold,
                  color = OnSurface
                )
              }
            }
          }
        }
      }

      // 2. Smart Category Section
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
          )

          Row(
            modifier = Modifier
              .clip(CircleShape)
              .background(SecondaryContainer)
              .clickable { selectedCategory = suggestedCat }
              .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(13.dp))
            Text(
              text = "Suggested: ${suggestedCat.replaceFirstChar { it.uppercase() }}",
              style = MaterialTheme.typography.labelSmall,
              color = OnSecondaryContainer,
              fontWeight = FontWeight.Medium
            )
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(13.dp))
          }
        }

        // Category Pills Grid (8 items from design)
        val categories = listOf(
          Triple("groceries", "Groceries (Ration)", Icons.Default.ShoppingBasket),
          Triple("dining", "Dining Out", Icons.Default.Restaurant),
          Triple("utilities", "Utilities", Icons.Default.Bolt),
          Triple("education", "School Fees", Icons.Default.School),
          Triple("dairy", "Milk & Dairy", Icons.Default.LocalCafe),
          Triple("transport", "Fuel / Petrol", Icons.Default.LocalGasStation),
          Triple("healthcare", "Medicine", Icons.Default.Medication),
          Triple("shopping", "Shopping", Icons.Default.ShoppingBag)
        )

        val chunked = categories.chunked(2)
        for (row in chunked) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            for (cat in row) {
              val isSelected = selectedCategory == cat.first
              Row(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) Primary else SurfaceContainerLowest)
                  .border(1.dp, if (isSelected) Primary else OutlineVariant, RoundedCornerShape(12.dp))
                  .clickable { selectedCategory = cat.first }
                  .padding(horizontal = 12.dp, vertical = 10.dp)
                  .testTag("category_pill_${cat.first}"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = cat.third,
                  contentDescription = cat.second,
                  tint = if (isSelected) OnPrimary else OnSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = cat.second,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) OnPrimary else OnSurface,
                  maxLines = 1
                )
              }
            }
          }
        }
      }

      // 3. Paid By (Avatar Selector Chips)
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Paid By",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = OnSurface
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val members = listOf(
            Triple("rajesh", "Rajesh (Self)", "R"),
            Triple("priya", "Priya (Wife)", "P"),
            Triple("aarav", "Aarav (Son)", "A"),
            Triple("dadi", "Dadi (Mother)", "D")
          )

          for (m in members) {
            val isSelected = selectedMember == m.first
            Row(
              modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) Primary else SurfaceContainer)
                .border(1.dp, if (isSelected) Primary else OutlineVariant, CircleShape)
                .clickable { selectedMember = m.first }
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(22.dp)
                  .clip(CircleShape)
                  .background(if (isSelected) SecondaryFixed else SurfaceVariant),
                contentAlignment = Alignment.Center
              ) {
                Text(m.third, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
              }
              Text(
                text = m.second,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) OnPrimary else OnSurface
              )
            }
          }
        }
      }

      // 4. Payment Method Horizontal Controls
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Payment Method",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = OnSurface
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val methods = listOf(
            Pair("UPI", Icons.Default.QrCodeScanner),
            Pair("Cash", Icons.Default.Payments),
            Pair("Credit Card", Icons.Default.CreditCard),
            Pair("Debit Card", Icons.Default.CreditCard),
            Pair("Bank Transfer", Icons.Default.AccountBalance)
          )

          for (method in methods) {
            val isSelected = selectedPaymentMethod == method.first
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) Secondary else SurfaceContainerLowest)
                .border(1.dp, if (isSelected) Secondary else OutlineVariant, RoundedCornerShape(12.dp))
                .clickable { selectedPaymentMethod = method.first }
                .padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = method.second,
                contentDescription = method.first,
                tint = if (isSelected) OnSecondary else OnSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = method.first,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) OnSecondary else OnSurfaceVariant
              )
            }
          }
        }
      }

      // 5. Merchant / Description Field
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Merchant / Description",
          style = MaterialTheme.typography.labelMedium,
          color = OnSurfaceVariant
        )

        TextField(
          value = merchantDesc,
          onValueChange = { merchantDesc = it },
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            .testTag("expense_description_input"),
          leadingIcon = {
            Icon(Icons.Default.Storefront, contentDescription = null, tint = Outline, modifier = Modifier.size(20.dp))
          },
          singleLine = true,
          colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceContainerLowest,
            unfocusedContainerColor = SurfaceContainerLowest,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
          )
        )
      }

      // 6. Date & Time Row
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
            Column {
              Text("Date & Time", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
              val sdf = SimpleDateFormat("EEEE, d MMM • hh:mm a", Locale.ENGLISH)
              val dateStr = sdf.format(Date(existingExpense?.dateEpochMillis ?: System.currentTimeMillis()))
              Text(dateStr, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = OnSurface)
            }
          }

          Icon(Icons.Default.Edit, contentDescription = "Edit date", tint = Outline, modifier = Modifier.size(16.dp))
        }
      }

      // 7. Recurring Monthly Bill Switch
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
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
                .background(SurfaceVariant),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Autorenew, contentDescription = null, tint = OnSurface, modifier = Modifier.size(18.dp))
            }
            Column {
              Text("Recurring Monthly Bill", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = OnSurface)
              Text("Auto-flag in monthly household forecast", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
          }

          Switch(
            checked = isRecurring,
            onCheckedChange = { isRecurring = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = Secondary
            ),
            modifier = Modifier.testTag("recurring_switch")
          )
        }
      }

      if (errorMessage != null) {
        Text(
          text = errorMessage ?: "",
          color = Error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(horizontal = 4.dp)
        )
      }
    }

    // Action Footer
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(SurfaceContainerLowest)
        .border(0.5.dp, OutlineVariant)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Shield, contentDescription = null, tint = Secondary, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "AI will never auto-save or modify transactions without your direct confirmation.",
          style = MaterialTheme.typography.labelSmall,
          color = OnSurfaceVariant,
          fontSize = 11.sp
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Save & Add Another Button
        OutlinedButton(
          onClick = {
            val amountPaise = CurrencyFormatter.parseToPaise(amountInput)
            if (amountPaise <= 0L) {
              errorMessage = "Please enter a valid amount greater than ₹0."
              return@OutlinedButton
            }
            val title = if (merchantDesc.isNotBlank()) merchantDesc.trim() else "Household Expense"
            val expense = ExpenseEntity(
              id = existingExpense?.id ?: ("exp-" + UUID.randomUUID().toString().take(8)),
              title = title,
              amountPaise = amountPaise,
              dateEpochMillis = existingExpense?.dateEpochMillis ?: System.currentTimeMillis(),
              categoryId = selectedCategory,
              paidByMemberId = selectedMember,
              enteredByMemberId = "rajesh",
              paymentMethod = selectedPaymentMethod,
              merchant = title,
              description = title,
              isRecurring = isRecurring,
              tag = if (suggestedCat == selectedCategory) "Categorized by Local Rule" else ""
            )
            onSaveExpense(expense, true)
            // Reset for next entry
            amountInput = ""
            merchantDesc = ""
            errorMessage = null
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("save_and_add_another_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Save & Add Another", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
          }
        }

        // Save Expense Button (Primary)
        Button(
          onClick = {
            val amountPaise = CurrencyFormatter.parseToPaise(amountInput)
            if (amountPaise <= 0L) {
              errorMessage = "Please enter a valid amount greater than ₹0."
              return@Button
            }
            val title = if (merchantDesc.isNotBlank()) merchantDesc.trim() else "Household Expense"
            val expense = ExpenseEntity(
              id = existingExpense?.id ?: ("exp-" + UUID.randomUUID().toString().take(8)),
              title = title,
              amountPaise = amountPaise,
              dateEpochMillis = existingExpense?.dateEpochMillis ?: System.currentTimeMillis(),
              categoryId = selectedCategory,
              paidByMemberId = selectedMember,
              enteredByMemberId = "rajesh",
              paymentMethod = selectedPaymentMethod,
              merchant = title,
              description = title,
              isRecurring = isRecurring,
              tag = if (suggestedCat == selectedCategory) "Categorized by Local Rule" else ""
            )
            onSaveExpense(expense, false)
          },
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("save_expense_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = SecondaryFixed, modifier = Modifier.size(18.dp))
            Text("Save Expense", color = OnPrimary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
