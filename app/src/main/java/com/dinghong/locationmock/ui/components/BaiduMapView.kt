package com.dinghong.locationmock.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng

/**
 * 百度地图Compose组件
 * 封装百度地图MapView，提供Compose风格的API
 */
@Composable
fun BaiduMapView(
    modifier: Modifier = Modifier,
    onMapReady: (BaiduMap) -> Unit = {},
    onMapClick: (LatLng) -> Unit = {},
    mapType: Int = BaiduMap.MAP_TYPE_SATELLITE,
    isTrafficEnabled: Boolean = false,
    isMyLocationEnabled: Boolean = false
) {
    val context = LocalContext.current
    var baiduMap by remember { mutableStateOf<BaiduMap?>(null) }
    
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                // 获取地图控制器
                val map = this.map
                baiduMap = map
                
                // 配置地图属性
                map.apply {
                    // 设置地图类型为卫星图
                    mapType = mapType
                    
                    // 设置交通图层
                    isTrafficEnabled = isTrafficEnabled
                    
                    // 设置我的位置图层
                    isMyLocationEnabled = isMyLocationEnabled
                    
                    // 隐藏百度Logo和比例尺
                    uiSettings.apply {
                        isCompassEnabled = false
                        isRotateGesturesEnabled = true
                        isScrollGesturesEnabled = true
                        isZoomGesturesEnabled = true
                        isOverlookingGesturesEnabled = false
                    }
                    
                    // 设置地图点击监听
                    setOnMapClickListener(object : BaiduMap.OnMapClickListener {
                        override fun onMapClick(latLng: LatLng) {
                            onMapClick(latLng)
                        }
                        
                        override fun onMapPoiClick(mapPoi: MapPoi): Boolean {
                            onMapClick(mapPoi.position)
                            return true
                        }
                    })
                    
                    // 设置默认地图中心点（北京）
                    val defaultCenter = LatLng(39.915, 116.404)
                    animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(defaultCenter, 12f))
                }
                
                // 回调地图准备完成
                onMapReady(map)
            }
        },
        update = { mapView ->
            // 更新地图配置
            baiduMap?.let { map ->
                map.mapType = mapType
                map.isTrafficEnabled = isTrafficEnabled
                map.isMyLocationEnabled = isMyLocationEnabled
            }
        }
    )
    
    // 组件销毁时清理资源
    DisposableEffect(Unit) {
        onDispose {
            // MapView的生命周期管理由AndroidView自动处理
        }
    }
}
