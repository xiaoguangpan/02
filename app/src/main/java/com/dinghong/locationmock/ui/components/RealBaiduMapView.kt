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
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng as GoogleLatLng
import com.google.android.gms.maps.model.MarkerOptions

// 地图SDK类型定义 - 使用Google Maps
typealias MapSDK = GoogleMap

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
    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                onCreate(null)
                onResume()
                getMapAsync { map ->
                    googleMap = map
                    isMapReady = true

                    // 设置默认位置为北京天安门
                    val defaultLocation = GoogleLatLng(39.9042, 116.4074)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

                    // 设置地图点击监听
                    map.setOnMapClickListener { latLng ->
                        onMapClick(latLng.latitude to latLng.longitude)

                        // 清除之前的标记并添加新标记
                        map.clear()
                        map.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title("选中位置")
                        )
                    }

                    // 通知地图准备完成
                    onMapReady(map)
                    Log.i("GoogleMapView", "✅ Google地图初始化成功")
                }
            }
        },
        update = { mapView ->
            // 地图更新逻辑
        }
    )
}
