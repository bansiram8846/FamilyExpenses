package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

@Database(
  entities = [
    ExpenseEntity::class,
    HouseholdBudgetEntity::class,
    CategoryBudgetEntity::class,
    MemberEntity::class,
    CategoryEntity::class,
    RecurringExpenseEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class FamilyWiseDatabase : RoomDatabase() {
  abstract fun expenseDao(): ExpenseDao

  companion object {
    @Volatile
    private var INSTANCE: FamilyWiseDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): FamilyWiseDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          FamilyWiseDatabase::class.java,
          "familywise_database"
        )
          .addCallback(DatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.expenseDao())
          }
        }
      }
    }

    suspend fun populateInitialData(dao: ExpenseDao) {
      // Seed only default categories. Real expenses and household members are configured by the user during onboarding!
      val categories = listOf(
        CategoryEntity(id = "groceries", name = "Groceries & Kitchen", iconName = "shopping_basket", colorHex = "#006A61", defaultMonthlyBudgetPaise = 1000000L),
        CategoryEntity(id = "education", name = "School & Education", iconName = "school", colorHex = "#131B2E", defaultMonthlyBudgetPaise = 600000L),
        CategoryEntity(id = "utilities", name = "Utilities & Electricity", iconName = "bolt", colorHex = "#565E74", defaultMonthlyBudgetPaise = 500000L),
        CategoryEntity(id = "healthcare", name = "Healthcare & Medicine", iconName = "medical_services", colorHex = "#006F66", defaultMonthlyBudgetPaise = 400000L),
        CategoryEntity(id = "dining", name = "Dining & Food Delivery", iconName = "restaurant", colorHex = "#BA1A1A", defaultMonthlyBudgetPaise = 250000L),
        CategoryEntity(id = "transport", name = "Fuel & Transport", iconName = "directions_car", colorHex = "#3F465C", defaultMonthlyBudgetPaise = 350000L),
        CategoryEntity(id = "shopping", name = "Shopping & Clothing", iconName = "shopping_bag", colorHex = "#7C839B", defaultMonthlyBudgetPaise = 400000L)
      )
      dao.insertCategories(categories)
    }
  }
}
