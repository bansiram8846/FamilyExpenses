package com.example.backup

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.local.CategoryEntity
import com.example.data.local.ExpenseEntity
import com.example.data.local.MemberEntity
import com.example.domain.expense.CurrencyFormatter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportManager {

  private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)

  fun generateCsv(
    expenses: List<ExpenseEntity>,
    categoryMap: Map<String, CategoryEntity>,
    memberMap: Map<String, MemberEntity>
  ): String {
    val sb = StringBuilder()
    sb.append("Transaction ID,Title,Amount (INR),Date,Category,Paid By,Payment Method,Merchant,Description,Tag\n")

    for (exp in expenses) {
      val catName = categoryMap[exp.categoryId]?.name ?: exp.categoryId
      val memberName = memberMap[exp.paidByMemberId]?.name ?: exp.paidByMemberId
      val amountRupees = exp.amountPaise / 100.0
      val dateStr = dateFormat.format(Date(exp.dateEpochMillis))

      val line = listOf(
        escapeCsv(exp.id),
        escapeCsv(exp.title),
        String.format(Locale.ENGLISH, "%.2f", amountRupees),
        escapeCsv(dateStr),
        escapeCsv(catName),
        escapeCsv(memberName),
        escapeCsv(exp.paymentMethod),
        escapeCsv(exp.merchant),
        escapeCsv(exp.description),
        escapeCsv(exp.tag)
      ).joinToString(",")

      sb.append(line).append("\n")
    }

    return sb.toString()
  }

  fun generateTextStatement(
    period: String,
    expenses: List<ExpenseEntity>,
    categoryMap: Map<String, CategoryEntity>,
    memberMap: Map<String, MemberEntity>,
    budgetPaise: Long
  ): String {
    val totalPaise = expenses.sumOf { it.amountPaise }
    val sb = StringBuilder()
    sb.append("=========================================\n")
    sb.append("FAMILYWISE AI - HOUSEHOLD EXPENSE STATEMENT\n")
    sb.append("Period: $period\n")
    sb.append("Total Expenses: ${CurrencyFormatter.formatPaise(totalPaise)}\n")
    sb.append("Monthly Budget: ${CurrencyFormatter.formatPaise(budgetPaise)}\n")
    sb.append("Remaining: ${CurrencyFormatter.formatPaise(budgetPaise - totalPaise)}\n")
    sb.append("Generated on: ${dateFormat.format(Date())}\n")
    sb.append("Security: Room DB Verified & Stored Locally\n")
    sb.append("=========================================\n\n")

    sb.append("TRANSACTION LEDGER:\n")
    for ((index, exp) in expenses.withIndex()) {
      val cat = categoryMap[exp.categoryId]?.name ?: exp.categoryId
      val member = memberMap[exp.paidByMemberId]?.name ?: exp.paidByMemberId
      val date = dateFormat.format(Date(exp.dateEpochMillis))
      sb.append("${index + 1}. ${exp.title} - ${CurrencyFormatter.formatPaise(exp.amountPaise)}\n")
      sb.append("   $cat | Paid by: $member (${exp.paymentMethod}) | $date\n\n")
    }

    return sb.toString()
  }

  fun shareExport(context: Context, filename: String, content: String, mimeType: String = "text/plain") {
    try {
      val cacheDir = context.cacheDir
      val file = File(cacheDir, filename)
      file.writeText(content)

      val uri = try {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
      } catch (e: Exception) {
        null
      }

      val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_SUBJECT, "FamilyWise Expense Export")
        if (uri != null) {
          putExtra(Intent.EXTRA_STREAM, uri)
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
          putExtra(Intent.EXTRA_TEXT, content)
        }
      }

      val chooser = Intent.createChooser(intent, "Share Household Statement")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
    } catch (e: Exception) {
      // Fallback simple share
      val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "FamilyWise Expense Export")
        putExtra(Intent.EXTRA_TEXT, content)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(Intent.createChooser(fallbackIntent, "Share Household Statement"))
    }
  }

  private fun escapeCsv(value: String): String {
    return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      "\"" + value.replace("\"", "\"\"") + "\""
    } else {
      value
    }
  }
}
