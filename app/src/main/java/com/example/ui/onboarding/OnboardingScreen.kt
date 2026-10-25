package com.example.ui.onboarding

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MemberEntity
import com.example.data.repository.OperatingMode
import com.example.domain.expense.CurrencyFormatter
import com.example.ui.theme.Background
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
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.Tertiary

data class OnboardingMemberItem(
  val name: String,
  val relation: String
)

@Composable
fun OnboardingScreen(
  onCompleteSetup: (
    householdName: String,
    primaryMember: MemberEntity,
    otherMembers: List<MemberEntity>,
    monthlyBudgetPaise: Long,
    mode: OperatingMode
  ) -> Unit
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  // Step 1: Household Name (Starts clean)
  var householdName by remember { mutableStateOf("") }

  // Step 2: Primary Member (Starts clean)
  var primaryName by remember { mutableStateOf("") }
  var primaryRelation by remember { mutableStateOf("Self") }

  // Step 3: Additional Members (Starts clean)
  val additionalMembers = remember {
    mutableStateListOf<OnboardingMemberItem>()
  }
  var newMemberName by remember { mutableStateOf("") }
  var newMemberRelation by remember { mutableStateOf("Spouse") }
  var showAddMemberInputs by remember { mutableStateOf(false) }

  // Step 4: Monthly Budget
  var budgetInput by remember { mutableStateOf("50000") }

  // Step 5: Operating Mode
  var selectedMode by remember { mutableStateOf(OperatingMode.HYBRID) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Background)
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 24.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // Header Hero Banner
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .background(SecondaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.FamilyRestroom,
          contentDescription = "Welcome",
          tint = OnSecondaryContainer,
          modifier = Modifier.size(32.dp)
        )
      }

      Text(
        text = "Welcome to FamilyWise AI",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = OnSurface
      )

      Text(
        text = "Let's personalize your private, offline-first family ledger in 4 quick questions.",
        style = MaterialTheme.typography.bodyMedium,
        color = OnSurfaceVariant,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
    }

    // Question 1: Household Name
    Card(
      modifier = Modifier.fillMaxWidth(),
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
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Primary),
            contentAlignment = Alignment.Center
          ) {
            Text("1", color = OnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Text(
            text = "What is your household or family name?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OnSurface
          )
        }

        OutlinedTextField(
          value = householdName,
          onValueChange = { householdName = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("onboarding_household_name"),
          placeholder = { Text("e.g. Our Family, Smith Household") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurface,
            focusedContainerColor = SurfaceContainerLowest,
            unfocusedContainerColor = SurfaceContainerLowest,
            focusedBorderColor = Secondary,
            unfocusedBorderColor = OutlineVariant
          )
        )

        // Presets chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("Our Family", "My Household", "Home Ledger").forEach { name ->
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(SurfaceContainerLow)
                .clickable { householdName = name }
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(name, fontSize = 11.sp, color = OnSurfaceVariant)
            }
          }
        }
      }
    }

    // Question 2: Primary Member Details
    Card(
      modifier = Modifier.fillMaxWidth(),
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
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Primary),
            contentAlignment = Alignment.Center
          ) {
            Text("2", color = OnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Text(
            text = "Your Name & Family Role",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OnSurface
          )
        }

        OutlinedTextField(
          value = primaryName,
          onValueChange = { primaryName = it },
          label = { Text("Your First Name") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("onboarding_primary_name"),
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurface,
            focusedContainerColor = SurfaceContainerLowest,
            unfocusedContainerColor = SurfaceContainerLowest,
            focusedBorderColor = Secondary,
            unfocusedBorderColor = OutlineVariant
          )
        )

        Text("Your relationship / role:", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Self", "Spouse", "Parent", "Child").forEach { role ->
            val isSelected = primaryRelation == role
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) Primary else SurfaceContainer)
                .clickable { primaryRelation = role }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                role,
                color = if (isSelected) OnPrimary else OnSurface,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }
    }

    // Question 3: Family Members Sharing Expenses
    Card(
      modifier = Modifier.fillMaxWidth(),
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
                .size(28.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
            ) {
              Text("3", color = OnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text(
              text = "Family Members Sharing Expenses",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = OnSurface
            )
          }

          IconButton(
            onClick = { showAddMemberInputs = !showAddMemberInputs },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.PersonAdd, contentDescription = "Add Member", tint = Secondary)
          }
        }

        Text(
          text = "Add spouses, children, or elder parents who make or share household payments:",
          style = MaterialTheme.typography.bodySmall,
          color = OnSurfaceVariant
        )

        // Quick suggestion chips to add members
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("Spouse" to "Spouse", "Child" to "Child", "Parent" to "Parent").forEach { (label, rel) ->
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(SurfaceContainerLow)
                .clickable {
                  newMemberName = ""
                  newMemberRelation = rel
                  showAddMemberInputs = true
                }
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text("+ $label", fontSize = 11.sp, color = Secondary, fontWeight = FontWeight.SemiBold)
            }
          }
        }

        if (additionalMembers.isEmpty() && !showAddMemberInputs) {
          Text(
            text = "No additional members added yet. You can track solo or add family members anytime.",
            style = MaterialTheme.typography.bodySmall,
            color = Outline
          )
        }

        // List of currently added members
        additionalMembers.forEachIndexed { index, member ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SurfaceContainerLow)
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(SecondaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = member.name.take(1).uppercase(),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = OnSecondaryContainer
                )
              }
              Text(
                text = "${member.name} (${member.relation})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
              )
            }

            IconButton(
              onClick = { additionalMembers.removeAt(index) },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Remove", tint = Outline, modifier = Modifier.size(16.dp))
            }
          }
        }

        // Inline input to add another member
        if (showAddMemberInputs) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = newMemberName,
              onValueChange = { newMemberName = it },
              placeholder = { Text("Name") },
              singleLine = true,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
              value = newMemberRelation,
              onValueChange = { newMemberRelation = it },
              placeholder = { Text("Relation") },
              singleLine = true,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            )
            Button(
              onClick = {
                if (newMemberName.isNotBlank()) {
                  additionalMembers.add(OnboardingMemberItem(newMemberName.trim(), newMemberRelation.trim()))
                  newMemberName = ""
                  showAddMemberInputs = false
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Primary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Add")
            }
          }
        } else {
          TextButton(
            onClick = { showAddMemberInputs = true },
            modifier = Modifier.align(Alignment.Start)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
              Text("+ Add Another Member", color = Secondary, style = MaterialTheme.typography.labelMedium)
            }
          }
        }
      }
    }

    // Question 4: Monthly Household Budget
    Card(
      modifier = Modifier.fillMaxWidth(),
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
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Primary),
            contentAlignment = Alignment.Center
          ) {
            Text("4", color = OnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Text(
            text = "Monthly Household Budget",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OnSurface
          )
        }

        Text(
          text = "Enter your family's overall monthly target ceiling. We will alert you at 75% and 100%:",
          style = MaterialTheme.typography.bodySmall,
          color = OnSurfaceVariant
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("₹", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Secondary)
          OutlinedTextField(
            value = budgetInput,
            onValueChange = { budgetInput = it.filter { ch -> ch.isDigit() } },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("onboarding_budget_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = OnSurface,
              unfocusedTextColor = OnSurface,
              focusedContainerColor = SurfaceContainerLowest,
              unfocusedContainerColor = SurfaceContainerLowest,
              focusedBorderColor = Secondary,
              unfocusedBorderColor = OutlineVariant
            )
          )
        }

        // Budget Presets
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("25000", "35000", "50000", "75000").forEach { amount ->
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceContainerLow)
                .clickable { budgetInput = amount }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                "₹${amount.toInt() / 1000}k",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
              )
            }
          }
        }
      }
    }

    // Question 5: Operating Mode (Hybrid vs 100% Offline)
    Card(
      modifier = Modifier.fillMaxWidth(),
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
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Primary),
            contentAlignment = Alignment.Center
          ) {
            Text("5", color = OnPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
          Text(
            text = "AI Privacy & Operating Mode",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OnSurface
          )
        }

        // Hybrid mode choice
        val isHybrid = selectedMode == OperatingMode.HYBRID
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isHybrid) SecondaryContainer.copy(alpha = 0.25f) else SurfaceContainerLow)
            .border(if (isHybrid) 2.dp else 1.dp, if (isHybrid) Secondary else OutlineVariant, RoundedCornerShape(12.dp))
            .clickable { selectedMode = OperatingMode.HYBRID }
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(Icons.Default.CloudSync, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text("Hybrid Mode (Recommended)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = OnSurface)
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Secondary)
                    .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                  Text("Popular", color = OnSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
              Text(
                text = "100% local Room DB for all financial math, with optional cloud AI for conversational advice. Zero PII transmitted.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                fontSize = 11.5.sp
              )
            }
          }
        }

        // Strict 100% offline choice
        val isOffline = selectedMode == OperatingMode.OFFLINE
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isOffline) SecondaryContainer.copy(alpha = 0.25f) else SurfaceContainerLow)
            .border(if (isOffline) 2.dp else 1.dp, if (isOffline) Secondary else OutlineVariant, RoundedCornerShape(12.dp))
            .clickable { selectedMode = OperatingMode.OFFLINE }
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(Icons.Default.CloudOff, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
            Column {
              Text("Strict 100% Offline Mode", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = OnSurface)
              Text(
                text = "Completely air-gapped. Zero network calls ever. Assistant uses local rule-based heuristics only.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                fontSize = 11.5.sp
              )
            }
          }
        }
      }
    }

    // Launch Ledger Button
    Button(
      onClick = {
        val hName = if (householdName.isNotBlank()) householdName.trim() else "My Household"
        val pName = if (primaryName.isNotBlank()) primaryName.trim() else "Admin"
        val budgetPaise = CurrencyFormatter.parseToPaise(budgetInput).takeIf { it > 0L } ?: 3500000L

        val primaryMember = MemberEntity(
          id = pName.lowercase().replace(" ", "_"),
          name = pName,
          relation = primaryRelation,
          avatarInitial = pName.take(1).uppercase(),
          isAdmin = true
        )

        val others = additionalMembers.map {
          MemberEntity(
            id = it.name.lowercase().replace(" ", "_"),
            name = it.name,
            relation = it.relation,
            avatarInitial = it.name.take(1).uppercase(),
            isAdmin = false
          )
        }

        onCompleteSetup(hName, primaryMember, others, budgetPaise, selectedMode)
        Toast.makeText(context, "Household initialized successfully!", Toast.LENGTH_SHORT).show()
      },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("complete_onboarding_button"),
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Primary)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Tertiary, modifier = Modifier.size(20.dp))
        Text(
          text = "Complete Setup & Launch Ledger",
          color = OnPrimary,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
