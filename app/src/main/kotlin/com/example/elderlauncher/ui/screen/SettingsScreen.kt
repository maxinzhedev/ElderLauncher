package com.example.elderlauncher.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderlauncher.WeChatHelper

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onManageContacts: () -> Unit
) {
    val context = LocalContext.current
    val isWeChatInstalled = WeChatHelper.isWeChatInstalled(context)
    val isAccessibilityEnabled = WeChatHelper.isAccessibilityServiceEnabled(context)
    val canOverlay = WeChatHelper.canDrawOverlays(context)

    Scaffold { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)) {
            Text(text = "隐藏设置", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = if (isAccessibilityEnabled) "✓ 无障碍服务：已启用" else "✗ 无障碍服务：未启用", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { WeChatHelper.openAccessibilitySettings(context) }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text("打开无障碍设置", fontSize = 18.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = if (canOverlay) "✓ 悬浮窗权限：已授权" else "✗ 悬浮窗权限：未授权", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { WeChatHelper.openOverlaySettings(context) }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text("打开悬浮窗设置", fontSize = 18.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = if (isWeChatInstalled) "✓ 微信已安装" else "✗ 微信未安装", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (isWeChatInstalled) WeChatHelper.launchWeChat(context) else Toast.makeText(context, "请先安装微信", Toast.LENGTH_SHORT).show()
                    }, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("启动微信", fontSize = 18.sp) }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onManageContacts, modifier = Modifier.fillMaxWidth().height(72.dp)) {
                Text("管理联系人", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("返回", fontSize = 22.sp)
            }
        }
    }
}
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "悬浮窗状态：" + if (canOverlay) "已授权" else "未授权", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { WeChatHelper.openOverlaySettings(context) }) {
                        Text("打开悬浮窗设置")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = if (isWeChatInstalled) "微信已安装" else "微信未安装", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (isWeChatInstalled) WeChatHelper.launchWeChat(context) else Toast.makeText(context, "微信未安装，请安装微信", Toast.LENGTH_SHORT).show()
                    }) { Text("启动微信") }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onManageContacts, modifier = Modifier.fillMaxWidth().height(72.dp)) {
                Text("管理联系人", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text("返回", fontSize = 22.sp)
            }
        }
    }
}
