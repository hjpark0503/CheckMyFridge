package com.example.checkmyfridge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkmyfridge.ui.theme.grey
import java.util.Date

class ListScreen {
    companion object {
        private fun makeItem(name: String, category: String) = Item(
            name = name,
            addedDate = Date(),
            expirationDate = Date(),
            category = category
        )

        @Composable
        fun Content(modifier: Modifier = Modifier) {
            val categories = listOf("냉동", "냉장", "실온")
            var selectedIndex by remember { mutableIntStateOf(0) }

            val allItems = remember {
                mutableListOf(
                    mutableListOf(
                        makeItem("다진마늘", "냉동"),
                        makeItem("양파", "냉동"),
                        makeItem("삼겹살", "냉동"),
                        makeItem("식빵", "냉동"),
                        makeItem("닭가슴살", "냉동")
                    ),
                    mutableListOf(
                        makeItem("김치", "냉장"),
                        makeItem("봄동", "냉장"),
                        makeItem("고춧가루", "냉장"),
                        makeItem("고구마", "냉장"),
                        makeItem("감자", "냉장"),
                        makeItem("계란", "냉장"),
                        makeItem("양배추", "냉장"),
                        makeItem("로제 소스", "냉장"),
                        makeItem("까르보나라 소스", "냉장")
                    ),
                    mutableListOf(
                        makeItem("라면", "실온"),
                        makeItem("참치", "실온"),
                        makeItem("스팸", "실온")
                    )
                )
            }
            val listStates = remember {
                allItems.map { mutableStateOf(it.toList()) }
            }

            var inputText by remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current

            fun addItem() {
                val trimmed = inputText.trim()
                if (trimmed.isNotEmpty()) {
                    allItems[selectedIndex].add(0, makeItem(trimmed, categories[selectedIndex]))
                    listStates[selectedIndex].value = allItems[selectedIndex].toList()
                    inputText = ""
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
                    items(listStates[selectedIndex].value) { item ->
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 8.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
