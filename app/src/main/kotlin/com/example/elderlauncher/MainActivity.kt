package com.example.elderlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.elderlauncher.ui.HomeScreen
import com.example.elderlauncher.ui.AddEditContactScreen
import com.example.elderlauncher.ui.SettingsScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

sealed class Screen {
    object Home : Screen()
    object AddEdit : Screen()
    object Settings : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
            MaterialTheme {
                Surface { 
                    when (currentScreen) {
                        is Screen.Home -> HomeScreen(
                            onAddContact = {},
                            onEditContact = { /* TODO */ },
                            onCallVoice = { /* TODO */ },
                            onCallVideo = { /* TODO */ },
                            onOpenSettings = { currentScreen = Screen.Settings }
                        )
                        is Screen.AddEdit -> AddEditContactScreen(onDone = { currentScreen = Screen.Home })
                        is Screen.Settings -> SettingsScreen(onBack = { currentScreen = Screen.Home })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    MaterialTheme { HomeScreen(onAddContact = {}, onEditContact = {}, onCallVoice = {}, onCallVideo = {}, onOpenSettings = {}) }
}
