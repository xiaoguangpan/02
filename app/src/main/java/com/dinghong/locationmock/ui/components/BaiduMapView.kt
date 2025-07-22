package com.dinghong.locationmock.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// import com.baidu.mapapi.map.*
// import com.baidu.mapapi.model.LatLng

// 使用manager包中的统一类型定义
import com.dinghong.locationmock.manager.LatLng
import com.dinghong.locationmock.manager.BaiduMap

/**
 * 百度地图Compose组件（增强占位版本）
 * 支持点击交互，等待百度地图SDK集成
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
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    // 增强的占位界面，支持点击交互
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        // 网格背景
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSize = 50.dp.toPx()
            val strokeWidth = 1.dp.toPx()

            // 绘制网格线
            for (x in 0..((size.width / gridSize).toInt())) {
                drawLine(
                    color = Color.White.copy(alpha = 0.1f),
                    start = Offset(x * gridSize, 0f),
                    end = Offset(x * gridSize, size.height),
                    strokeWidth = strokeWidth
                )
            }

            for (y in 0..((size.height / gridSize).toInt())) {
                drawLine(
                    color = Color.White.copy(alpha = 0.1f),
                    start = Offset(0f, y * gridSize),
                    end = Offset(size.width, y * gridSize),
                    strokeWidth = strokeWidth
                )
            }
        }

        // 点击检测层
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // 将屏幕坐标转换为模拟的地理坐标
                        val lat = 39.9042 + (0.5 - offset.y / size.height) * 0.1
                        val lng = 116.4074 + (offset.x / size.width - 0.5) * 0.1
                        val latLng = LatLng(lat, lng)

                        selectedLocation = latLng
                        onMapClick(latLng)
                    }
                }
        ) {
            // 中心提示
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🗺️",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "模拟地图视图",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "点击任意位置选择坐标",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                if (selectedLocation != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.7f)
                        )
                    ) {
                        Text(
                            text = "已选择: ${String.format("%.6f, %.6f", selectedLocation!!.latitude, selectedLocation!!.longitude)}",
                            modifier = Modifier.padding(8.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // 选中位置标记
            selectedLocation?.let { location ->
                val density = LocalDensity.current
                BoxWithConstraints {
                    val offsetX = maxWidth * (0.5f + (location.longitude - 116.4074) / 0.1).toFloat()
                    val offsetY = maxHeight * (0.5f - (location.latitude - 39.9042) / 0.1).toFloat()

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .offset(
                                x = offsetX.coerceIn(0.dp, maxWidth - 20.dp),
                                y = offsetY.coerceIn(0.dp, maxHeight - 20.dp)
                            )
                            .background(
                                Color.Red,
                                CircleShape
                            )
                            .border(2.dp, Color.White, CircleShape)
                    )
                }
            }
        }
    }

    // 模拟地图准备完成回调
    LaunchedEffect(Unit) {
        onMapReady(BaiduMap())
    }
}
