package com.example.checkmyfridge

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkmyfridge.db.ItemEntity
import com.example.checkmyfridge.ui.theme.*
import kotlinx.coroutines.launch

class ListScreen {
    companion object {
        @SuppressLint("DefaultLocale")
        @Composable
        fun Content(
            modifier: Modifier = Modifier,
            categories: Map<String, List<String>> = emptyMap()
        ) {
            val context = LocalContext.current
            val dao = remember { Common.getDao(context) }
            val scope = rememberCoroutineScope()

            val storageCategories = listOf("냉동", "냉장", "실온")
            var selectedIndex by remember { mutableIntStateOf(0) }
            val currentCategory = storageCategories[selectedIndex]

            val items by dao.getItemsByCategory(currentCategory).collectAsState(initial = emptyList())

            var inputText by remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current

            // name을 보고 카테고리.json에서 서브카테고리 자동 탐색
            fun detectSubCategory(name: String): String {
                val trimmed = name.trim()
                if (trimmed.isEmpty()) return "기타"
                for ((categoryName, foodList) in categories) {
                    if (foodList.any { it.contains(trimmed) || trimmed.contains(it) }) {
                        return categoryName
                    }
                }
                return "기타"
            }

            fun addItem() {
                val trimmed = inputText.trim()
                if (trimmed.isNotEmpty()) {
                    scope.launch {
                        dao.insert(
                            ItemEntity(
                                name = trimmed,
                                addedDate = Common.today(),
                                expirationDate = Common.daysLater(7),
                                category = currentCategory,
                                subCategory = detectSubCategory(trimmed),
                                count = 1
                            )
                        )
                    }
                    inputText = ""
                    focusManager.clearFocus()
                }
            }

            @Composable
            fun textField() {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    placeholder = { Text("재료 입력") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        hintLocales = androidx.compose.ui.text.intl.LocaleList("ko")
                    ),
                    keyboardActions = KeyboardActions(onDone = { addItem() })
                )
            }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
            ) {
                val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

                // 상단 버튼 3개
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    storageCategories.forEachIndexed { index, label ->
                        Button(
                            onClick = { selectedIndex = index },
                            modifier = Modifier.width(90.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedIndex == index)
                                    MaterialTheme.colorScheme.tertiary
                                else
                                    MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    if(isLandscape){
                        // EditText
                        textField()
                    }
                }

                if(!isLandscape) {
                    // EditText
                    textField()
                }

                // 세로 리스트
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {

                    items(items) { item ->
                        var showDeleteDialog by remember { mutableStateOf(false) }

                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                title = { Text("재료 삭제", fontSize = 16.sp) },
                                text = { Text("'${item.name}'을(를) 삭제하시겠습니까?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        scope.launch { dao.deleteById(item.id) }
                                        showDeleteDialog = false
                                    }) {
                                        Text("삭제", color = red)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteDialog = false }) {
                                        Text("취소")
                                    }
                                }
                            )
                        }

                        Column{
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = { showDeleteDialog = true }
                                    )
                                    .padding(vertical = 8.dp, horizontal = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(color = Common.getRestOfDayColor(item.expirationDate), shape = CircleShape)
                                        .align(Alignment.CenterVertically))

                                Column (modifier = Modifier.weight(1f).align(Alignment.CenterVertically).padding(start = 18.dp)){
                                    Text(
                                        text = item.name,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val str = String.format("%s • %s • %d개", item.category, item.subCategory, item.count)
                                    Text(
                                        text = str,
                                        fontSize = 13.sp,
                                        color = if (themeIndex == 3) grey else darkGrey,
                                    )
                                }




                                Column(modifier = Modifier.weight(1f).align(Alignment.CenterVertically)) {
                                    Text(
                                        text = "추가 " + Common.formatDate(item.addedDate),
                                        fontSize = 11.sp,
                                        color = if(themeIndex==3) grey else darkGrey,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                    Text(
                                        text = "소비 " + Common.formatDate(item.expirationDate),
                                        fontSize = 11.sp,
                                        color = if(themeIndex==3) grey else darkGrey,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }

                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
