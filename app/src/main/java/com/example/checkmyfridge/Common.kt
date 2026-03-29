package com.example.checkmyfridge

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.checkmyfridge.db.AppDatabase
import com.example.checkmyfridge.db.ItemDao
import com.example.checkmyfridge.ui.theme.green
import com.example.checkmyfridge.ui.theme.lightRed
import com.example.checkmyfridge.ui.theme.yellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Common {

    // 날짜 포맷
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatDate(timeMillis: Long): String = dateFormat.format(Date(timeMillis))

    // 오늘 날짜 밀리초
    fun today(): Long = Date().time

    // N일 뒤 날짜 밀리초
    fun daysLater(days: Int): Long = today() + days * 24 * 60 * 60 * 1000L

    // DB DAO
    fun getDao(context: Context): ItemDao =
        AppDatabase.getInstance(context).itemDao()


    fun getRestOfDayColor(restOfDay: Int): Color {
        if(restOfDay < 0){
            return lightRed
        }else if(restOfDay < 3){
            return yellow
        }else{
            return green
        }
    }
}
