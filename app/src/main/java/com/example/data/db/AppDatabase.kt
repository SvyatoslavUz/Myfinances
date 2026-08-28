package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [CategoryEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDefaultData(database.categoryDao(), database.transactionDao())
                    }
                }
            }
        }

        suspend fun populateDefaultData(categoryDao: CategoryDao, transactionDao: TransactionDao) {
            if (categoryDao.getCategoryCount() > 0) return

            // Income Categories
            val incomeCategories = listOf(
                CategoryEntity(
                    name = "Зарплата",
                    type = "INCOME",
                    iconName = "work",
                    colorHex = 0xFF10B981, // Emerald Green
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Перевод от родных и близких",
                    type = "INCOME",
                    iconName = "family",
                    colorHex = 0xFF06B6D4, // Cyan
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Подработка",
                    type = "INCOME",
                    iconName = "laptop",
                    colorHex = 0xFF3B82F6, // Blue
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Инвестиции",
                    type = "INCOME",
                    iconName = "trending_up",
                    colorHex = 0xFF8B5CF6, // Purple
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Подарки",
                    type = "INCOME",
                    iconName = "card_giftcard",
                    colorHex = 0xFFEC4899, // Pink
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Другой доход",
                    type = "INCOME",
                    iconName = "add_circle",
                    colorHex = 0xFF84CC16, // Lime
                    isDefault = true
                )
            )

            // Expense Categories requested specifically by the user:
            // "вредные привычки , магазин , обед , долг" + essential daily categories
            val expenseCategories = listOf(
                CategoryEntity(
                    name = "Вредные привычки",
                    type = "EXPENSE",
                    iconName = "smoking",
                    colorHex = 0xFFF59E0B, // Amber
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Магазин",
                    type = "EXPENSE",
                    iconName = "shopping_cart",
                    colorHex = 0xFFF97316, // Orange
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Обед",
                    type = "EXPENSE",
                    iconName = "restaurant",
                    colorHex = 0xFFEF4444, // Red
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Долг",
                    type = "EXPENSE",
                    iconName = "money_off",
                    colorHex = 0xFFE11D48, // Crimson Rose
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Транспорт",
                    type = "EXPENSE",
                    iconName = "directions_car",
                    colorHex = 0xFF6366F1, // Indigo
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Жилье и ЖКХ",
                    type = "EXPENSE",
                    iconName = "home",
                    colorHex = 0xFF0D9488, // Dark Teal
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Здоровье и Аптека",
                    type = "EXPENSE",
                    iconName = "local_hospital",
                    colorHex = 0xFFD946EF, // Fuchsia
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Развлечения",
                    type = "EXPENSE",
                    iconName = "sports_esports",
                    colorHex = 0xFFA855F7, // Purple
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Одежда",
                    type = "EXPENSE",
                    iconName = "checkroom",
                    colorHex = 0xFF0284C7, // Sky Blue
                    isDefault = true
                ),
                CategoryEntity(
                    name = "Другой расход",
                    type = "EXPENSE",
                    iconName = "remove_circle",
                    colorHex = 0xFF64748B, // Slate Gray
                    isDefault = true
                )
            )

            categoryDao.insertCategories(incomeCategories + expenseCategories)

            // Seed a few realistic sample transactions so the user immediately sees charts and lists working!
            val now = System.currentTimeMillis()
            val day = 86_400_000L

            val sampleTransactions = listOf(
                TransactionEntity(
                    amount = 85000.0,
                    type = "INCOME",
                    categoryId = 1,
                    categoryName = "Зарплата",
                    categoryIcon = "work",
                    categoryColor = 0xFF10B981,
                    note = "Основная зарплата за месяц",
                    timestamp = now - (day * 3)
                ),
                TransactionEntity(
                    amount = 5000.0,
                    type = "INCOME",
                    categoryId = 2,
                    categoryName = "Перевод от родных и близких",
                    categoryIcon = "family",
                    categoryColor = 0xFF06B6D4,
                    note = "Подарок на день рождения",
                    timestamp = now - (day * 1)
                ),
                TransactionEntity(
                    amount = 3200.0,
                    type = "EXPENSE",
                    categoryId = 8,
                    categoryName = "Магазин",
                    categoryIcon = "shopping_cart",
                    categoryColor = 0xFFF97316,
                    note = "Продукты на неделю",
                    timestamp = now - (day * 2)
                ),
                TransactionEntity(
                    amount = 650.0,
                    type = "EXPENSE",
                    categoryId = 9,
                    categoryName = "Обед",
                    categoryIcon = "restaurant",
                    categoryColor = 0xFFEF4444,
                    note = "Бизнес-ланч с коллегами",
                    timestamp = now - (day * 1)
                ),
                TransactionEntity(
                    amount = 450.0,
                    type = "EXPENSE",
                    categoryId = 7,
                    categoryName = "Вредные привычки",
                    categoryIcon = "smoking",
                    categoryColor = 0xFFF59E0B,
                    note = "Кофе и перекус",
                    timestamp = now - (3600_000L * 4)
                ),
                TransactionEntity(
                    amount = 5000.0,
                    type = "EXPENSE",
                    categoryId = 10,
                    categoryName = "Долг",
                    categoryIcon = "money_off",
                    categoryColor = 0xFFE11D48,
                    note = "Возврат долга по расписке",
                    timestamp = now - (3600_000L * 2)
                )
            )

            transactionDao.insertTransactions(sampleTransactions)
        }
    }
}
