package com.dinghong.locationmock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dinghong.locationmock.ui.theme.*

/**
 * 专业调试面板组件
 * 提供彩色日志显示、实时状态监控、调试信息复制导出等功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugPanel(
    modifier: Modifier = Modifier,
    debugLogs: List<String>,
    onClose: () -> Unit,
    onClearLogs: () -> Unit,
    onCopyLogs: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 调试面板标题栏
            DebugPanelHeader(
                onClose = onClose,
                onClearLogs = onClearLogs,
                onCopyLogs = onCopyLogs,
                logCount = debugLogs.size
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 调试日志列表（始终显示）
            DebugLogsList(
                logs = debugLogs,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * 调试面板标题栏
 */
@Composable
private fun DebugPanelHeader(
    onClose: () -> Unit,
    onClearLogs: () -> Unit,
    onCopyLogs: () -> Unit,
    logCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 标题和日志计数
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BugReport,
                contentDescription = "调试",
                tint = DebugLogError,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "调试面板",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = CircleShape,
                color = DebugLogInfo,
                modifier = Modifier.size(20.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = logCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }
        
        // 操作按钮组
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 清除日志按钮
            IconButton(
                onClick = onClearLogs,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "清除日志",
                    tint = DebugLogWarning,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // 复制日志按钮
            IconButton(
                onClick = onCopyLogs,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "复制日志",
                    tint = DebugLogInfo,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // 关闭按钮
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = DebugLogError,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * 调试日志列表
 */
@Composable
private fun DebugLogsList(
    logs: List<String>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    // 自动滚动到最新日志
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(8.dp)
    ) {
        if (logs.isEmpty()) {
            // 空状态显示
            EmptyLogsDisplay()
        } else {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                reverseLayout = true // 最新日志在顶部
            ) {
                items(logs) { log ->
                    DebugLogItem(log = log)
                }
            }
        }
    }
}

/**
 * 空日志状态显示
 */
@Composable
private fun EmptyLogsDisplay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "无日志",
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "暂无调试日志",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 单个调试日志项
 */
@Composable
private fun DebugLogItem(log: String) {
    val logColor = getLogColor(log)
    val logIcon = getLogIcon(log)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 日志类型图标
        Icon(
            imageVector = logIcon,
            contentDescription = null,
            tint = logColor,
            modifier = Modifier.size(12.dp)
        )
        
        // 日志内容
        Text(
            text = log,
            color = logColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 14.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * 根据日志内容获取颜色
 */
private fun getLogColor(log: String): Color {
    return when {
        log.contains("[ERROR]") -> DebugLogError
        log.contains("[SUCCESS]") -> DebugLogSuccess
        log.contains("[COORDINATE]") -> DebugLogCoordinate
        log.contains("[WARNING]") -> DebugLogWarning
        log.contains("[INFO]") -> DebugLogInfo
        else -> Color.White
    }
}

/**
 * 根据日志内容获取图标
 */
private fun getLogIcon(log: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        log.contains("[ERROR]") -> Icons.Default.Error
        log.contains("[SUCCESS]") -> Icons.Default.CheckCircle
        log.contains("[COORDINATE]") -> Icons.Default.LocationOn
        log.contains("[WARNING]") -> Icons.Default.Warning
        log.contains("[INFO]") -> Icons.Default.Info
        else -> Icons.Default.Circle
    }
}

/**
 * 调试状态指示器
 */
@Composable
fun DebugStatusIndicator(
    hasLocationPermission: Boolean,
    hasMockLocationPermission: Boolean,
    onLocationPermissionClick: () -> Unit,
    onMockPermissionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 位置权限状态（可点击）
        StatusIndicatorItem(
            icon = Icons.Default.LocationOn,
            label = "位置权限",
            isActive = hasLocationPermission,
            activeColor = DebugLogSuccess,
            inactiveColor = DebugLogError,
            onClick = onLocationPermissionClick
        )

        // 模拟位置权限状态（可点击）
        StatusIndicatorItem(
            icon = Icons.Default.DeveloperMode,
            label = "模拟权限",
            isActive = hasMockLocationPermission,
            activeColor = DebugLogSuccess,
            inactiveColor = DebugLogError,
            onClick = onMockPermissionClick
        )
    }
}

/**
 * 状态指示器项
 */
@Composable
private fun StatusIndicatorItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = if (onClick != null) {
            Modifier.clickable { onClick() }
        } else {
            Modifier
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) activeColor else inactiveColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            color = if (isActive) activeColor else inactiveColor,
            fontSize = 8.sp,
            maxLines = 1
        )
    }
}
