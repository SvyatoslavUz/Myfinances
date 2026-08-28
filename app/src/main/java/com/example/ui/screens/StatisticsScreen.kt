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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.components.InteractivePieChart
import com.example.ui.components.TransactionItem
import com.example.ui.icons.CategoryIcons
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.util.AppStrings
import com.example.ui.util.Formatters
import com.example.ui.viewmodel.CategoryStat
import com.example.ui.viewmodel.ExpenseUiState
import com.example.ui.viewmodel.PeriodFilter

@Composable
fun StatisticsScreen(
    uiState: ExpenseUiState,
    onPeriodSelected: (PeriodFilter) -> Unit,
    onStatisticsTypeChanged: (TransactionType) -> Unit,
    onCategorySelected: (CategoryStat?) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = uiState.strings
    val isExpense = uiState.statisticsType == TransactionType.EXPENSE
    val currentStats = if (isExpense) uiState.expenseStats else uiState.incomeStats
    val totalAmount = if (isExpense) uiState.totalExpense else uiState.totalIncome

    // Filter transactions for category drill-down
    val selectedCategoryTx = if (uiState.selectedCategoryStat != null) {
        uiState.transactions.filter {
            it.categoryId == uiState.selectedCategoryStat.categoryId &&
            it.type == uiState.statisticsType.name
        }
    } else emptyList()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header with Settings button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.statisticsTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = strings.statisticsSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(42.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .testTag("stats_settings_button")
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

        // Period Filters
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PeriodFilter.entries.forEach { period ->
                    val isSelected = uiState.selectedPeriod == period
                    FilterChip(
                        selected = isSelected,
                        onClick = { onPeriodSelected(period) },
                        label = {
                            Text(
                                text = period.getTitle(uiState.selectedLanguage),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Expense / Income Segmented Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Expenses Tab
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onStatisticsTypeChanged(TransactionType.EXPENSE) }
                        .testTag("stats_type_expense_tab"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpense) ExpenseRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = if (isExpense) androidx.compose.foundation.BorderStroke(2.dp, ExpenseRed) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isExpense) ExpenseRed else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.expenseTab,
                            fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium,
                            color = if (isExpense) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Incomes Tab
                val isIncome = uiState.statisticsType == TransactionType.INCOME
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onStatisticsTypeChanged(TransactionType.INCOME) }
                        .testTag("stats_type_income_tab"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isIncome) IncomeGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = if (isIncome) androidx.compose.foundation.BorderStroke(2.dp, IncomeGreen) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isIncome) IncomeGreen else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.incomeTab,
                            fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                            color = if (isIncome) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Circular Donut / Pie Chart Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pie_chart_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                InteractivePieChart(
                    stats = currentStats,
                    totalAmount = totalAmount,
                    title = if (isExpense) strings.expensesByCategory else strings.incomeByCategory,
                    selectedCategoryStat = uiState.selectedCategoryStat,
                    onCategorySelected = onCategorySelected,
                    currency = uiState.selectedCurrency,
                    language = uiState.selectedLanguage
                )
            }
        }

        // Category Breakdown Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpense) strings.expensesByCategory else strings.incomeByCategory,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${currentStats.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Detailed Category Cards with Progress Bars
        if (currentStats.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = strings.noStatsData,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        } else {
            items(currentStats, key = { it.categoryId }) { stat ->
                val isSelected = uiState.selectedCategoryStat?.categoryId == stat.categoryId
                val catColor = Color(stat.color)
                val catName = AppStrings.translateCategory(stat.categoryName, uiState.selectedLanguage)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) onCategorySelected(null) else onCategorySelected(stat)
                        }
                        .testTag("stat_category_card_${stat.categoryId}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) catColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, catColor) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(catColor.copy(alpha = 0.18f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CategoryIcons.getIcon(stat.iconName),
                                    contentDescription = null,
                                    tint = catColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = catName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${stat.count} ${strings.operationsCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = Formatters.formatCurrency(
                                        amount = stat.totalAmount,
                                        currency = uiState.selectedCurrency,
                                        language = uiState.selectedLanguage
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExpense) ExpenseRed else IncomeGreen
                                )
                                Text(
                                    text = String.format("%.1f%%", stat.percentage),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = catColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Percentage Progress Indicator
                        LinearProgressIndicator(
                            progress = { (stat.percentage / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = catColor,
                            trackColor = catColor.copy(alpha = 0.15f),
                            drawStopIndicator = {}
                        )
                    }
                }
            }
        }

        // Category Drill-Down Transactions
        if (uiState.selectedCategoryStat != null && selectedCategoryTx.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                val drillCatName = AppStrings.translateCategory(uiState.selectedCategoryStat.categoryName, uiState.selectedLanguage)
                Text(
                    text = "$drillCatName (${selectedCategoryTx.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(selectedCategoryTx, key = { "drill_${it.id}" }) { tx ->
                TransactionItem(
                    transaction = tx,
                    onDelete = onDeleteTransaction,
                    currency = uiState.selectedCurrency,
                    language = uiState.selectedLanguage
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
