package com.example.checkmyfridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkmyfridge.ui.theme.CheckMyFridgeTheme


var themeIndex = 1 //1 블루, 2 우드, 3 다크

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeIndex by rememberSaveable { mutableIntStateOf(1) }
            CheckMyFridgeTheme(themeIndex = themeIndex) {
                MainScreen(
                    themeIndex = themeIndex,
                    onThemeChange = { themeIndex = it }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    themeIndex: Int = 1,
    onThemeChange: (Int) -> Unit = {}
) {
    val tabs = listOf("홈", "목록", "설정")
    val tabIcons = listOf(R.drawable.home, R.drawable.paper, R.drawable.settings)
    val clickIcons = listOf(R.drawable.home_dark, R.drawable.paper_dark, R.drawable.settings_dark)

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                tonalElevation = 0.dp,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.height(75.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = if (selectedTabIndex == index) clickIcons[index] else tabIcons[index]
                                ),
                                contentDescription = title
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTabIndex) {
            0 -> HomeScreen.Content(modifier = Modifier.padding(innerPadding))
            1 -> ListScreen.Content(modifier = Modifier.padding(innerPadding))
            else -> SettingsScreen.Content(
                modifier = Modifier.padding(innerPadding),
                themeIndex = themeIndex,
                onThemeChange = onThemeChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CheckMyFridgeTheme(themeIndex = 1) {
        MainScreen()
    }
}