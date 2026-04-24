package com.example.elderlauncher.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onAddContact: () -> Unit,
    onEditContact: (Long) -> Unit,
    onCallVoice: (Long) -> Unit,
    onCallVideo: (Long) -> Unit,
    onOpenSettings: () -> Unit
) {
    // 简化版本：直接显示占位的网格，未来替换为数据源驱动
    // 隐藏设置入口：通过顶部标题持续点击触发
    val tapWindowMs = 5000L
    var tapCount by remember { mutableStateOf(0) }
    var lastTap by remember { mutableStateOf(0L) }
    val contacts = listOf(
        1L, 2L, 3L, 4L
    )
    Surface(modifier = Modifier.fillMaxSize()) {
        // 顶部区域作为隐藏设置入口
        androidx.compose.material3.Text(
            text = "家人联系人",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    val now = System.currentTimeMillis()
                    if (now - lastTap <= tapWindowMs) {
                        tapCount += 1
                    } else {
                        tapCount = 1
                    }
                    lastTap = now
                    if (tapCount >= 5) {
                        onOpenSettings()
                        tapCount = 0
                    }
                }
        )
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            contentPadding = androidx.compose.ui.unit.PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contacts.size) { idx ->
                Card(modifier = Modifier, elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                    Column(
                        modifier = Modifier.padding(12.dp).fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "联系人 ${contacts[idx]}")
                        Button(onClick = { onCallVoice(contacts[idx]) }) {
                            Text("语音")
                        }
                        Button(onClick = { onCallVideo(contacts[idx]) }) {
                            Text("视频")
                        }
                    }
                }
            }
        }
    }
}
