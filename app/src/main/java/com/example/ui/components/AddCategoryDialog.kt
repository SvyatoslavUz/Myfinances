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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.TransactionType
import com.example.ui.icons.CategoryIcons
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.util.AppLanguage
import com.example.ui.util.AppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddCategoryDialog(
    initialType: TransactionType = TransactionType.EXPENSE,
    language: AppLanguage = AppLanguage.RU,
    onDismiss: () -> Unit,
    onSave: (name: String, type: TransactionType, iconName: String, colorHex: Long) -> Unit
) {
    val strings = AppStrings.get(language)
    var categoryName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(initialType) }
    var selectedIconKey by remember { mutableStateOf(CategoryIcons.availableIcons.first().key) }
    var selectedColorHex by remember { mutableStateOf(CategoryColors.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.newCategoryTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.close,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isExpense = selectedType == TransactionType.EXPENSE
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedType = TransactionType.EXPENSE }
                            .testTag("cat_dialog_type_expense"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpense) ExpenseRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = if (isExpense) androidx.compose.foundation.BorderStroke(2.dp, ExpenseRed) else null
                    ) {
                        Text(
                            text = strings.expenseTab,
                            fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium,
                            color = if (isExpense) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    val isIncome = selectedType == TransactionType.INCOME
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedType = TransactionType.INCOME }
                            .testTag("cat_dialog_type_income"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isIncome) IncomeGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = if (isIncome) androidx.compose.foundation.BorderStroke(2.dp, IncomeGreen) else null
                    ) {
                        Text(
                            text = strings.incomeTab,
                            fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                            color = if (isIncome) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name field
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cat_dialog_name_input"),
                    label = { Text(strings.categoryNamePlaceholder) },
                    placeholder = { Text("...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color Selection
                Text(
                    text = strings.chooseColor,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryColors.forEach { colorHex ->
                        val isSelected = selectedColorHex == colorHex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(colorHex), shape = CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = colorHex }
                                .testTag("color_picker_${colorHex}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Icon Selection
                Text(
                    text = strings.chooseIcon,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryIcons.availableIcons.forEach { iconOption ->
                        val isSelected = selectedIconKey == iconOption.key
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(selectedColorHex).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(selectedColorHex)) else null,
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { selectedIconKey = iconOption.key }
                                .testTag("icon_picker_${iconOption.key}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = iconOption.icon,
                                    contentDescription = iconOption.name,
                                    tint = if (isSelected) Color(selectedColorHex) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onSave(categoryName.trim(), selectedType, selectedIconKey, selectedColorHex)
                        onDismiss()
                    }
                },
                enabled = categoryName.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("cat_dialog_save_button")
            ) {
                Text(strings.save, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
