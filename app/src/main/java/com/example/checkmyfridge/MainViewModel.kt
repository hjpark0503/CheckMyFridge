package com.example.checkmyfridge

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _showSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean> = _showSplash.asStateFlow()

    // 월별 제철 식재료: e.g. "4월" -> ["멍게", "주꾸미", ...]
    private val _seasonalFoods = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val seasonalFoods: StateFlow<Map<String, List<String>>> = _seasonalFoods.asStateFlow()

    // 카테고리별 식재료 목록: e.g. "채소류" -> ["상추", "시금치", ...]
    private val _categories = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val categories: StateFlow<Map<String, List<String>>> = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            val context = getApplication<Application>()
            _seasonalFoods.value = parseJsonAsset(context, "제철.json")
            _categories.value = parseJsonAsset(context, "카테고리.json")
        }
    }

    fun onSplashFinished() {
        _showSplash.value = false
    }

    private suspend fun parseJsonAsset(context: Context, fileName: String): Map<String, List<String>> =
        withContext(Dispatchers.IO) {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            buildMap {
                for (key in jsonObject.keys()) {
                    val array = jsonObject.getJSONArray(key)
                    put(key, List(array.length()) { i -> array.getString(i) })
                }
            }
        }

    /** 현재 월(1~12)의 제철 식재료 목록 반환 */
    fun currentMonthSeasonalFoods(): List<String> {
        val month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        return _seasonalFoods.value["${month}월"] ?: emptyList()
    }

    /** 모든 카테고리 이름 목록 반환 */
    fun categoryNames(): List<String> = _categories.value.keys.toList()

    /** 특정 카테고리의 식재료 목록 반환 */
    fun foodsInCategory(category: String): List<String> =
        _categories.value[category] ?: emptyList()
}
