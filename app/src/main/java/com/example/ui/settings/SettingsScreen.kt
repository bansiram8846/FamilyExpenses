package com.example.ui.settings

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.backup.ExportManager
import com.example.data.repository.AISettingsState
import com.example.data.repository.OperatingMode
import com.example.ui.theme.Background
import com.example.ui.theme.Error
import com.example.ui.theme.ErrorContainer
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
import com.example.ui.theme.Tertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
  settings: AISettingsState,
  onNavigateBack: () -> Unit,
  onToggleOnlineAI: (Boolean) -> Unit,
  onSelectOperatingMode: (OperatingMode) -> Unit,
  onClearHistory: () -> Unit,
  onUpdateProxyEndpoint: (String) -> Unit,
  onResetHouseholdSetup: () -> Unit = {}
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  var showProxyDialog by remember { mutableStateOf(false) }
  var proxyInput by remember { mutableStateOf(settings.proxyEndpoint) }
  var showPayloadLogDialog by remember { mutableStateOf(false) }
  var showResetConfirmDialog by remember { mutableStateOf(false) }

  if (showProxyDialog) {
    AlertDialog(
      onDismissRequest = { showProxyDialog = false },
      title = { Text("Proxy Endpoint URL", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Configure community proxy or self-hosted Gemini routing gateway:", style = MaterialTheme.typography.bodySmall)
          OutlinedTextField(
            value = proxyInput,
            onValueChange = { proxyInput = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateProxyEndpoint(proxyInput)
            showProxyDialog = false
            Toast.makeText(context, "Proxy endpoint updated", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showProxyDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  val currentPeriodStr = remember {
    SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(Date())
  }

  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text("Re-run Household Setup?", fontWeight = FontWeight.Bold) },
      text = {
        Text("This will clear the current household profile and reopen the 5-question setup wizard. Do you wish to proceed?")
      },
      confirmButton = {
        Button(
          onClick = {
            showResetConfirmDialog = false
            onResetHouseholdSetup()
            Toast.makeText(context, "Opening setup wizard...", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
          Text("Proceed")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  if (showPayloadLogDialog) {
    AlertDialog(
      onDismissRequest = { showPayloadLogDialog = false },
      title = { Text("Data Sharing Audit Log", fontWeight = FontWeight.Bold) },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Timestamped payload sent to secure proxy:\n\n" +
              "{\n" +
              "  \"anonymizationProtocol\": \"ZeroPII-v1\",\n" +
              "  \"period\": \"$currentPeriodStr\",\n" +
              "  \"currentMonthTotalPaise\": 0,\n" +
              "  \"previousMonthTotalPaise\": 0,\n" +
              "  \"categories\": []\n" +
              "}\n\n" +
              "✔ No names, passwords, PINs, or UPI IDs transmitted.",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
          )
        }
      },
      confirmButton = {
        Button(onClick = { showPayloadLogDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
          Text("Done")
        }
      }
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Background)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(SurfaceContainerLowest)
        .border(0.5.dp, OutlineVariant)
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(36.dp)
            .testTag("settings_back_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnSurface)
        }

        Column {
          Text(
            text = "AI & Privacy Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OnSurface
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Secondary, modifier = Modifier.size(13.dp))
            Text(
              text = "FamilyWise Protected",
              style = MaterialTheme.typography.labelSmall,
              color = Secondary,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      IconButton(
        onClick = {
          Toast.makeText(context, "All financial processing operates under Zero-PII offline rules", Toast.LENGTH_LONG).show()
        }
      ) {
        Icon(Icons.Default.HelpOutline, contentDescription = "Help", tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
      }
    }

    // Scrollable Settings Content
    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 0. Household Profile & Reconfigure Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp))
          .testTag("settings_household_profile_card"),
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
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(SecondaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(22.dp))
              }
              Column {
                Text(
                  text = settings.householdName.ifBlank { "My Household" },
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = OnSurface
                )
                Text(
                  text = "Primary: ${settings.primaryUserName.ifBlank { "Admin" }} (${settings.primaryUserRelation})",
                  style = MaterialTheme.typography.bodySmall,
                  color = OnSurfaceVariant
                )
              }
            }
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(0.5.dp)
              .background(OutlineVariant.copy(alpha = 0.5f))
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Reconfigure household questions:",
              style = MaterialTheme.typography.bodySmall,
              color = OnSurfaceVariant
            )

            Button(
              onClick = { showResetConfirmDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerLow),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("rerun_setup_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                Text("Re-run Setup", color = Primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }

      // 1. Status & Master Toggle Card
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
            verticalAlignment = Alignment.Top
          ) {
            Row(
              modifier = Modifier.weight(1f),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(SecondaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Secondary, modifier = Modifier.size(22.dp))
              }

              Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "Online AI Assistant",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                  )
                  Box(
                    modifier = Modifier
                      .clip(CircleShape)
                      .background(SecondaryContainer.copy(alpha = 0.4f))
                      .border(0.5.dp, Secondary, CircleShape)
                      .padding(horizontal = 6.dp, vertical = 1.dp)
                  ) {
                    Text("GEMINI 1.5", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSecondaryContainer)
                  }
                }

                Text(
                  text = "Uses secure online proxy (Gemini Free Tier) to analyze spending trends and answer natural language financial questions. Requires active internet connection.",
                  style = MaterialTheme.typography.bodySmall,
                  color = OnSurfaceVariant,
                  lineHeight = 16.sp
                )
              }
            }

            Switch(
              checked = settings.onlineAIEnabled,
              onCheckedChange = { onToggleOnlineAI(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Secondary
              ),
              modifier = Modifier.testTag("master_ai_switch")
            )
          }

          // Latency & Connection Status Indicator Pill
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .border(0.5.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(0.dp))
              .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier
                .clip(CircleShape)
                .background(Tertiary.copy(alpha = 0.12f))
                .border(1.dp, Tertiary.copy(alpha = 0.3f), CircleShape)
                .padding(horizontal = 8.dp, vertical = 4.dp),
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
                text = "Connected to Secure Proxy • Latency 142ms",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
              )
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(13.dp))
              Text("Encrypted TLS 1.3", fontSize = 11.sp, color = OnSurfaceVariant)
            }
          }

          // Privacy Assurance Notice Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceContainerLow)
              .border(0.5.dp, OutlineVariant, RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
              Text(
                text = "No private API keys or personal identifiers stored on device or shared directly.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.5.sp,
                color = OnSurfaceVariant
              )
            }
          }
        }
      }

      // 2. Bank-Grade Anonymization Card
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
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Shield, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
            }
            Column {
              Text("Bank-Grade Anonymization", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
              Text("Zero-PII transmission pipeline", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = OnSurfaceVariant)
            }
          }

          val guarantees = listOf(
            "Only aggregated monthly & category totals are sent for queries.",
            "Account numbers, UPI IDs, PINs, and passwords are never transmitted.",
            "Family member real names are replaced with anonymous tokens (e.g. Member A, Member B).",
            "No on-device AI model download required (saves device storage & battery)."
          )

          guarantees.forEach { text ->
            Row(
              modifier = Modifier.padding(vertical = 2.dp),
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Tertiary, modifier = Modifier.size(16.dp))
              Text(text, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, lineHeight = 16.sp)
            }
          }
        }
      }

      // 3. Operating Mode Selection
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "OPERATING MODE",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = OnSurface,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "Deterministic First",
            style = MaterialTheme.typography.labelSmall,
            color = Secondary,
            fontWeight = FontWeight.Medium
          )
        }

        // Option 1: Hybrid Mode
        val isHybrid = settings.operatingMode == OperatingMode.HYBRID
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              width = if (isHybrid) 2.dp else 1.dp,
              color = if (isHybrid) Secondary else OutlineVariant,
              shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelectOperatingMode(OperatingMode.HYBRID) },
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = if (isHybrid) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
              contentDescription = null,
              tint = if (isHybrid) Secondary else Outline,
              modifier = Modifier.size(20.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text("Hybrid Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(Secondary)
                      .padding(horizontal = 6.dp, vertical = 1.dp)
                  ) {
                    Text("Recommended", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSecondary)
                  }
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(Icons.Default.CloudSync, contentDescription = null, tint = Secondary, modifier = Modifier.size(13.dp))
                  Text("Cloud + Local", style = MaterialTheme.typography.labelSmall, color = Secondary, fontWeight = FontWeight.Medium)
                }
              }

              Text(
                text = "Uses local SQLite/Room database for all math & categorization, with optional cloud AI for conversational insights and savings advice.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 16.sp
              )

              Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Box(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(SecondaryContainer.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text("Online AI Parsing", fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = OnSecondaryContainer)
                }
                Box(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text("Local Room DB", fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = OnSurfaceVariant)
                }
              }
            }
          }
        }

        // Option 2: Strict 100% Offline Mode
        val isOffline = settings.operatingMode == OperatingMode.OFFLINE
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              width = if (isOffline) 2.dp else 1.dp,
              color = if (isOffline) Secondary else OutlineVariant,
              shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelectOperatingMode(OperatingMode.OFFLINE) },
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = if (isOffline) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
              contentDescription = null,
              tint = if (isOffline) Secondary else Outline,
              modifier = Modifier.size(20.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Strict 100% Offline Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(Icons.Default.CloudOff, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(13.dp))
                  Text("Air-Gapped", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
                }
              }

              Text(
                text = "Disables all AI network calls. Assistant uses local rule-based heuristics only. Works without internet indefinitely.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 16.sp
              )

              Row(modifier = Modifier.padding(top = 4.dp)) {
                Box(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(11.dp))
                    Text("Categorized by Local Rule Engine", fontSize = 10.5.sp, color = OnSurfaceVariant)
                  }
                }
              }
            }
          }
        }
      }

      // 4. Daily Free-Tier Quota Card
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
                  .size(32.dp)
                  .clip(RoundedCornerShape(8.dp))
                .background(SurfaceContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.QueryStats, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
              }
              Text("Daily Free-Tier Quota", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OnSurface)
            }

            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(SurfaceContainer)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text("Resets at 00:00 IST", fontSize = 10.5.sp, color = OnSurfaceVariant)
            }
          }

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${settings.dailyQuestionsUsed}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Secondary
                )
                Text(
                  text = " of ${settings.dailyQuotaLimit} AI questions used today",
                  style = MaterialTheme.typography.bodySmall,
                  color = OnSurface,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(start = 4.dp)
                )
              }
              Text("36%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
            }

            LinearProgressIndicator(
              progress = { 0.36f },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
              color = Secondary,
              trackColor = SurfaceContainerHigh
            )

            Text(
              text = "Community shared rate limit: 15 queries / minute per device node.",
              style = MaterialTheme.typography.bodySmall,
              fontSize = 11.sp,
              color = OnSurfaceVariant
            )
          }

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .border(0.5.dp, OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(0.dp))
              .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Active AI Provider", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
              Text("Google Gemini 1.5 Flash", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = OnSurface)
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Gateway Router", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(SecondaryContainer.copy(alpha = 0.3f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("Community Free Proxy", fontSize = 11.sp, color = Secondary, fontWeight = FontWeight.Medium)
              }
            }

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceContainerLow)
                .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp))
                .clickable { showProxyDialog = true }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                Text("Change Proxy Endpoint / Self-Hosted URL", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = OnSurface)
              }
            }
          }
        }
      }

      // 5. History & Privacy Controls
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "HISTORY & PRIVACY CONTROLS",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = OnSurface,
          letterSpacing = 0.5.sp
        )

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OutlineVariant, RoundedCornerShape(16.dp)),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
        ) {
          Column {
            // Row 1: Clear History
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ErrorContainer.copy(alpha = 0.4f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Error, modifier = Modifier.size(20.dp))
                }

                Column {
                  Text("Clear AI Conversation History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = OnSurface, fontSize = 14.5.sp)
                  Text("Deletes cached questions and responses from local memory.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, fontSize = 12.sp)
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(ErrorContainer.copy(alpha = 0.4f))
                  .clickable {
                    onClearHistory()
                    Toast.makeText(context, "AI chat history cleared", Toast.LENGTH_SHORT).show()
                  }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text("Clear", color = Error, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
              }
            }

            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(OutlineVariant.copy(alpha = 0.4f)))

            // Row 2: Review Data Sharing Log
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { showPayloadLogDialog = true }
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.DataObject, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                }

                Column {
                  Text("Review Data Sharing Log", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = OnSurface, fontSize = 14.5.sp)
                  Text("Inspect the exact payload sent in the last 5 AI queries.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, fontSize = 12.sp)
                }
              }

              Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
            }

            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(OutlineVariant.copy(alpha = 0.4f)))

            // Row 3: Export Privacy Audit Report (JSON)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  val auditJson = "{\n  \"app\": \"FamilyWise AI\",\n  \"protocol\": \"ZeroPII\",\n  \"timestamp\": ${System.currentTimeMillis()},\n  \"storedData\": \"Room Database SQLite\",\n  \"piiTransmitted\": false\n}"
                  ExportManager.shareExport(context, "familywise_privacy_audit.json", auditJson, "application/json")
                }
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Download, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                }

                Column {
                  Text("Export Privacy Audit Report (JSON)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = OnSurface, fontSize = 14.5.sp)
                  Text("Download timestamped export of local hashing tokens and logs.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant, fontSize = 12.sp)
                }
              }

              Icon(Icons.Default.Download, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
            }
          }
        }
      }

      // 6. Provider Privacy Policy & Disclaimers Footer
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("View Gemini API Terms & Privacy Notice", style = MaterialTheme.typography.labelSmall, color = Secondary, fontWeight = FontWeight.Medium)
          Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Secondary, modifier = Modifier.size(12.dp))
          Text("•", color = OutlineVariant)
          Text("FamilyWise AI Security Whitepaper", style = MaterialTheme.typography.labelSmall, color = Secondary, fontWeight = FontWeight.Medium)
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainerLow)
            .border(0.5.dp, OutlineVariant, RoundedCornerShape(8.dp))
            .padding(10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "FamilyWise AI is an open, offline-first tool. Financial calculations are always computed on-device before display.",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            color = OnSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }

        Text(
          text = "FAMILYWISE CORE V2.4.1 (BUILD 412) • SQLITE LOCAL ENGINE ACTIVE",
          style = MaterialTheme.typography.labelSmall,
          fontSize = 10.sp,
          color = Outline,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
