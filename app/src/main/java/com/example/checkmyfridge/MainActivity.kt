package com.example.checkmyfridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
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
import com.example.checkmyfridge.ui.theme.CheckMyFridgeTheme


var themeIndex = 2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CheckMyFridgeTheme(
                themeIndex = themeIndex
            ) {
                MainScreen(themeIndex = themeIndex)
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, themeIndex: Int = 1) {
    val tabs = listOf("홈", "목록", "설정")
    val tabIcons = listOf(R.drawable.home, R.drawable.paper, R.drawable.settings)
    val clickIcons = listOf(R.drawable.home_dark, R.drawable.paper_dark, R.drawable.settings_dark)


    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
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
            0 -> HomeScreen.Content(modifier = Modifier.padding(innerPadding).background(Color(0xFFD9EFFF)))
            1 -> ListScreen.Content(modifier = Modifier.padding(innerPadding))
            else -> SettingsScreen.Content(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CheckMyFridgeTheme (themeIndex = themeIndex) {
        MainScreen()
    }
}