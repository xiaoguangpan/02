package com.dinghong.locationmock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// import com.baidu.mapapi.map.*
// import com.baidu.mapapi.model.LatLng

// 使用manager包中的统一类型定义
import com.dinghong.locationmock.manager.LatLng
import com.dinghong.locationmock.manager.BaiduMap

/**
 * 百度地图Compose组件（临时占位版本）
 * TODO: 添加百度地图SDK依赖后替换为真实实现
 */
@Composable
fun BaiduMapView(
    modifier: Modifier = Modifier,
    onMapReady: (BaiduMap) -> Unit = {},
    onMapClick: (LatLng) -> Unit = {},
    mapType: Int = 1, // BaiduMap.MAP_TYPE_SATELLITE
    isTrafficEnabled: Boolean = false,
    isMyLocationEnabled: Boolean = false
) {
    val context = LocalContext.current

    // 临时占位界面，显示地图加载提示
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🗺️",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )
            Text(
                text = "地图加载中...",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "需要添加百度地图SDK依赖",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }

    // 模拟地图准备完成回调
    LaunchedEffect(Unit) {
        onMapReady(BaiduMap())
    }
}
