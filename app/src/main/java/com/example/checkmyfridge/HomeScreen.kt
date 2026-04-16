package com.example.checkmyfridge

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkmyfridge.db.ItemEntity
import com.example.checkmyfridge.ui.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StorageSection(
    title: String,
    items: List<ItemEntity>,
    onDelete: (ItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val sorted = items.sortedBy { it.expirationDate }

    Column(
        modifier = modifier
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(lightGrey)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            if (items.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = lightGrey,
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${items.size}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }


        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "재료가 없어요", color = darkGrey, fontSize = 13.sp)
            }
        } else {
            FlowRow(
                modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                sorted.forEach { item ->
                    var showDialog by remember { mutableStateOf(false) }
                    val dDay = (item.expirationDate - Common.today()) / 86_400_000L

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("재료 삭제", fontSize = 16.sp) },
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
                        shape = RoundedCornerShape(10.dp),
                        color = Common.getRestOfDayColor(item.expirationDate),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = { showDialog = true }
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = item.name,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun topSection(allItems: List<ItemEntity>){

    val cal = Calendar.getInstance()
    val month = cal.get(Calendar.MONTH) + 1
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val dayOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")[cal.get(Calendar.DAY_OF_WEEK) - 1]

    val sufficient = allItems.count { (it.expirationDate - Common.today()) / 86_400_000L > 3 }
    val imminent   = allItems.count { (it.expirationDate - Common.today()) / 86_400_000L in 0..3 }
    val expired    = allItems.count { it.expirationDate < Common.today() }


    Column (modifier = Modifier.padding(end = 50.dp)){
        // 날짜
        Text(
            text = "${month}월 ${day}일 (${dayOfWeek})",
            fontSize = 13.sp,
            color = darkGrey
        )

        Spacer(modifier = Modifier.height(4.dp))
        // 총 재료 개수 (큰 숫자)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "${allItems.size}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 52.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "가지",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

    }

    // 3가지 통계
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, grey, RoundedCornerShape(10.dp))
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(
            label = "기한 여유",
            value = sufficient,
            valueColor = green,
            modifier = Modifier.weight(1f)
        )
        VerticalDivider(
            modifier = Modifier.height(32.dp),
            color = grey,
            thickness = 1.dp
        )
        StatItem(
            label = "기한 임박",
            value = imminent,
            valueColor = yellow,
            modifier = Modifier.weight(1f)
        )
        VerticalDivider(
            modifier = Modifier.height(32.dp),
            color = grey,
            thickness = 1.dp
        )
        StatItem(
            label = "기한 만료",
            value = expired,
            valueColor = lightRed,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}
class HomeScreen {
    companion object {
        @Composable
        fun Content(modifier: Modifier = Modifier) {
            val context = LocalContext.current
            val dao = remember { Common.getDao(context) }
            val scope = rememberCoroutineScope()
            val items1 by dao.getItemsByCategory("냉동").collectAsState(initial = emptyList())
            val items2 by dao.getItemsByCategory("냉장").collectAsState(initial = emptyList())
            val items3 by dao.getItemsByCategory("실온").collectAsState(initial = emptyList())
            val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            val onDelete: (ItemEntity) -> Unit = { scope.launch { dao.deleteById(it.id) } }



            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(white)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── 상단 히어로 영역 ──────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if(isLandscape){
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 50.dp, end = 50.dp, top = 28.dp, bottom = 0.dp)
                        ) {
                            topSection(items1+items2+items3);
                        }
                    }else{
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 0.dp)
                        ) {
                            topSection(items1+items2+items3)
                        }
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── 보관 섹션 ─────────────────────────────────────────
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        StorageSection(title = "냉동", items = items1, onDelete = onDelete, modifier = Modifier.weight(1f).fillMaxHeight())
                        StorageSection(title = "냉장", items = items2, onDelete = onDelete, modifier = Modifier.weight(1f).fillMaxHeight())
                        StorageSection(title = "실온", items = items3, onDelete = onDelete, modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                } else {
                    StorageSection(title = "냉동", items = items1, onDelete = onDelete, modifier = Modifier.fillMaxWidth())
                    StorageSection(title = "냉장", items = items2, onDelete = onDelete, modifier = Modifier.fillMaxWidth())
                    StorageSection(title = "실온", items = items3, onDelete = onDelete, modifier = Modifier.fillMaxWidth())
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: Int,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$value",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = darkGrey
        )
    }
}
