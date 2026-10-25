package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExpenseEntity
import com.example.domain.expense.CurrencyFormatter
import com.example.ui.theme.Error
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.OnErrorContainer
import com.example.ui.theme.OnPrimary
import com.example.ui.theme.OnSecondary
import com.example.ui.theme.OnSecondaryContainer
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.OutlineVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.Tertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FamilyWiseHeader(
  householdName: String = "Sharma Family",
  periodOrSubtitle: String = "Ledger Active",
  onNotificationsClick: () -> Unit = {},
  onProfileClick: () -> Unit = {},
  showOfflineSubheader: Boolean = true
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(SurfaceContainerLowest)
      .border(0.5.dp, OutlineVariant)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
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
          Icon(
            imageVector = Icons.Default.FamilyRestroom,
            contentDescription = "Family",
            tint = OnSecondaryContainer,
            modifier = Modifier.size(20.dp)
          )
        }

        Column {
          Text(
            text = householdName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
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
              text = periodOrSubtitle,
              style = MaterialTheme.typography.labelSmall,
              color = Secondary,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        IconButton(
          onClick = onNotificationsClick,
          modifier = Modifier.testTag("notifications_button")
        ) {
          BadgedBox(
            badge = {
              Badge(
                containerColor = Secondary,
                modifier = Modifier.size(6.dp)
              )
            }
          ) {
            Icon(
              imageVector = Icons.Outlined.Notifications,
              contentDescription = "Notifications",
              tint = OnSurfaceVariant,
              modifier = Modifier.size(22.dp)
            )
          }
        }

        IconButton(
          onClick = onProfileClick,
          modifier = Modifier.testTag("profile_button")
        ) {
          Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Account Profile",
            tint = Primary,
            modifier = Modifier.size(26.dp)
          )
        }
      }
    }

    if (showOfflineSubheader) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(SurfaceContainerLow)
          .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(Tertiary)
          )
          Text(
            text = "Offline Ready • Stored Locally",
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Synced",
            tint = Secondary,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "Synced",
            style = MaterialTheme.typography.labelSmall,
            color = Secondary,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

@Composable
fun FamilyWiseBottomNavigation(
  currentRoute: String,
  onNavigate: (String) -> Unit,
  onAddClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(64.dp)
      .background(SurfaceContainerLowest)
      .border(0.5.dp, OutlineVariant)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Home
      NavTab(
        label = "Home",
        icon = if (currentRoute == "home") Icons.Default.Home else Icons.Outlined.Home,
        isSelected = currentRoute == "home",
        onClick = { onNavigate("home") },
        testTag = "nav_home"
      )

      // 2. Transactions
      NavTab(
        label = "Transactions",
        icon = if (currentRoute == "transactions") Icons.Default.ReceiptLong else Icons.Outlined.ReceiptLong,
        isSelected = currentRoute == "transactions",
        onClick = { onNavigate("transactions") },
        testTag = "nav_transactions"
      )

      // 3. Add Elevated Button (Center FAB)
      Box(
        modifier = Modifier
          .offset(y = (-14).dp)
          .size(52.dp)
          .clip(CircleShape)
          .background(SurfaceContainerLowest)
          .padding(3.dp)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Secondary)
            .clickable { onAddClick() }
            .testTag("nav_add_expense_fab"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Expense",
            tint = OnSecondary,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      // 4. Budgets
      NavTab(
        label = "Budgets",
        icon = Icons.Outlined.AccountBalanceWallet,
        isSelected = currentRoute == "budgets",
        onClick = { onNavigate("budgets") },
        testTag = "nav_budgets"
      )

      // 5. Reports
      NavTab(
        label = "Reports",
        icon = if (currentRoute == "reports") Icons.Default.BarChart else Icons.Outlined.BarChart,
        isSelected = currentRoute == "reports",
        onClick = { onNavigate("reports") },
        testTag = "nav_reports"
      )
    }
  }
}

@Composable
private fun NavTab(
  label: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .background(if (isSelected) SecondaryContainer else Color.Transparent)
      .clickable { onClick() }
      .padding(horizontal = if (isSelected) 12.dp else 8.dp, vertical = 6.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isSelected) OnSecondaryContainer else OnSurfaceVariant,
        modifier = Modifier.size(20.dp)
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = if (isSelected) OnSecondaryContainer else OnSurfaceVariant,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        fontSize = 10.5.sp
      )
    }
  }
}

@Composable
fun CategoryIcon(
  iconName: String,
  tint: Color = Secondary,
  modifier: Modifier = Modifier
) {
  val imageVector = when (iconName) {
    "shopping_basket" -> Icons.Default.ShoppingBasket
    "school" -> Icons.Default.School
    "bolt" -> Icons.Default.ElectricBolt
    "medical_services" -> Icons.Default.MedicalServices
    "restaurant" -> Icons.Default.Restaurant
    "directions_car" -> Icons.Default.DirectionsCar
    "shopping_bag" -> Icons.Default.ShoppingBag
    "local_drink" -> Icons.Default.LocalDrink
    "storefront" -> Icons.Default.Storefront
    "local_mall" -> Icons.Default.LocalMall
    else -> Icons.Default.ShoppingBasket
  }

  Icon(
    imageVector = imageVector,
    contentDescription = iconName,
    tint = tint,
    modifier = modifier
  )
}
