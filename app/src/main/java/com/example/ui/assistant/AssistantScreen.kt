package com.example.ui.assistant

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AIAssistantService
import com.example.ai.ChatMessage
import com.example.ai.ComparisonTableRow
import com.example.ai.StructuredAIResponse
import com.example.data.local.ExpenseEntity
import com.example.data.repository.OperatingMode
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
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.Tertiary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AssistantScreen(
  currentExpenses: List<ExpenseEntity>,
  previousExpenses: List<ExpenseEntity>,
  budgetPaise: Long,
  operatingMode: OperatingMode,
  isOnlineAIEnabled: Boolean,
  onNavigateBack: () -> Unit,
  onOpenPrivacySettings: () -> Unit,
  onNavigateToTransactions: () -> Unit,
  onToggleOperatingMode: () -> Unit
) {
  val messages = remember {
    mutableStateListOf(
      AIAssistantService.getInitialGreeting(),
      AIAssistantService.getSampleUserQuery(),
      AIAssistantService.getSampleStructuredResponse()
    )
  }

  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  val suggestedQuestions = listOf(
    "Where can our family save ₹3,000 next month?",
    "Which category is closest to exceeding budget?",
    "Show daily spending pace",
    "Project end-of-month total"
  )

  fun handleSend(query: String) {
    if (query.isBlank()) return
    val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
    val userMsg = ChatMessage(
      isUser = true,
      senderName = "Rajesh (Family Admin)",
      timeText = timeFormat.format(Date()),
      text = query.trim()
    )
    messages.add(userMsg)

    val botResponse = AIAssistantService.answerQuestion(
      query = query,
      currentExpenses = currentExpenses,
      previousExpenses = previousExpenses,
      budgetPaise = budgetPaise,
      isOnlineAI = isOnlineAIEnabled && operatingMode == OperatingMode.HYBRID
    )
    messages.add(botResponse)

    inputText = ""
    scope.launch {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Background)
  ) {
    // 1. Top Bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(SurfaceContainerLowest)
        .border(0.5.dp, OutlineVariant)
        .padding(horizontal = 16.dp, vertical = 8.dp)
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
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
              .size(36.dp)
              .testTag("assistant_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnSurface)
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "FamilyWise AI Assistant",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = OnSurface
            )
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          IconButton(
            onClick = onOpenPrivacySettings,
            modifier = Modifier.testTag("assistant_privacy_button")
          ) {
            Icon(Icons.Default.Security, contentDescription = "Privacy Settings", tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
          }

          IconButton(
            onClick = {
              messages.clear()
              messages.add(AIAssistantService.getInitialGreeting())
            },
            modifier = Modifier.testTag("assistant_clear_chat_button")
          ) {
            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
          }
        }
      }

      // Online/Offline Intelligence State Toggle Pill
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 6.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(SurfaceContainerLow)
          .clickable { onToggleOperatingMode() }
          .padding(horizontal = 12.dp, vertical = 8.dp)
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
                .size(8.dp)
                .clip(CircleShape)
                .background(if (operatingMode == OperatingMode.HYBRID) Secondary else Outline)
            )
            Text(
              text = if (operatingMode == OperatingMode.HYBRID) {
                "🟢 Online (Gemini Free Tier Proxy) • Tap to switch to Offline Engine"
              } else {
                "🔒 Strict 100% Offline Mode • Tap to switch to Online AI"
              },
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Medium,
              color = OnSurface
            )
          }

          Icon(Icons.Default.SyncAlt, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(15.dp))
        }
      }
    }

    // 2. Chat Scroll Stream
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(10.dp))
        // Safe & Anonymized Privacy Banner
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .border(0.5.dp, OutlineVariant, RoundedCornerShape(12.dp))
              .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
            Column {
              Text(
                text = "Safe & Anonymized",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
              )
              Text(
                text = "Only verified aggregated totals are sent. No bank passwords, phone PINs, or raw account numbers leave this device.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                lineHeight = 16.sp
              )
            }
          }
        }
      }

      item {
        // Timestamp Divider
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(SurfaceContainer)
              .padding(horizontal = 10.dp, vertical = 2.dp)
          ) {
            Text("Today, 10:42 AM", fontSize = 11.sp, color = OnSurfaceVariant)
          }
        }
      }

      items(messages, key = { it.id }) { msg ->
        if (msg.isUser) {
          UserMessageBubble(msg)
        } else if (msg.structuredResponse != null) {
          AssistantStructuredCard(
            message = msg,
            onActionClick = { actionText ->
              if (actionText.contains("Dining Transactions")) {
                onNavigateToTransactions()
              } else {
                handleSend(actionText)
              }
            }
          )
        } else {
          AssistantSimpleBubble(msg)
        }
      }

      item {
        // Suggested Questions Carousel
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Secondary, modifier = Modifier.size(14.dp))
            Text("Suggested questions for Rajesh", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            for (q in suggestedQuestions) {
              Box(
                modifier = Modifier
                  .clip(CircleShape)
                  .background(SurfaceContainerLowest)
                  .border(1.dp, OutlineVariant, CircleShape)
                  .clickable { handleSend(q) }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = q,
                  style = MaterialTheme.typography.labelSmall,
                  color = OnSurface,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(10.dp))
      }
    }

    // 3. Bottom Input Dock
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(SurfaceContainerLowest)
        .border(0.5.dp, OutlineVariant)
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Mic button
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainer)
            .clickable { handleSend("Project end-of-month total") },
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Secondary, modifier = Modifier.size(20.dp))
        }

        // Input Field
        OutlinedTextField(
          value = inputText,
          onValueChange = { inputText = it },
          modifier = Modifier
            .weight(1f)
            .testTag("assistant_text_input"),
          textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = OnSurface,
            fontWeight = FontWeight.Normal
          ),
          placeholder = { Text("Ask FamilyWise AI about household...", fontSize = 13.sp, color = OnSurfaceVariant) },
          trailingIcon = {
            Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurface,
            focusedContainerColor = SurfaceContainerLowest,
            unfocusedContainerColor = SurfaceContainerLowest,
            focusedBorderColor = Secondary,
            unfocusedBorderColor = OutlineVariant,
            cursorColor = Secondary
          )
        )

        // Send Button
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Primary)
            .clickable { handleSend(inputText) }
            .testTag("assistant_send_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = OnPrimary, modifier = Modifier.size(18.dp))
        }
      }

      Text(
        text = "Offline calculations always available. AI does not execute money transfers or edit records.",
        style = MaterialTheme.typography.labelSmall,
        color = OnSurfaceVariant,
        fontSize = 11.sp,
        modifier = Modifier.align(Alignment.CenterHorizontally)
      )
    }
  }
}

@Composable
private fun UserMessageBubble(msg: ChatMessage) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.End
  ) {
    Column(
      horizontalAlignment = Alignment.End,
      modifier = Modifier.fillMaxWidth(0.85f)
    ) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp))
          .background(Primary)
          .padding(14.dp)
      ) {
        Text(
          text = msg.text,
          style = MaterialTheme.typography.bodyMedium,
          color = OnPrimary,
          lineHeight = 20.sp
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(top = 4.dp, end = 4.dp)
      ) {
        Text(
          text = "${msg.senderName} • ${msg.timeText}",
          style = MaterialTheme.typography.labelSmall,
          color = OnSurfaceVariant
        )
        Icon(Icons.Default.DoneAll, contentDescription = null, tint = Secondary, modifier = Modifier.size(14.dp))
      }
    }
  }
}

@Composable
private fun AssistantSimpleBubble(msg: ChatMessage) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(SecondaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(18.dp))
    }

    Column(modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(2.dp, 16.dp, 16.dp, 16.dp))
          .background(SurfaceContainerLowest)
          .border(1.dp, OutlineVariant, RoundedCornerShape(2.dp, 16.dp, 16.dp, 16.dp))
          .padding(14.dp)
      ) {
        Text(
          text = msg.text,
          style = MaterialTheme.typography.bodyMedium,
          color = OnSurface,
          lineHeight = 20.sp
        )
      }

      Text(
        text = "${msg.senderName} • ${msg.timeText}",
        style = MaterialTheme.typography.labelSmall,
        color = OnSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
      )
    }
  }
}

@Composable
private fun AssistantStructuredCard(
  message: ChatMessage,
  onActionClick: (String) -> Unit
) {
  val resp = message.structuredResponse ?: return

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(SecondaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = OnSecondaryContainer, modifier = Modifier.size(18.dp))
    }

    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, OutlineVariant, RoundedCornerShape(2.dp, 16.dp, 16.dp, 16.dp)),
        shape = RoundedCornerShape(2.dp, 16.dp, 16.dp, 16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
      ) {
        Column {
          // Card Header
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(SurfaceContainerLow)
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = resp.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = OnSurface
            )

            Row(
              modifier = Modifier
                .clip(CircleShape)
                .background(SurfaceContainer)
                .padding(horizontal = 8.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.SettingsSuggest, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(13.dp))
              Text(
                text = resp.attribution,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
              )
            }
          }

          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Key finding box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SecondaryContainer.copy(alpha = 0.25f))
                .border(1.dp, SecondaryContainer, RoundedCornerShape(12.dp))
                .padding(12.dp)
            ) {
              Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(Icons.Default.Insights, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                Text(
                  text = resp.keyFinding,
                  style = MaterialTheme.typography.bodyMedium,
                  color = OnSurface,
                  lineHeight = 20.sp
                )
              }
            }

            // Comparison table
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
            ) {
              Column(modifier = Modifier.border(0.5.dp, OutlineVariant, RoundedCornerShape(10.dp))) {
                // Table header
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainer)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Category", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                  Text("Sep → Oct", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                  Text("Variance", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }

                // Table rows
                resp.tableRows.forEachIndexed { idx, row ->
                  if (idx > 0) {
                    Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(OutlineVariant.copy(alpha = 0.4f)))
                  }
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                      CategoryIcon(
                        iconName = row.iconName,
                        tint = if (row.isAlert) Error else Secondary,
                        modifier = Modifier.size(16.dp)
                      )
                      Text(row.categoryName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = OnSurface)
                    }

                    Text(row.sepToOctText, style = MaterialTheme.typography.bodySmall, color = OnSurface)

                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (row.isAlert) ErrorContainer else SecondaryContainer)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = row.varianceText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (row.isAlert) OnErrorContainer else OnSecondaryContainer
                      )
                    }
                  }
                }
              }
            }

            // Advice box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLow)
                .border(0.5.dp, OutlineVariant, RoundedCornerShape(12.dp))
                .padding(12.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                  Text("Practical Household Advice", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Secondary)
                }
                Text(
                  text = resp.advice,
                  style = MaterialTheme.typography.bodySmall,
                  color = OnSurface,
                  lineHeight = 18.sp
                )
              }
            }

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(Primary)
                  .clickable { onActionClick(resp.primaryActionText) }
                  .padding(12.dp)
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
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(16.dp))
                    Text(resp.primaryActionText, color = OnPrimary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                  }
                  Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(16.dp))
                }
              }

              if (resp.secondaryActions.isNotEmpty()) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  resp.secondaryActions.forEach { act ->
                    Box(
                      modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainer)
                        .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                        .clickable { onActionClick(act) }
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = act,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        maxLines = 1
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      // Card subtext
      Row(
        modifier = Modifier.padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "${message.senderName} • ${message.timeText}",
          style = MaterialTheme.typography.labelSmall,
          color = OnSurfaceVariant
        )
        Text("•", style = MaterialTheme.typography.labelSmall, color = Outline)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp),
          modifier = Modifier.clickable { }
        ) {
          Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Secondary, modifier = Modifier.size(13.dp))
          Text("Helpful", style = MaterialTheme.typography.labelSmall, color = Secondary)
        }
        Text("•", style = MaterialTheme.typography.labelSmall, color = Outline)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp),
          modifier = Modifier.clickable { }
        ) {
          Icon(Icons.Default.Share, contentDescription = null, tint = Error, modifier = Modifier.size(13.dp))
          Text("Share", style = MaterialTheme.typography.labelSmall, color = Error)
        }
      }
    }
  }
}
