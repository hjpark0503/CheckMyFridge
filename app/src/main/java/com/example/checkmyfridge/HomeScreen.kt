package com.example.checkmyfridge

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkmyfridge.db.ItemEntity
import com.example.checkmyfridge.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
private fun StorageSection(
    title: String,
    items: List<ItemEntity>,
    onDelete: (ItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            modifier = Modifier.padding(15.dp)
        )

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(text = "재료가 없어요", color = darkGrey)
            }
        } else {
            FlowRow(
                modifier = Modifier.padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items.forEach { item ->
                    var showDialog by remember { mutableStateOf(false) }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("재료 삭제",  fontSize = 18.sp) },
                            text = { Text("'${item.name}'을(를) 삭제하시겠습니까?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    onDelete(item)
                                    showDialog = false
                                }) { Text("삭제", color = red) }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) { Text("취소") }
                            }
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Common.getRestOfDayColor(item.expirationDate),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = { showDialog = true }
                            )
                    ) {
                        Text(
                            text = item.name,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

class HomeScreen {
    companion object {
        @Composable
        fun Content(modifier: Modifier = Modifier) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .verticalScroll(rememberScrollState())
            ) {


                // 콘텐츠 레이어
                Column(modifier = Modifier.fillMaxWidth().padding(5.dp)) {

                    //냉장고 요정
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(white),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val fullText = "냉장고 재료를 추가해보세요~!"
                        var displayedText by remember { mutableStateOf("") }
                        LaunchedEffect(Unit) {
                            fullText.forEach { char ->
                                displayedText += char
                                delay(150L)
                            }
                        }
                        Text(
                            text = displayedText,
                            color = black,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f).padding(start = 20.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.fridge_happy),
                            contentDescription = null,
                            modifier = Modifier
                                .size(110.dp)
                                .padding(end = 10.dp)
                        )
                    }

                    val context = LocalContext.current
                    val dao = remember { Common.getDao(context) }
                    val scope = rememberCoroutineScope()
                    val items1 by dao.getItemsByCategory("냉동").collectAsState(initial = emptyList())
                    val items2 by dao.getItemsByCategory("냉장").collectAsState(initial = emptyList())
                    val items3 by dao.getItemsByCategory("실온").collectAsState(initial = emptyList())
                    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                    val onDelete: (ItemEntity) -> Unit = { scope.launch { dao.deleteById(it.id) } }

                    if (isLandscape) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            StorageSection(title = "냉동", items = items1, onDelete = onDelete, modifier = Modifier.weight(1f).fillMaxHeight())
                            StorageSection(title = "냉장", items = items2, onDelete = onDelete, modifier = Modifier.weight(1f).fillMaxHeight())
                            StorageSection(title = "실온", items = items3, onDelete = onDelete, modifier = Modifier.weight(1f).fillMaxHeight())
                        }
                    } else {
                        StorageSection(title = "냉동", items = items1, onDelete = onDelete, modifier = Modifier.fillMaxWidth())
                        StorageSection(title = "냉장", items = items2, onDelete = onDelete, modifier = Modifier.fillMaxWidth())
                        StorageSection(title = "실온", items = items3, onDelete = onDelete, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
