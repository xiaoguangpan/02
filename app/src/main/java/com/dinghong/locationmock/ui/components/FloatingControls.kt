package com.dinghong.locationmock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dinghong.locationmock.ui.theme.FloatingButtonBackground

/**
 * 右侧悬浮控件组
 * 包含调试、帮助、指南针重置、缩放控制等功能按钮
 */
@Composable
fun FloatingControls(
    modifier: Modifier = Modifier,
    onDebugClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onNavigationClick: () -> Unit = {},
    onZoomInClick: () -> Unit = {},
    onZoomOutClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 调试按钮
        FloatingControlButton(
            icon = Icons.Default.BugReport,
            contentDescription = "调试面板",
            onClick = onDebugClick,
            backgroundColor = Color(0xFFFF5722)
        )
        
        // 帮助按钮
        FloatingControlButton(
            icon = Icons.Default.Help,
            contentDescription = "帮助",
            onClick = onHelpClick
        )
        
        // 导航按钮
        FloatingControlButton(
            icon = Icons.Default.Navigation,
            contentDescription = "导航",
            onClick = onNavigationClick
        )
        
        // 分隔线
        Divider(
            modifier = Modifier
                .width(32.dp)
                .padding(vertical = 4.dp),
            color = Color.White.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        
        // 放大按钮
        FloatingControlButton(
            icon = Icons.Default.Add,
            contentDescription = "放大",
            onClick = onZoomInClick
        )
        
        // 缩小按钮
        FloatingControlButton(
            icon = Icons.Default.Remove,
            contentDescription = "缩小",
            onClick = onZoomOutClick
        )
    }
}

/**
 * 悬浮控制按钮
 */
@Composable
private fun FloatingControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    backgroundColor: Color = FloatingButtonBackground,
    iconTint: Color = Color.White
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
