package com.dinghong.locationmock.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * 帮助对话框
 * 显示应用使用说明和功能介绍
 */
@Composable
fun HelpDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E1E)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 标题栏
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "使用帮助",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = Color.White
                        )
                    }
                }
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.Gray
                )
                
                // 帮助内容
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HelpSection(
                        title = "🎯 基本使用",
                        content = """
                        1. 在底部搜索框输入地址或坐标
                        2. 点击地图选择目标位置
                        3. 点击"开始模拟"按钮启动位置模拟
                        4. 点击"停止模拟"按钮停止位置模拟
                        """.trimIndent()
                    )
                    
                    HelpSection(
                        title = "🗺️ 地图操作",
                        content = """
                        • 点击地图任意位置选择目标坐标
                        • 使用右侧缩放按钮调整地图大小
                        • 点击指南针按钮重置地图方向
                        • 支持手势缩放和拖拽操作
                        """.trimIndent()
                    )
                    
                    HelpSection(
                        title = "📍 坐标输入格式",
                        content = """
                        支持以下格式：
                        • 经纬度：116.404,39.915
                        • 带空格：116.404, 39.915
                        • 地址搜索：北京市天安门广场
                        """.trimIndent()
                    )
                    
                    HelpSection(
                        title = "🔧 调试功能",
                        content = """
                        • 点击红色调试按钮查看详细日志
                        • 右上角状态指示器显示权限状态
                        • 支持复制调试日志到剪贴板
                        """.trimIndent()
                    )
                    
                    HelpSection(
                        title = "⚠️ 注意事项",
                        content = """
                        1. 需要开启"模拟位置"权限
                        2. 建议在开发者选项中设置本应用为模拟位置应用
                        3. 某些应用可能检测模拟位置，请谨慎使用
                        4. 仅供开发测试使用，请遵守相关法律法规
                        """.trimIndent()
                    )
                    
                    // 版本信息
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2D2D2D)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "reding定位模拟器 v2.0",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "基于百度地图SDK开发",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 帮助章节组件
 */
@Composable
private fun HelpSection(
    title: String,
    content: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = content,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
        )
    }
}
