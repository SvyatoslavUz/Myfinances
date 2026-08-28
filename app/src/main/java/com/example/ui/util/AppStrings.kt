package com.example.ui.util

object AppStrings {
    fun get(language: AppLanguage): Strings = if (language == AppLanguage.EN) EnglishStrings else RussianStrings

    // Helper to translate default category names
    fun translateCategory(name: String, language: AppLanguage): String {
        if (language == AppLanguage.RU) return name
        return when (name) {
            "Зарплата" -> "Salary"
            "Перевод от родных и близких" -> "Family Transfer"
            "Подработка" -> "Side Hustle"
            "Инвестиции" -> "Investments"
            "Подарки" -> "Gifts"
            "Другой доход" -> "Other Income"
            "Вредные привычки" -> "Bad Habits"
            "Магазин" -> "Groceries & Shop"
            "Обед" -> "Lunch & Food"
            "Долг" -> "Debt"
            "Транспорт" -> "Transport"
            "Жилье и ЖКХ" -> "Housing & Bills"
            "Здоровье и Аптека", "Здоровье" -> "Health & Pharmacy"
            "Развлечения" -> "Entertainment"
            "Одежда" -> "Clothing"
            "Другой расход" -> "Other Expense"
            else -> name
        }
    }
}

interface Strings {
    // App Navigation
    val navHome: String
    val navStatistics: String
    val navHistory: String
    val navCategories: String
    val settingsTitle: String

    // Greeting & Header
    val greetingHello: String
    val appTitle: String

    // Periods
    val periodToday: String
    val periodThisWeek: String
    val periodThisMonth: String
    val periodThisYear: String
    val periodAllTime: String

    // Balance & Totals
    val totalBalanceLabel: String
    val incomeLabel: String
    val expenseLabel: String
    val periodIncomeLabel: String
    val periodExpenseLabel: String

    // Action Buttons
    val addExpense: String
    val addIncome: String
    val addTransaction: String
    val editTransaction: String
    val save: String
    val cancel: String
    val delete: String
    val apply: String
    val close: String

    // Home Screen
    val expenseStructure: String
    val recentTransactions: String
    val seeAll: String
    val noTransactionsPeriod: String
    val addFirstOperation: String

    // Statistics Screen
    val statisticsTitle: String
    val statisticsSubtitle: String
    val expensesByCategory: String
    val incomeByCategory: String
    val categoryRanking: String
    val operationsCount: String
    val totalForPeriod: String
    val tapForDetails: String
    val noStatsData: String
    val addOperationsForPeriod: String

    // History Screen
    val historyTitle: String
    val historySubtitle: String
    val searchPlaceholder: String
    val clear: String
    val allOperations: String
    val allCategories: String
    val noOperationsFound: String
    val tryChangingFilters: String
    val today: String
    val yesterday: String

    // Categories Screen
    val categoriesTitle: String
    val categoriesSubtitle: String
    val expenseCategoriesTab: String
    val incomeCategoriesTab: String
    val addExpenseCategory: String
    val addIncomeCategory: String
    val newCategoryTitle: String
    val categoryNamePlaceholder: String
    val chooseIcon: String
    val chooseColor: String

    // Add Transaction Sheet
    val newOperation: String
    val expenseTab: String
    val incomeTab: String
    val amountPlaceholder: String
    val selectCategory: String
    val notePlaceholder: String
    val dateLabel: String
    val quickAdd: String
    val enterValidAmount: String
    val selectCategoryPrompt: String

    // Settings Modal
    val settingsHeading: String
    val languageSection: String
    val currencySection: String
    val selectedCurrency: String
    val selectedLanguage: String
}

object RussianStrings : Strings {
    override val navHome = "Главная"
    override val navStatistics = "Статистика"
    override val navHistory = "История"
    override val navCategories = "Категории"
    override val settingsTitle = "Настройки"

    override val greetingHello = "Финансовый контроль"
    override val appTitle = "Мои Финансы"

    override val periodToday = "Сегодня"
    override val periodThisWeek = "Эта неделя"
    override val periodThisMonth = "Этот месяц"
    override val periodThisYear = "Этот год"
    override val periodAllTime = "Все время"

    override val totalBalanceLabel = "ОБЩИЙ БАЛАНС"
    override val incomeLabel = "Доходы"
    override val expenseLabel = "Расходы"
    override val periodIncomeLabel = "ДОХОДЫ"
    override val periodExpenseLabel = "РАСХОДЫ"

    override val addExpense = "Расход"
    override val addIncome = "Доход"
    override val addTransaction = "Добавить операцию"
    override val editTransaction = "Редактировать"
    override val save = "Сохранить"
    override val cancel = "Отмена"
    override val delete = "Удалить"
    override val apply = "Применить"
    override val close = "Закрыть"

    override val expenseStructure = "Структура расходов"
    override val recentTransactions = "Последние операции"
    override val seeAll = "Все"
    override val noTransactionsPeriod = "Нет операций за этот период"
    override val addFirstOperation = "Нажмите + чтобы добавить доход или расход"

    override val statisticsTitle = "Статистика"
    override val statisticsSubtitle = "Наглядный анализ ваших расходов и доходов"
    override val expensesByCategory = "Расходы по категориям"
    override val incomeByCategory = "Доходы по категориям"
    override val categoryRanking = "Рейтинг категорий"
    override val operationsCount = "операций"
    override val totalForPeriod = "Всего"
    override val tapForDetails = "Нажмите для деталей"
    override val noStatsData = "Нет данных"
    override val addOperationsForPeriod = "Добавьте операции за этот период"

    override val historyTitle = "История операций"
    override val historySubtitle = "Все доходы и расходы с поиском и фильтрами"
    override val searchPlaceholder = "Поиск по названию или заметке..."
    override val clear = "Очистить"
    override val allOperations = "Все операции"
    override val allCategories = "Все категории"
    override val noOperationsFound = "Операции не найдены"
    override val tryChangingFilters = "Попробуйте изменить параметры поиска или фильтров"
    override val today = "Сегодня"
    override val yesterday = "Вчера"

    override val categoriesTitle = "Категории"
    override val categoriesSubtitle = "Управление категориями расходов и доходов"
    override val expenseCategoriesTab = "Расходы"
    override val incomeCategoriesTab = "Доходы"
    override val addExpenseCategory = "Добавить категорию расхода"
    override val addIncomeCategory = "Добавить категорию дохода"
    override val newCategoryTitle = "Новая категория"
    override val categoryNamePlaceholder = "Название категории..."
    override val chooseIcon = "Выберите иконку"
    override val chooseColor = "Выберите цвет"

    override val newOperation = "Новая операция"
    override val expenseTab = "Расход"
    override val incomeTab = "Доход"
    override val amountPlaceholder = "0"
    override val selectCategory = "Выберите категорию"
    override val notePlaceholder = "Комментарий / Заметка (необязательно)"
    override val dateLabel = "Дата и время"
    override val quickAdd = "Быстрый ввод"
    override val enterValidAmount = "Введите корректную сумму"
    override val selectCategoryPrompt = "Пожалуйста, выберите категорию"

    override val settingsHeading = "Настройки"
    override val languageSection = "Язык интерфейса / Language"
    override val currencySection = "Основная валюта / Currency"
    override val selectedCurrency = "Выбранная валюта"
    override val selectedLanguage = "Выбранный язык"
}

object EnglishStrings : Strings {
    override val navHome = "Home"
    override val navStatistics = "Analytics"
    override val navHistory = "History"
    override val navCategories = "Categories"
    override val settingsTitle = "Settings"

    override val greetingHello = "Financial Control"
    override val appTitle = "My Finances"

    override val periodToday = "Today"
    override val periodThisWeek = "This Week"
    override val periodThisMonth = "This Month"
    override val periodThisYear = "This Year"
    override val periodAllTime = "All Time"

    override val totalBalanceLabel = "NET BALANCE"
    override val incomeLabel = "Income"
    override val expenseLabel = "Expenses"
    override val periodIncomeLabel = "INCOME"
    override val periodExpenseLabel = "EXPENSES"

    override val addExpense = "Expense"
    override val addIncome = "Income"
    override val addTransaction = "Add Transaction"
    override val editTransaction = "Edit"
    override val save = "Save"
    override val cancel = "Cancel"
    override val delete = "Delete"
    override val apply = "Apply"
    override val close = "Close"

    override val expenseStructure = "Expense Breakdown"
    override val recentTransactions = "Recent Transactions"
    override val seeAll = "See All"
    override val noTransactionsPeriod = "No transactions in this period"
    override val addFirstOperation = "Tap + to add an income or expense"

    override val statisticsTitle = "Analytics"
    override val statisticsSubtitle = "Visual insights into your income and expenses"
    override val expensesByCategory = "Expenses by Category"
    override val incomeByCategory = "Income by Category"
    override val categoryRanking = "Category Ranking"
    override val operationsCount = "txns"
    override val totalForPeriod = "Total"
    override val tapForDetails = "Tap for details"
    override val noStatsData = "No Data"
    override val addOperationsForPeriod = "Add transactions for this period"

    override val historyTitle = "Transaction History"
    override val historySubtitle = "All records with instant search and filters"
    override val searchPlaceholder = "Search by category or note..."
    override val clear = "Clear"
    override val allOperations = "All Records"
    override val allCategories = "All Categories"
    override val noOperationsFound = "No transactions found"
    override val tryChangingFilters = "Try adjusting your search or filter options"
    override val today = "Today"
    override val yesterday = "Yesterday"

    override val categoriesTitle = "Categories"
    override val categoriesSubtitle = "Manage expense and income categories"
    override val expenseCategoriesTab = "Expenses"
    override val incomeCategoriesTab = "Income"
    override val addExpenseCategory = "Add Expense Category"
    override val addIncomeCategory = "Add Income Category"
    override val newCategoryTitle = "New Category"
    override val categoryNamePlaceholder = "Category name..."
    override val chooseIcon = "Select Icon"
    override val chooseColor = "Select Color"

    override val newOperation = "New Transaction"
    override val expenseTab = "Expense"
    override val incomeTab = "Income"
    override val amountPlaceholder = "0"
    override val selectCategory = "Select Category"
    override val notePlaceholder = "Note / Comment (optional)"
    override val dateLabel = "Date & Time"
    override val quickAdd = "Quick Add"
    override val enterValidAmount = "Please enter a valid amount"
    override val selectCategoryPrompt = "Please select a category"

    override val settingsHeading = "Settings"
    override val languageSection = "Language / Язык"
    override val currencySection = "Currency / Валюта"
    override val selectedCurrency = "Selected Currency"
    override val selectedLanguage = "Selected Language"
}
