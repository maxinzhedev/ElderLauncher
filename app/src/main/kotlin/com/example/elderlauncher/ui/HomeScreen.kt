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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.elderlauncher.dao.ContactDatabase
import com.example.elderlauncher.repo.ContactRepository
import com.example.elderlauncher.model.Contact
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun HomeScreen(
    onAddContact: () -> Unit,
    onEditContact: (Long) -> Unit,
    onCallVoice: (Long) -> Unit,
    onCallVideo: (Long) -> Unit,
    onOpenSettings: () -> Unit
) {
    // 数据驱动：从 Room 读取前 6 条联系人
    val context = LocalContext.current
    val db = remember { ContactDatabase.getDatabase(context) }
    val repo = remember { ContactRepository(db.contactDao()) }
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    // Hidden settings trigger
    val tapWindowMs = 5000L
    var tapCount by remember { mutableStateOf(0) }
    var lastTap by remember { mutableStateOf(0L) }
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
        // Load data on first composition
        LaunchedEffect(Unit) {
            try {
                val top = repo.getTopContacts()
                contacts = if (top.isNotEmpty()) top else {
                    // Fallback sample data for MVP demo
                    listOf(
                        Contact(id = 101, name = "父亲", wechatName = "Dad", colorHex = "#FFB74D"),
                        Contact(id = 102, name = "母亲", wechatName = "Mom", colorHex = "#4FC3F7"),
                        Contact(id = 103, name = "儿子", wechatName = "Son", colorHex = "#81C784"),
                        Contact(id = 104, name = "女儿", wechatName = "Daughter", colorHex = "#BA68C8"),
                        Contact(id = 105, name = "姥姥", wechatName = "Grandma", colorHex = "#90A4AE"),
                        Contact(id = 106, name = "爷爷", wechatName = "Grandpa", colorHex = "#FFA726")
                    )
                }
            } catch (_: Throwable) {
                // ignore
            }
        }
        LazyVerticalGrid(
            cells = GridCells.Fixed(2),
            contentPadding = androidx.compose.ui.unit.PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contacts.size) { idx ->
                val contact = contacts[idx]
                Card(modifier = Modifier, elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                    Column(
                        modifier = Modifier.padding(12.dp).fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Avatar
                        val avatarColor = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(contact.colorHex))
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = contact.name.firstOrNull()?.uppercase() ?: "?", fontSize = 28.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                        }
                        Spacer(Modifier.height(6.dp))
                        // Name
                        androidx.compose.material3.Text(
                            text = contact.name,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        // Actions: 长按触发拨打
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Voice call area with long-press to trigger actual call
                            androidx.compose.material3.Box(
                                modifier = Modifier
                                    .height(72.dp)
                                    .weight(1f)
                                    .combinedClickable(onClick = { /* quick feedback placeholder */ }, onLongClick = { onCallVoice(contact.id) }),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("语音通话", fontSize = 16.sp)
                            }
                            androidx.compose.material3.Box(
                                modifier = Modifier
                                    .height(72.dp)
                                    .weight(1f)
                                    .combinedClickable(onClick = { /* quick feedback placeholder */ }, onLongClick = { onCallVideo(contact.id) }),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("视频通话", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
