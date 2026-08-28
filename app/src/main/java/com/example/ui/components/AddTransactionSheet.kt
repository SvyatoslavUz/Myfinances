package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionType
import com.example.ui.icons.CategoryIcons
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.util.AppCurrency
import com.example.ui.util.AppLanguage
import com.example.ui.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionSheet(
    sheetState: SheetState,
    categories: List<CategoryEntity>,
    initialType: TransactionType = TransactionType.EXPENSE,
    currency: AppCurrency = AppCurrency.RUB,
    language: AppLanguage = AppLanguage.RU,
    onDismiss: () -> Unit,
    onSave: (amount: Double, type: TransactionType, category: CategoryEntity, note: String, timestamp: Long) -> Unit,
    onOpenAddCategory: (TransactionType) -> Unit
) {
    val strings = AppStrings.get(language)
    var selectedType by remember { mutableStateOf(initialType) }
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    val filteredCategories = remember(categories, selectedType) {
        categories.filter { it.type == selectedType.name }
    }

    var selectedCategory by remember(filteredCategories) {
        mutableStateOf(filteredCategories.firstOrNull())
    }

    LaunchedEffect(selectedType, categories) {
        if (selectedCategory == null || selectedCategory?.type != selectedType.name) {
            selectedCategory = filteredCategories.firstOrNull()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Title and Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedType == TransactionType.EXPENSE) strings.addExpense else strings.addIncome,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.close,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Type Selector: Expense vs Income
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Expense button
                val isExpense = selectedType == TransactionType.EXPENSE
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedType = TransactionType.EXPENSE }
                        .testTag("type_toggle_expense"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpense) ExpenseRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = if (isExpense) androidx.compose.foundation.BorderStroke(2.dp, ExpenseRed) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
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

                // Income button
                val isIncome = selectedType == TransactionType.INCOME
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedType = TransactionType.INCOME }
                        .testTag("type_toggle_income"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isIncome) IncomeGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = if (isIncome) androidx.compose.foundation.BorderStroke(2.dp, IncomeGreen) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
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

            Spacer(modifier = Modifier.height(18.dp))

            // Amount Input with currency symbol
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    val sanitized = input.replace(',', '.')
                    if (sanitized.isEmpty() || sanitized.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        amountText = input
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("amount_input_field"),
                label = { Text(strings.amountPlaceholder) },
                placeholder = { Text("0") },
                trailingIcon = {
                    Text(
                        text = currency.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick add amount chips scaled appropriately for currency
            val quickSums = when (currency) {
                AppCurrency.USD, AppCurrency.EUR -> listOf(5, 10, 50, 100)
                AppCurrency.CNY -> listOf(20, 50, 100, 500)
                AppCurrency.KZT -> listOf(500, 1000, 5000, 10000)
                AppCurrency.UZS -> listOf(10000, 50000, 100000, 500000)
                AppCurrency.RUB -> listOf(100, 500, 1000, 5000)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickSums.forEach { quickSum ->
                    val labelText = if (currency.isPrefix) "+${currency.symbol}$quickSum" else "+$quickSum ${currency.symbol}"
                    SuggestionChip(
                        onClick = {
                            val current = amountText.replace(',', '.').toDoubleOrNull() ?: 0.0
                            val newSum = current + quickSum
                            amountText = if (newSum % 1.0 == 0.0) newSum.toInt().toString() else String.format("%.2f", newSum)
                        },
                        label = { Text(labelText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        border = null,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Category Selection Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.selectCategory,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "+ ${strings.newCategoryTitle}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onOpenAddCategory(selectedType) }
                        .padding(4.dp)
                        .testTag("create_category_button")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips Grid
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredCategories.forEach { category ->
                    val isSelected = selectedCategory?.id == category.id
                    val catColor = Color(category.colorHex)
                    val catDisplayName = AppStrings.translateCategory(category.name, language)

                    Card(
                        modifier = Modifier
                            .clickable { selectedCategory = category }
                            .testTag("category_select_${category.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) catColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, catColor) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(catColor.copy(alpha = 0.2f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CategoryIcons.getIcon(category.iconName),
                                    contentDescription = null,
                                    tint = catColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = catDisplayName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Note Input (Optional)
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_input_field"),
                label = { Text(strings.notePlaceholder) },
                placeholder = { Text("...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            val amountVal = amountText.replace(',', '.').toDoubleOrNull()
            val canSave = amountVal != null && amountVal > 0.0 && selectedCategory != null

            Button(
                onClick = {
                    if (canSave && selectedCategory != null && amountVal != null) {
                        onSave(
                            amountVal,
                            selectedType,
                            selectedCategory!!,
                            noteText,
                            System.currentTimeMillis()
                        )
                        onDismiss()
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_transaction_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == TransactionType.EXPENSE) ExpenseRed else IncomeGreen,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.save,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
