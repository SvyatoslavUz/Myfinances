package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.TransactionType
import com.example.ui.components.AddCategoryDialog
import com.example.ui.components.AddTransactionSheet
import com.example.ui.components.SettingsSheet
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.util.AppLanguage
import com.example.ui.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch

enum class AppDestination(
    val titleRu: String,
    val titleEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Главная", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    STATISTICS("Статистика", "Statistics", Icons.Filled.PieChart, Icons.Outlined.PieChart),
    HISTORY("История", "History", Icons.Filled.History, Icons.Outlined.History),
    CATEGORIES("Категории", "Categories", Icons.Filled.Category, Icons.Outlined.Category);

    fun getTitle(language: AppLanguage): String = if (language == AppLanguage.EN) titleEn else titleRu
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ExpenseApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseApp(
    viewModel: ExpenseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = uiState.strings
    val scope = rememberCoroutineScope()

    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }

    // Add transaction sheet state
    var showAddTransactionSheet by remember { mutableStateOf(false) }
    var addTransactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Add category dialog state
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var addCategoryType by remember { mutableStateOf(TransactionType.EXPENSE) }

    // Settings sheet state
    var showSettingsSheet by remember { mutableStateOf(false) }
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF211F26),
                tonalElevation = 8.dp
            ) {
                AppDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    val label = destination.getTitle(uiState.selectedLanguage)
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1D192B),
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = Color(0xFFE8DEF8),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_${destination.name.lowercase()}")
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    addTransactionType = TransactionType.EXPENSE
                    showAddTransactionSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(18.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier
                    .size(56.dp)
                    .testTag("main_add_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = strings.newOperation,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppDestination.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        onPeriodSelected = { viewModel.setPeriod(it) },
                        onAddTransaction = { type ->
                            addTransactionType = type
                            showAddTransactionSheet = true
                        },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) },
                        onNavigateToHistory = { currentDestination = AppDestination.HISTORY },
                        onNavigateToStatistics = { currentDestination = AppDestination.STATISTICS },
                        onOpenSettings = { showSettingsSheet = true }
                    )
                }
                AppDestination.STATISTICS -> {
                    StatisticsScreen(
                        uiState = uiState,
                        onPeriodSelected = { viewModel.setPeriod(it) },
                        onStatisticsTypeChanged = { viewModel.setStatisticsType(it) },
                        onCategorySelected = { viewModel.selectCategoryStat(it) },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) },
                        onOpenSettings = { showSettingsSheet = true }
                    )
                }
                AppDestination.HISTORY -> {
                    HistoryScreen(
                        uiState = uiState,
                        onSearchQueryChanged = { viewModel.setHistorySearchQuery(it) },
                        onTypeFilterChanged = { viewModel.setHistoryTypeFilter(it) },
                        onCategoryFilterChanged = { viewModel.setHistoryCategoryFilterId(it) },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) },
                        onOpenSettings = { showSettingsSheet = true }
                    )
                }
                AppDestination.CATEGORIES -> {
                    CategoriesScreen(
                        uiState = uiState,
                        onAddCategoryClick = { type ->
                            addCategoryType = type
                            showAddCategoryDialog = true
                        },
                        onDeleteCategory = { viewModel.deleteCategory(it) },
                        onOpenSettings = { showSettingsSheet = true }
                    )
                }
            }
        }

        // Add Transaction Modal Bottom Sheet
        if (showAddTransactionSheet) {
            AddTransactionSheet(
                sheetState = sheetState,
                categories = uiState.categories,
                initialType = addTransactionType,
                currency = uiState.selectedCurrency,
                language = uiState.selectedLanguage,
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        showAddTransactionSheet = false
                    }
                },
                onSave = { amount, type, category, note, timestamp ->
                    viewModel.addTransaction(amount, type, category, note, timestamp)
                },
                onOpenAddCategory = { type ->
                    addCategoryType = type
                    showAddCategoryDialog = true
                }
            )
        }

        // Add Custom Category Dialog
        if (showAddCategoryDialog) {
            AddCategoryDialog(
                initialType = addCategoryType,
                language = uiState.selectedLanguage,
                onDismiss = { showAddCategoryDialog = false },
                onSave = { name, type, iconName, colorHex ->
                    viewModel.addCategory(name, type, iconName, colorHex)
                }
            )
        }

        // Settings Sheet
        if (showSettingsSheet) {
            SettingsSheet(
                sheetState = settingsSheetState,
                currentCurrency = uiState.selectedCurrency,
                currentLanguage = uiState.selectedLanguage,
                onCurrencySelected = { currency ->
                    viewModel.setCurrency(currency)
                },
                onLanguageSelected = { language ->
                    viewModel.setLanguage(language)
                },
                onDismiss = {
                    scope.launch {
                        settingsSheetState.hide()
                        showSettingsSheet = false
                    }
                }
            )
        }
    }
}
