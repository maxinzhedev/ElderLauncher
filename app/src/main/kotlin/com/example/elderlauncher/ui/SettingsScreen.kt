package com.example.elderlauncher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Surface {
        Column(modifier = Modifier.padding(16.dp)) {
            SmallTopAppBar(title = { Text("隐藏设置") })
            Text("此处放置隐藏设置项（无障碍/悬浮窗等）")
        }
    }
}
