package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.repository.ExpenseRepository
import com.example.ui.util.AppCurrency
import com.example.ui.util.AppLanguage
import com.example.ui.util.AppStrings
import com.example.ui.util.Strings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class PeriodFilter {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    ALL_TIME;

    fun getTitle(language: AppLanguage): String {
        val strings = AppStrings.get(language)
        return when (this) {
            TODAY -> strings.periodToday
            THIS_WEEK -> strings.periodThisWeek
            THIS_MONTH -> strings.periodThisMonth
            THIS_YEAR -> strings.periodThisYear
            ALL_TIME -> strings.periodAllTime
        }
    }
}

data class CategoryStat(
    val categoryId: Long,
    val categoryName: String,
    val iconName: String,
    val color: Long,
    val totalAmount: Double,
    val percentage: Float,
    val count: Int
)

data class ExpenseUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val expenseCategories: List<CategoryEntity> = emptyList(),
    val incomeCategories: List<CategoryEntity> = emptyList(),
    val selectedPeriod: PeriodFilter = PeriodFilter.THIS_MONTH,
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val expenseStats: List<CategoryStat> = emptyList(),
    val incomeStats: List<CategoryStat> = emptyList(),
    val selectedCategoryStat: CategoryStat? = null,
    val statisticsType: TransactionType = TransactionType.EXPENSE,
    val historySearchQuery: String = "",
    val historyTypeFilter: TransactionType? = null,
    val historyCategoryFilterId: Long? = null,
    val selectedCurrency: AppCurrency = AppCurrency.RUB,
    val selectedLanguage: AppLanguage = AppLanguage.RU
) {
    val strings: Strings get() = AppStrings.get(selectedLanguage)
}

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExpenseRepository
    private val prefs = application.getSharedPreferences("expense_prefs", Context.MODE_PRIVATE)

    private val _selectedPeriod = MutableStateFlow(PeriodFilter.THIS_MONTH)
    val selectedPeriod: StateFlow<PeriodFilter> = _selectedPeriod.asStateFlow()

    private val _statisticsType = MutableStateFlow(TransactionType.EXPENSE)
    val statisticsType: StateFlow<TransactionType> = _statisticsType.asStateFlow()

    private val _selectedCategoryStat = MutableStateFlow<CategoryStat?>(null)
    val selectedCategoryStat: StateFlow<CategoryStat?> = _selectedCategoryStat.asStateFlow()

    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery.asStateFlow()

    private val _historyTypeFilter = MutableStateFlow<TransactionType?>(null)
    val historyTypeFilter: StateFlow<TransactionType?> = _historyTypeFilter.asStateFlow()

    private val _historyCategoryFilterId = MutableStateFlow<Long?>(null)
    val historyCategoryFilterId: StateFlow<Long?> = _historyCategoryFilterId.asStateFlow()

    private val _selectedCurrency = MutableStateFlow(
        run {
            val code = prefs.getString("selected_currency", AppCurrency.RUB.code) ?: AppCurrency.RUB.code
            AppCurrency.entries.firstOrNull { it.code == code } ?: AppCurrency.RUB
        }
    )
    val selectedCurrency: StateFlow<AppCurrency> = _selectedCurrency.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(
        run {
            val code = prefs.getString("selected_language", AppLanguage.RU.code) ?: AppLanguage.RU.code
            AppLanguage.entries.firstOrNull { it.code == code } ?: AppLanguage.RU
        }
    )
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = ExpenseRepository(database.categoryDao(), database.transactionDao())

        viewModelScope.launch(Dispatchers.IO) {
            repository.checkAndSeedDefaults()
        }
    }

    val uiState: StateFlow<ExpenseUiState> = combine(
        repository.allTransactions,
        repository.allCategories,
        _selectedPeriod,
        _statisticsType,
        _selectedCategoryStat,
        _historySearchQuery,
        _historyTypeFilter,
        _historyCategoryFilterId,
        _selectedCurrency,
        _selectedLanguage
    ) { params: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val allTx = params[0] as List<TransactionEntity>
        @Suppress("UNCHECKED_CAST")
        val allCats = params[1] as List<CategoryEntity>
        val period = params[2] as PeriodFilter
        val statsType = params[3] as TransactionType
        val selectedStat = params[4] as? CategoryStat
        val searchQuery = params[5] as String
        val historyType = params[6] as? TransactionType
        val historyCatId = params[7] as? Long
        val currency = params[8] as AppCurrency
        val language = params[9] as AppLanguage

        val expenseCats = allCats.filter { it.type == TransactionType.EXPENSE.name }
        val incomeCats = allCats.filter { it.type == TransactionType.INCOME.name }

        // Filter transactions for current selected period
        val periodFilteredTx = filterTransactionsByPeriod(allTx, period)

        var totalInc = 0.0
        var totalExp = 0.0

        for (tx in periodFilteredTx) {
            if (tx.type == TransactionType.INCOME.name) {
                totalInc += tx.amount
            } else {
                totalExp += tx.amount
            }
        }

        // Calculate all-time net balance
        var lifetimeBalance = 0.0
        for (tx in allTx) {
            if (tx.type == TransactionType.INCOME.name) {
                lifetimeBalance += tx.amount
            } else {
                lifetimeBalance -= tx.amount
            }
        }

        val expenseStats = calculateCategoryStats(
            periodFilteredTx.filter { it.type == TransactionType.EXPENSE.name },
            totalExp
        )

        val incomeStats = calculateCategoryStats(
            periodFilteredTx.filter { it.type == TransactionType.INCOME.name },
            totalInc
        )

        // Filter history transactions by search and filter parameters
        val historyFilteredTx = allTx.filter { tx ->
            val matchesType = historyType == null || tx.type == historyType.name
            val matchesCat = historyCatId == null || tx.categoryId == historyCatId
            val matchesQuery = searchQuery.isBlank() ||
                    tx.categoryName.contains(searchQuery, ignoreCase = true) ||
                    tx.note.contains(searchQuery, ignoreCase = true)
            matchesType && matchesCat && matchesQuery
        }

        ExpenseUiState(
            transactions = allTx,
            filteredTransactions = historyFilteredTx,
            categories = allCats,
            expenseCategories = expenseCats,
            incomeCategories = incomeCats,
            selectedPeriod = period,
            totalBalance = lifetimeBalance,
            totalIncome = totalInc,
            totalExpense = totalExp,
            expenseStats = expenseStats,
            incomeStats = incomeStats,
            selectedCategoryStat = selectedStat,
            statisticsType = statsType,
            historySearchQuery = searchQuery,
            historyTypeFilter = historyType,
            historyCategoryFilterId = historyCatId,
            selectedCurrency = currency,
            selectedLanguage = language
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpenseUiState()
    )

    fun setPeriod(period: PeriodFilter) {
        _selectedPeriod.value = period
        _selectedCategoryStat.value = null
    }

    fun setStatisticsType(type: TransactionType) {
        _statisticsType.value = type
        _selectedCategoryStat.value = null
    }

    fun selectCategoryStat(stat: CategoryStat?) {
        _selectedCategoryStat.value = stat
    }

    fun setHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun setHistoryTypeFilter(type: TransactionType?) {
        _historyTypeFilter.value = type
    }

    fun setHistoryCategoryFilterId(categoryId: Long?) {
        _historyCategoryFilterId.value = categoryId
    }

    fun setCurrency(currency: AppCurrency) {
        _selectedCurrency.value = currency
        prefs.edit().putString("selected_currency", currency.code).apply()
    }

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        prefs.edit().putString("selected_language", language.code).apply()
    }

    fun addTransaction(
        amount: Double,
        type: TransactionType,
        category: CategoryEntity,
        note: String,
        timestamp: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val transaction = TransactionEntity(
                amount = amount,
                type = type.name,
                categoryId = category.id,
                categoryName = category.name,
                categoryIcon = category.iconName,
                categoryColor = category.colorHex,
                note = note.trim(),
                timestamp = timestamp
            )
            repository.insertTransaction(transaction)
        }
    }

    fun updateTransaction(
        id: Long,
        amount: Double,
        type: TransactionType,
        category: CategoryEntity,
        note: String,
        timestamp: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val transaction = TransactionEntity(
                id = id,
                amount = amount,
                type = type.name,
                categoryId = category.id,
                categoryName = category.name,
                categoryIcon = category.iconName,
                categoryColor = category.colorHex,
                note = note.trim(),
                timestamp = timestamp
            )
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(transaction)
        }
    }

    fun addCategory(
        name: String,
        type: TransactionType,
        iconName: String,
        colorHex: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val category = CategoryEntity(
                name = name.trim(),
                type = type.name,
                iconName = iconName,
                colorHex = colorHex,
                isDefault = false
            )
            repository.insertCategory(category)
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
        }
    }

    private fun filterTransactionsByPeriod(
        transactions: List<TransactionEntity>,
        period: PeriodFilter
    ): List<TransactionEntity> {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        return when (period) {
            PeriodFilter.ALL_TIME -> transactions
            PeriodFilter.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.timeInMillis
                transactions.filter { it.timestamp in startOfDay..now }
            }
            PeriodFilter.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfWeek = calendar.timeInMillis
                transactions.filter { it.timestamp in startOfWeek..now }
            }
            PeriodFilter.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfMonth = calendar.timeInMillis
                transactions.filter { it.timestamp in startOfMonth..now }
            }
            PeriodFilter.THIS_YEAR -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfYear = calendar.timeInMillis
                transactions.filter { it.timestamp in startOfYear..now }
            }
        }
    }

    private fun calculateCategoryStats(
        transactions: List<TransactionEntity>,
        totalAmount: Double
    ): List<CategoryStat> {
        if (transactions.isEmpty() || totalAmount <= 0.0) return emptyList()

        val grouped = transactions.groupBy { it.categoryId }
        return grouped.map { (_, txList) ->
            val first = txList.first()
            val categorySum = txList.sumOf { it.amount }
            val percentage = ((categorySum / totalAmount) * 100.0).toFloat()
            CategoryStat(
                categoryId = first.categoryId,
                categoryName = first.categoryName,
                iconName = first.categoryIcon,
                color = first.categoryColor,
                totalAmount = categorySum,
                percentage = percentage,
                count = txList.size
            )
        }.sortedByDescending { it.totalAmount }
    }
}
