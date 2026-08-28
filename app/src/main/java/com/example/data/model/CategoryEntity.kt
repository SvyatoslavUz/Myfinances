package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "EXPENSE" or "INCOME"
    val iconName: String,
    val colorHex: Long,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
