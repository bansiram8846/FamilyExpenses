package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
  @Query("SELECT * FROM expenses ORDER BY dateEpochMillis DESC")
  fun getAllExpensesFlow(): Flow<List<ExpenseEntity>>

  @Query("SELECT * FROM expenses WHERE dateEpochMillis BETWEEN :startMillis AND :endMillis ORDER BY dateEpochMillis DESC")
  fun getExpensesBetweenDatesFlow(startMillis: Long, endMillis: Long): Flow<List<ExpenseEntity>>

  @Query("SELECT * FROM expenses WHERE dateEpochMillis BETWEEN :startMillis AND :endMillis ORDER BY dateEpochMillis DESC")
  suspend fun getExpensesBetweenDates(startMillis: Long, endMillis: Long): List<ExpenseEntity>

  @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
  suspend fun getExpenseById(id: String): ExpenseEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpense(expense: ExpenseEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpenses(expenses: List<ExpenseEntity>)

  @Update
  suspend fun updateExpense(expense: ExpenseEntity)

  @Delete
  suspend fun deleteExpense(expense: ExpenseEntity)

  @Query("DELETE FROM expenses WHERE id = :id")
  suspend fun deleteExpenseById(id: String)

  @Query("DELETE FROM expenses")
  suspend fun clearAllExpenses()

  @Query("DELETE FROM family_members")
  suspend fun clearAllMembers()

  @Query("DELETE FROM household_budgets")
  suspend fun clearAllBudgets()

  @Query("DELETE FROM category_budgets")
  suspend fun clearAllCategoryBudgets()

  @Query("DELETE FROM recurring_expenses")
  suspend fun clearAllRecurring()

  // Potential duplicate detection: same amount within 30 minutes
  @Query("SELECT * FROM expenses WHERE amountPaise = :amountPaise AND dateEpochMillis BETWEEN :startMillis AND :endMillis AND id != :excludeId")
  suspend fun findPotentialDuplicates(amountPaise: Long, startMillis: Long, endMillis: Long, excludeId: String): List<ExpenseEntity>

  // Household Budgets
  @Query("SELECT * FROM household_budgets WHERE year = :year AND month = :month LIMIT 1")
  fun getHouseholdBudgetFlow(year: Int, month: Int): Flow<HouseholdBudgetEntity?>

  @Query("SELECT * FROM household_budgets WHERE year = :year AND month = :month LIMIT 1")
  suspend fun getHouseholdBudget(year: Int, month: Int): HouseholdBudgetEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHouseholdBudget(budget: HouseholdBudgetEntity)

  // Category Budgets
  @Query("SELECT * FROM category_budgets WHERE year = :year AND month = :month")
  fun getCategoryBudgetsFlow(year: Int, month: Int): Flow<List<CategoryBudgetEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategoryBudget(budget: CategoryBudgetEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategoryBudgets(budgets: List<CategoryBudgetEntity>)

  // Members
  @Query("SELECT * FROM family_members")
  fun getAllMembersFlow(): Flow<List<MemberEntity>>

  @Query("SELECT * FROM family_members")
  suspend fun getAllMembers(): List<MemberEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMembers(members: List<MemberEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMember(member: MemberEntity)

  // Categories
  @Query("SELECT * FROM categories")
  fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM categories")
  suspend fun getAllCategories(): List<CategoryEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<CategoryEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity)

  // Recurring
  @Query("SELECT * FROM recurring_expenses WHERE isActive = 1")
  fun getActiveRecurringExpensesFlow(): Flow<List<RecurringExpenseEntity>>

  @Query("SELECT * FROM recurring_expenses")
  suspend fun getAllRecurringExpenses(): List<RecurringExpenseEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecurringExpense(recurring: RecurringExpenseEntity)
}
