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
import com.dinghong.locationmock.manager.SearchResultItem

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

    isSimulating: Boolean = false,
    onSimulateToggle: () -> Unit = {},
    onAddFavoriteClick: () -> Unit = {},
    onShowFavoritesClick: () -> Unit = {},
    currentCoordinate: String = "",
    searchSuggestions: List<SearchResultItem> = emptyList(),
    onSuggestionClick: (SearchResultItem) -> Unit = {}
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
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 当前坐标显示（移到顶部）
            if (currentCoordinate.isNotEmpty()) {
                CoordinateDisplay(coordinate = currentCoordinate)
            }

            // 搜索框和建议列表
            Column {
                SearchInputField(
                    searchText = searchText,
                    onSearchTextChange = onSearchTextChange,
                    onSearchSubmit = onSearchSubmit
                )

                // 搜索建议列表
                if (searchSuggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    SearchSuggestionList(
                        suggestions = searchSuggestions,
                        onSuggestionClick = onSuggestionClick
                    )
                }
            }
            
            // 控制按钮行
            ControlButtonsRow(
                isSimulating = isSimulating,
                onSimulateToggle = onSimulateToggle,
                onAddFavoriteClick = onAddFavoriteClick,
                onShowFavoritesClick = onShowFavoritesClick
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
    onSearchSubmit: () -> Unit
) {
    // 搜索框
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "地址或坐标",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        },
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
    onAddFavoriteClick: () -> Unit,
    onShowFavoritesClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isSimulating) "停止模拟" else "开始模拟")
        }

        // 添加收藏按钮
        IconButton(
            onClick = onAddFavoriteClick,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "添加收藏",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        // 收藏列表按钮
        IconButton(
            onClick = onShowFavoritesClick,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.tertiary)
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "收藏列表",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
