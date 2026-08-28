package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.components.TransactionItem
import com.example.ui.icons.CategoryIcons
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.util.AppStrings
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.ExpenseUiState

@Composable
fun HistoryScreen(
    uiState: ExpenseUiState,
    onSearchQueryChanged: (String) -> Unit,
    onTypeFilterChanged: (TransactionType?) -> Unit,
    onCategoryFilterChanged: (Long?) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = uiState.strings

    // Group filtered transactions by date
    val groupedTransactions = uiState.filteredTransactions.groupBy {
        Formatters.formatDateGroup(it.timestamp, uiState.selectedLanguage)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header with Settings
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.historyTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = strings.historySubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(42.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .testTag("history_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = strings.settingsTitle,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Search bar
        item {
            OutlinedTextField(
                value = uiState.historySearchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_input"),
                placeholder = { Text(strings.searchPlaceholder) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.historySearchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = strings.clear)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Type filter chips (All / Expenses / Incomes)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All
                FilterChip(
                    selected = uiState.historyTypeFilter == null,
                    onClick = { onTypeFilterChanged(null) },
                    label = { Text(strings.allOperations) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("filter_chip_all")
                )

                // Expense
                FilterChip(
                    selected = uiState.historyTypeFilter == TransactionType.EXPENSE,
                    onClick = { onTypeFilterChanged(TransactionType.EXPENSE) },
                    label = { Text(strings.expenseLabel) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ExpenseRed,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("filter_chip_expense")
                )

                // Income
                FilterChip(
                    selected = uiState.historyTypeFilter == TransactionType.INCOME,
                    onClick = { onTypeFilterChanged(TransactionType.INCOME) },
                    label = { Text(strings.incomeLabel) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IncomeGreen,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("filter_chip_income")
                )
            }
        }

        // Category Horizontal Filter Bar
        item {
            val relevantCategories = if (uiState.historyTypeFilter != null) {
                uiState.categories.filter { it.type == uiState.historyTypeFilter.name }
            } else {
                uiState.categories
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All categories chip
                FilterChip(
                    selected = uiState.historyCategoryFilterId == null,
                    onClick = { onCategoryFilterChanged(null) },
                    label = { Text(strings.allCategories) },
                    shape = RoundedCornerShape(12.dp)
                )

                relevantCategories.forEach { category ->
                    val isSelected = uiState.historyCategoryFilterId == category.id
                    val catColor = Color(category.colorHex)
                    val catName = AppStrings.translateCategory(category.name, uiState.selectedLanguage)

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) onCategoryFilterChanged(null)
                            else onCategoryFilterChanged(category.id)
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(catColor, CircleShape)
                            )
                        },
                        label = { Text(catName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = catColor.copy(alpha = 0.2f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("filter_cat_${category.id}")
                    )
                }
            }
        }

        // Transactions Grouped by Date
        if (groupedTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = strings.noOperationsFound,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.tryChangingFilters,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            groupedTransactions.forEach { (dateGroup, transactions) ->
                // Date Header with Day Subtotal
                item(key = "header_$dateGroup") {
                    val dayIncome = transactions.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }
                    val dayExpense = transactions.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateGroup,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (dayIncome > 0) {
                                Text(
                                    text = "+${Formatters.formatCurrency(dayIncome, uiState.selectedCurrency, language = uiState.selectedLanguage)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = IncomeGreen
                                )
                            }
                            if (dayExpense > 0) {
                                Text(
                                    text = "-${Formatters.formatCurrency(dayExpense, uiState.selectedCurrency, language = uiState.selectedLanguage)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }
                        }
                    }
                }

                items(transactions, key = { it.id }) { tx ->
                    TransactionItem(
                        transaction = tx,
                        onDelete = onDeleteTransaction,
                        currency = uiState.selectedCurrency,
                        language = uiState.selectedLanguage
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
