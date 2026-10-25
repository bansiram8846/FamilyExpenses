package com.example.data.repository

import com.example.data.local.CategoryBudgetEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.ExpenseDao
import com.example.data.local.ExpenseEntity
import com.example.data.local.HouseholdBudgetEntity
import com.example.data.local.MemberEntity
import com.example.data.local.RecurringExpenseEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class ExpenseRepository(private val dao: ExpenseDao) {

  val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpensesFlow()
  val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategoriesFlow()
  val allMembers: Flow<List<MemberEntity>> = dao.getAllMembersFlow()
  val activeRecurring: Flow<List<RecurringExpenseEntity>> = dao.getActiveRecurringExpensesFlow()

  fun getExpensesForMonth(year: Int, month: Int): Flow<List<ExpenseEntity>> {
    val (start, end) = getMonthBounds(year, month)
    return dao.getExpensesBetweenDatesFlow(start, end)
  }

  suspend fun getExpensesForMonthList(year: Int, month: Int): List<ExpenseEntity> {
    val (start, end) = getMonthBounds(year, month)
    return dao.getExpensesBetweenDates(start, end)
  }

  fun getHouseholdBudget(year: Int, month: Int): Flow<HouseholdBudgetEntity?> {
    return dao.getHouseholdBudgetFlow(year, month)
  }

  suspend fun getHouseholdBudgetOnce(year: Int, month: Int): HouseholdBudgetEntity? {
    return dao.getHouseholdBudget(year, month)
  }

  suspend fun saveHouseholdBudget(year: Int, month: Int, amountPaise: Long) {
    dao.insertHouseholdBudget(HouseholdBudgetEntity(year, month, amountPaise))
  }

  fun getCategoryBudgets(year: Int, month: Int): Flow<List<CategoryBudgetEntity>> {
    return dao.getCategoryBudgetsFlow(year, month)
  }

  suspend fun saveCategoryBudget(categoryId: String, year: Int, month: Int, budgetPaise: Long) {
    dao.insertCategoryBudget(CategoryBudgetEntity(categoryId, year, month, budgetPaise))
  }

  suspend fun insertExpense(expense: ExpenseEntity) {
    dao.insertExpense(expense)
  }

  suspend fun updateExpense(expense: ExpenseEntity) {
    dao.updateExpense(expense)
  }

  suspend fun deleteExpense(id: String) {
    dao.deleteExpenseById(id)
  }

  suspend fun getExpenseById(id: String): ExpenseEntity? {
    return dao.getExpenseById(id)
  }

  suspend fun findDuplicates(amountPaise: Long, dateEpochMillis: Long, excludeId: String): List<ExpenseEntity> {
    val windowMillis = 30 * 60 * 1000L // 30 minutes
    val start = dateEpochMillis - windowMillis
    val end = dateEpochMillis + windowMillis
    return dao.findPotentialDuplicates(amountPaise, start, end, excludeId)
  }

  companion object {
    fun getMonthBounds(year: Int, month: Int): Pair<Long, Long> {
      val cal = Calendar.getInstance()
      cal.set(Calendar.YEAR, year)
      cal.set(Calendar.MONTH, month - 1)
      cal.set(Calendar.DAY_OF_MONTH, 1)
      cal.set(Calendar.HOUR_OF_DAY, 0)
      cal.set(Calendar.MINUTE, 0)
      cal.set(Calendar.SECOND, 0)
      cal.set(Calendar.MILLISECOND, 0)
      val start = cal.timeInMillis

      cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
      cal.set(Calendar.HOUR_OF_DAY, 23)
      cal.set(Calendar.MINUTE, 59)
      cal.set(Calendar.SECOND, 59)
      cal.set(Calendar.MILLISECOND, 999)
      val end = cal.timeInMillis

      return Pair(start, end)
    }
  }
}
