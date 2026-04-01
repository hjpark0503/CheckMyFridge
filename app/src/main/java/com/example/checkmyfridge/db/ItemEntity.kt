package com.example.checkmyfridge.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val addedDate: Long,
    val expirationDate: Long,
    val category: String,
    val subCategory: String,
    val count: Int,
)
