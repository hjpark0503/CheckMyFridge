package com.example.checkmyfridge

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StorageSection(
    title: String,
    items: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE0F0FF))
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(15.dp)
        )
        FlowRow(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { label ->
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(label)
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
                    .verticalScroll(rememberScrollState())
            ) {

                // 배경 레이어
                Column(modifier = Modifier.matchParentSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.White)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF98C8FF))
                    )
                }

                // 이미지
                Box(modifier = Modifier.fillMaxWidth().height(130.dp).padding(top = 30.dp, end = 20.dp)) {
                    Text(
                        text = "냉장고 재료를 추가해보세요~!",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 110.dp).align(Alignment.CenterEnd)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.fridge_happy),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp).align(Alignment.BottomEnd)
                    )
                }

                // 콘텐츠 레이어
                Column(modifier = Modifier.fillMaxWidth().padding(top = 130.dp)) {
                    val items1 = listOf("다진마늘", "양파", "삼겹살", "식빵", "닭가슴살")
                    val items2 = listOf("김치", "봄동", "고춧가루", "고구마", "감자", "계란", "양배추", "로제 소스", "까르보나라 소스")
                    val items3 = listOf("라면", "참치", "스팸")

                    StorageSection(title = "냉동", items = items1)
                    StorageSection(title = "냉장", items = items2)
                    StorageSection(title = "실온", items = items3)
                }
            }
        }
    }
}
