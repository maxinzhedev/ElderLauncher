package com.example.elderlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.elderlauncher.ui.HomeScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface { HomeScreen(onAddContact = {}, onEditContact = {}, onCallVoice = {}, onCallVideo = {}) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMain() {
    MaterialTheme { HomeScreen(onAddContact = {}, onEditContact = {}, onCallVoice = {}, onCallVideo = {}) }
}
