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
import androidx.compose.ui.graphics.graphicsLayer
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
 * 百度地图Compose组件（准备SDK集成版本）
 * 支持点击交互，缩放控制，为真实地图SDK做准备
 */
@Composable
fun BaiduMapView(
    modifier: Modifier = Modifier,
    onMapReady: (BaiduMap) -> Unit = {},
    onMapClick: (LatLng) -> Unit = {},
    mapType: Int = 1, // BaiduMap.MAP_TYPE_SATELLITE
    isTrafficEnabled: Boolean = false,
    isMyLocationEnabled: Boolean = false,
    zoomLevel: Float = 15f
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var currentZoom by remember { mutableStateOf(zoomLevel) }

    // 增强的地图模拟界面，支持缩放和交互
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2E3440),
                        Color(0xFF3B4252),
                        Color(0xFF434C5E)
                    )
                )
            )
    ) {
        // 地图网格背景（支持缩放）
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = currentZoom / 15f,
                    scaleY = currentZoom / 15f
                )
        ) {
            val baseGridSize = 50.dp.toPx()
            val gridSize = baseGridSize * (currentZoom / 15f)
            val strokeWidth = 1.dp.toPx()

            // 绘制地图网格线
            for (x in 0..((size.width / gridSize).toInt() + 2)) {
                drawLine(
                    color = Color.White.copy(alpha = 0.15f),
                    start = Offset(x * gridSize, 0f),
                    end = Offset(x * gridSize, size.height),
                    strokeWidth = strokeWidth
                )
            }

            for (y in 0..((size.height / gridSize).toInt() + 2)) {
                drawLine(
                    color = Color.White.copy(alpha = 0.15f),
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
            // 地图状态提示
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🗺️",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "模拟地图已就绪",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "点击选择位置 • 缩放级别: ${String.format("%.1f", currentZoom)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
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
