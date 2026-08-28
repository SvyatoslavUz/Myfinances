package com.example.data.repository

import com.example.data.db.CategoryDao
import com.example.data.db.TransactionDao
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) {
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>> =
        categoryDao.getCategoriesByType(type)

    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsBetween(startTime, endTime)

    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByType(type)

    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByCategory(categoryId)

    suspend fun insertCategory(category: CategoryEntity): Long =
        categoryDao.insertCategory(category)

    suspend fun updateCategory(category: CategoryEntity) =
        categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) =
        categoryDao.deleteCategory(category)

    suspend fun deleteCategoryById(id: Long) =
        categoryDao.deleteCategoryById(id)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        transactionDao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) =
        transactionDao.deleteTransactionById(id)

    suspend fun checkAndSeedDefaults() {
        if (categoryDao.getCategoryCount() == 0) {
            com.example.data.db.AppDatabase.populateDefaultData(categoryDao, transactionDao)
        }
    }
}
