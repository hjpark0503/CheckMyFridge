package com.example.checkmyfridge

import java.util.Date
import java.util.concurrent.TimeUnit

data class Item(
    val name: String,
    val addedDate: Date,
    val expirationDate: Date,
    val category: String
) {
    val dDay: Long
        get() {
            val diff = expirationDate.time - Date().time
            return TimeUnit.MILLISECONDS.toDays(diff)
        }
}
