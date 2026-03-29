package com.example.checkmyfridge

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkmyfridge.db.AppDatabase
import com.example.checkmyfridge.db.ItemEntity
import com.example.checkmyfridge.ui.theme.red
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListScreen {
    companion object {
        @Composable
        fun Content(modifier: Modifier = Modifier) {
            val context = LocalContext.current
            val dao = remember { Common.getDao(context) }
            val scope = rememberCoroutineScope()

            val categories = listOf("냉동", "냉장", "실온")
            var selectedIndex by remember { mutableIntStateOf(0) }
            val currentCategory = categories[selectedIndex]

            val items by dao.getItemsByCategory(currentCategory).collectAsState(initial = emptyList())

            var inputText by remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current

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
                                restOfDay = 7
                            )
                        )
                    }
                    inputText = ""
                    focusManager.clearFocus()
                }
            }

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
            ) {
                // 상단 버튼 3개
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEachIndexed { index, label ->
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
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // EditText
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
                                title = { Text("재료 삭제", fontSize = 18.sp) },
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
                                    .background(color = Common.getRestOfDayColor(item.restOfDay), shape = CircleShape)
                                    .align(Alignment.CenterVertically))
                            Text(
                                text = item.name,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f).align(Alignment.CenterVertically).padding(start = 12.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "추가일자 " + Common.formatDate(item.addedDate),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.align(Alignment.End)
                                )
                                Text(
                                    text = "소비기한 " + Common.formatDate(item.expirationDate),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }

                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
