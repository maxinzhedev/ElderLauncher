package com.example.elderlauncher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddEditContactScreen(onDone: () -> Unit) {
    val name = remember { mutableStateOf("") }
    val wechat = remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text("添加联系人") })
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("姓名") }, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(value = wechat.value, onValueChange = { wechat.value = it }, label = { Text("微信昵称/备注") }, modifier = Modifier.padding(bottom = 8.dp))
                Button(onClick = { onDone() }) {
                    Text("保存")
                }
            }
        }
    )
}
