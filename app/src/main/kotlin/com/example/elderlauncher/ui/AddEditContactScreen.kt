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
import androidx.compose.runtime.rememberCoroutineScope
import com.example.elderlauncher.model.Contact
import com.example.elderlauncher.dao.ContactDatabase
import com.example.elderlauncher.repo.ContactRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import android.content.Context

@Composable
fun AddEditContactScreen(contactToEdit: Contact? = null, onDone: () -> Unit) {
    val context = LocalContext.current
    val db = remember { ContactDatabase.getDatabase(context) }
    val repo = remember { ContactRepository(db.contactDao()) }
    val scope = rememberCoroutineScope()
    val name = remember { mutableStateOf(contactToEdit?.name ?: "") }
    val wechat = remember { mutableStateOf(contactToEdit?.wechatName ?: "") }
    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text("添加联系人") })
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("姓名") }, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(value = wechat.value, onValueChange = { wechat.value = it }, label = { Text("微信昵称/备注") }, modifier = Modifier.padding(bottom = 8.dp))
                Button(onClick = {
                    val c = Contact(
                        id = contactToEdit?.id ?: 0,
                        name = name.value,
                        wechatName = wechat.value,
                        colorHex = contactToEdit?.colorHex ?: "#CCCCCC"
                    )
                    scope.launch {
                        if (contactToEdit == null) repo.insert(c) else repo.update(c)
                    }
                    onDone()
                }) {
                    Text("保存")
                }
            }
        }
    )
}
