package com.dinghong.locationmock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dinghong.locationmock.ui.theme.ControlCardBackground

/**
 * 底部控制卡片
 * 包含搜索框、位置获取、模拟控制、增强模式开关等功能
 */
@Composable
fun BottomControlCard(
    modifier: Modifier = Modifier,
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    onSearchSubmit: () -> Unit = {},
    onCurrentLocationClick: () -> Unit = {},
    isSimulating: Boolean = false,
    onSimulateToggle: () -> Unit = {},
    isEnhancedMode: Boolean = false,
    onEnhancedModeToggle: () -> Unit = {},
    currentCoordinate: String = ""
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ControlCardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 搜索框
            SearchInputField(
                searchText = searchText,
                onSearchTextChange = onSearchTextChange,
                onSearchSubmit = onSearchSubmit,
                onCurrentLocationClick = onCurrentLocationClick
            )
            
            // 当前坐标显示
            if (currentCoordinate.isNotEmpty()) {
                CoordinateDisplay(coordinate = currentCoordinate)
            }
            
            // 控制按钮行
            ControlButtonsRow(
                isSimulating = isSimulating,
                onSimulateToggle = onSimulateToggle,
                isEnhancedMode = isEnhancedMode,
                onEnhancedModeToggle = onEnhancedModeToggle
            )
        }
    }
}

/**
 * 搜索输入框
 */
@Composable
private fun SearchInputField(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onCurrentLocationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 搜索框
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("输入地址或坐标 (如: 116.404,39.915)", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = Color.White
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { onSearchTextChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "清除",
                            tint = Color.White
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray
            )
        )
        
        // 当前位置按钮
        IconButton(
            onClick = onCurrentLocationClick,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "获取当前位置",
                tint = Color.White
            )
        }
    }
}

/**
 * 坐标显示
 */
@Composable
private fun CoordinateDisplay(coordinate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Text(
            text = "当前坐标: $coordinate",
            modifier = Modifier.padding(12.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * 控制按钮行
 */
@Composable
private fun ControlButtonsRow(
    isSimulating: Boolean,
    onSimulateToggle: () -> Unit,
    isEnhancedMode: Boolean,
    onEnhancedModeToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 模拟控制按钮
        Button(
            onClick = onSimulateToggle,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSimulating) Color(0xFFFF5722) else MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = if (isSimulating) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (isSimulating) "停止模拟" else "开始模拟",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isSimulating) "停止模拟" else "开始模拟")
        }
        
        // 增强模式开关
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "增强模式",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = isEnhancedMode,
                onCheckedChange = { onEnhancedModeToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}
