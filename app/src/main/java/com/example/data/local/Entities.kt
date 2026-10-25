package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
  @PrimaryKey val id: String,
  val title: String,
  val amountPaise: Long, // paise: 1 Rupee = 100 Paise. Stored as integer
  val dateEpochMillis: Long,
  val categoryId: String,
  val paidByMemberId: String,
  val enteredByMemberId: String,
  val paymentMethod: String, // UPI, Cash, Credit Card, Debit Card, Bank Transfer, Other
  val merchant: String = "",
  val description: String = "",
  val isRecurring: Boolean = false,
  val splitNote: String = "",
  val tag: String = "", // e.g. "Analyzed by FamilyWise AI", "Categorized by Local Rule"
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "household_budgets", primaryKeys = ["year", "month"])
data class HouseholdBudgetEntity(
  val year: Int,
  val month: Int, // 1 - 12
  val totalBudgetPaise: Long
)

@Entity(tableName = "category_budgets", primaryKeys = ["categoryId", "year", "month"])
data class CategoryBudgetEntity(
  val categoryId: String,
  val year: Int,
  val month: Int,
  val budgetPaise: Long
)

@Entity(tableName = "family_members")
data class MemberEntity(
  @PrimaryKey val id: String,
  val name: String,
  val relation: String,
  val avatarInitial: String,
  val isAdmin: Boolean = false
)

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey val id: String,
  val name: String,
  val iconName: String,
  val colorHex: String,
  val defaultMonthlyBudgetPaise: Long = 0L
)

@Entity(tableName = "recurring_expenses")
data class RecurringExpenseEntity(
  @PrimaryKey val id: String,
  val title: String,
  val amountPaise: Long,
  val categoryId: String,
  val paidByMemberId: String,
  val frequency: String, // Monthly, Weekly, Yearly
  val dayOfMonth: Int,
  val isActive: Boolean = true
)
