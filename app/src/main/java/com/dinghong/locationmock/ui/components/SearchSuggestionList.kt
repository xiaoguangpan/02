package com.dinghong.locationmock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dinghong.locationmock.manager.SearchResultItem

/**
 * 搜索建议列表组件
 */
@Composable
fun SearchSuggestionList(
    suggestions: List<SearchResultItem>,
    onSuggestionClick: (SearchResultItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isNotEmpty()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2D2D2D)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(suggestions) { suggestion ->
                    SearchSuggestionItem(
                        suggestion = suggestion,
                        onClick = { onSuggestionClick(suggestion) }
                    )
                }
            }
        }
    }
}

/**
 * 搜索建议项
 */
@Composable
private fun SearchSuggestionItem(
    suggestion: SearchResultItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "位置",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = suggestion.name,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = suggestion.address,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Text(
            text = suggestion.type,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
