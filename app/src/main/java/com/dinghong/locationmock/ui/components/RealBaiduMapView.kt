package com.dinghong.locationmock.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.util.Log
import android.view.View
import android.widget.FrameLayout
// 百度地图SDK导入
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.model.LatLng as BaiduLatLng

// 地图SDK类型定义 - 使用百度地图
typealias MapSDK = BaiduMap

/**
 * 真实的地图组件
 * 使用Google Maps SDK显示真实地图
 */
@Composable
fun RealBaiduMapView(
    modifier: Modifier = Modifier,
    onMapReady: (MapSDK?) -> Unit = {},
    onMapClick: (Pair<Double, Double>) -> Unit = {}
) {
    val context = LocalContext.current
    var isMapReady by remember { mutableStateOf(false) }
    var baiduMap by remember { mutableStateOf<BaiduMap?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            try {
                MapView(context).apply {
                    onCreate(null)
                    onResume()

                    val map = this.map
                    baiduMap = map
                    isMapReady = true

                    // 设置地图点击监听
                    map.setOnMapClickListener { latLng ->
                        onMapClick(latLng.latitude to latLng.longitude)
                    }

                    // 通知地图准备完成
                    onMapReady(map)
                    Log.i("BaiduMapView", "✅ 百度地图初始化成功")
                }
            } catch (e: Exception) {
                Log.e("BaiduMapView", "百度地图初始化失败: ${e.message}")
                // 创建占位符
                FrameLayout(context).apply {
                    setBackgroundColor(android.graphics.Color.parseColor("#E8F4FD"))
                    setOnClickListener {
                        val lat = 39.904200 + (Math.random() - 0.5) * 0.01
                        val lng = 116.407400 + (Math.random() - 0.5) * 0.01
                        onMapClick(lat to lng)
                    }
                }
            }
        },
        update = { mapView ->
            // 地图更新逻辑
        }
    )
}
