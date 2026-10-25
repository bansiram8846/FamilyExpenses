package com.example.domain.expense

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
  /**
   * Formats paise (1 Rupee = 100 Paise) into Indian currency format: ₹24,650
   */
  fun formatPaise(paise: Long, showSymbol: Boolean = true): String {
    val rupees = paise / 100
    val formattedNumber = formatIndianNumber(rupees)
    return if (showSymbol) "₹$formattedNumber" else formattedNumber
  }

  /**
   * Formats paise into compact representation: ₹24.6k or ₹5.8k
   */
  fun formatCompact(paise: Long): String {
    val rupees = paise / 100.0
    return when {
      rupees >= 10000000 -> String.format(Locale.ENGLISH, "₹%.1fCr", rupees / 10000000)
      rupees >= 100000 -> String.format(Locale.ENGLISH, "₹%.1fL", rupees / 100000)
      rupees >= 1000 -> String.format(Locale.ENGLISH, "₹%.1fk", rupees / 1000)
      else -> "₹${rupees.toLong()}"
    }
  }

  /**
   * Indian Numbering System formatting (e.g. 24,650; 1,85,400; 12,40,000)
   */
  fun formatIndianNumber(number: Long): String {
    val isNegative = number < 0
    val numStr = Math.abs(number).toString()

    if (numStr.length <= 3) {
      return if (isNegative) "-$numStr" else numStr
    }

    val lastThree = numStr.substring(numStr.length - 3)
    val remaining = numStr.substring(0, numStr.length - 3)

    val sb = StringBuilder()
    var count = 0
    for (i in remaining.length - 1 downTo 0) {
      sb.append(remaining[i])
      count++
      if (count == 2 && i != 0) {
        sb.append(',')
        count = 0
      }
    }

    val result = sb.reverse().toString() + "," + lastThree
    return if (isNegative) "-$result" else result
  }

  /**
   * Converts input string (e.g. "1,450" or "1450") to paise
   */
  fun parseToPaise(input: String): Long {
    val cleaned = input.replace("₹", "").replace(",", "").trim()
    return try {
      val doubleVal = cleaned.toDouble()
      Math.round(doubleVal * 100)
    } catch (e: Exception) {
      0L
    }
  }
}
