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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onAddContact: () -> Unit,
    onEditContact: (Long) -> Unit,
    onCallVoice: (Long) -> Unit,
    onCallVideo: (Long) -> Unit
) {
    // 简化版本：直接显示占位的网格，未来替换为数据源驱动
    val contacts = listOf(
        1L, 2L, 3L, 4L
    )
    Surface(modifier = Modifier.fillMaxSize()) {
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
                            Text("一键语音")
                        }
                        Button(onClick = { onCallVideo(contacts[idx]) }) {
                            Text("一键视频")
                        }
                    }
                }
            }
        }
    }
}
