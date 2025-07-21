package com.dinghong.locationmock.manager

import android.content.Context
import android.util.Log
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.geocode.*
import com.baidu.mapapi.search.poi.*
import com.dinghong.locationmock.utils.CoordinateConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 地图交互管理器
 * 负责处理地图点击、搜索、标记显示等交互功能
 */
class MapInteractionManager(private val context: Context) {
    
    companion object {
        private const val TAG = "MapInteractionManager"
    }
    
    private var baiduMap: BaiduMap? = null
    private var geocodeSearch: GeoCoder? = null
    private var poiSearch: PoiSearch? = null
    private var currentMarker: Marker? = null
    
    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<SearchResultItem>>(emptyList())
    val searchResults: StateFlow<List<SearchResultItem>> = _searchResults.asStateFlow()
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    
    /**
     * 搜索结果项
     */
    data class SearchResultItem(
        val name: String,
        val address: String,
        val location: LatLng,
        val type: String = "POI"
    )
    
    /**
     * 初始化地图
     */
    fun initializeMap(map: BaiduMap) {
        this.baiduMap = map
        
        // 初始化搜索服务
        geocodeSearch = GeoCoder.newInstance().apply {
            setOnGetGeoCodeResultListener(geocodeListener)
        }
        
        poiSearch = PoiSearch.newInstance().apply {
            setOnGetPoiResultListener(poiListener)
        }
        
        Log.i(TAG, "地图交互管理器已初始化")
    }
    
    /**
     * 处理地图点击事件
     */
    fun onMapClick(latLng: LatLng) {
        _selectedLocation.value = latLng
        addLocationMarker(latLng)
        
        // 执行反地理编码获取地址信息
        performReverseGeocode(latLng)
        
        Log.i(TAG, "地图点击: ${formatCoordinate(latLng)}")
    }
    
    /**
     * 执行地址搜索
     */
    fun searchAddress(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        _isSearching.value = true
        
        // 检查是否为坐标格式
        if (isCoordinateFormat(query)) {
            handleCoordinateInput(query)
        } else {
            // 执行POI搜索
            performPoiSearch(query)
        }
    }
    
    /**
     * 移动到指定位置
     */
    fun moveToLocation(latLng: LatLng, zoom: Float = 16f) {
        baiduMap?.let { map ->
            val mapStatus = MapStatusUpdateFactory.newLatLngZoom(latLng, zoom)
            map.animateMapStatus(mapStatus)
            
            _selectedLocation.value = latLng
            addLocationMarker(latLng)
        }
    }
    
    /**
     * 获取当前位置
     */
    fun getCurrentLocation() {
        // 这里可以集成定位SDK获取真实位置
        // 暂时使用默认位置（北京天安门）
        val defaultLocation = LatLng(39.9042, 116.4074)
        moveToLocation(defaultLocation)
        
        Log.i(TAG, "获取当前位置: ${formatCoordinate(defaultLocation)}")
    }
    
    /**
     * 缩放地图
     */
    fun zoomIn() {
        baiduMap?.let { map ->
            val currentZoom = map.mapStatus.zoom
            val newZoom = (currentZoom + 1f).coerceAtMost(21f)
            val mapStatus = MapStatusUpdateFactory.zoomTo(newZoom)
            map.animateMapStatus(mapStatus)
        }
    }
    
    fun zoomOut() {
        baiduMap?.let { map ->
            val currentZoom = map.mapStatus.zoom
            val newZoom = (currentZoom - 1f).coerceAtLeast(3f)
            val mapStatus = MapStatusUpdateFactory.zoomTo(newZoom)
            map.animateMapStatus(mapStatus)
        }
    }
    
    /**
     * 重置指南针
     */
    fun resetCompass() {
        baiduMap?.let { map ->
            val mapStatus = MapStatusUpdateFactory.newMapStatus(
                MapStatus.Builder(map.mapStatus)
                    .rotate(0f)
                    .overlook(0f)
                    .build()
            )
            map.animateMapStatus(mapStatus)
        }
    }
    
    /**
     * 添加位置标记
     */
    private fun addLocationMarker(latLng: LatLng) {
        baiduMap?.let { map ->
            // 清除之前的标记
            currentMarker?.remove()
            
            // 创建标记选项
            val markerOptions = MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true)
            
            // 添加新标记
            currentMarker = map.addOverlay(markerOptions) as Marker
            
            // 设置标记拖拽监听
            map.setOnMarkerDragListener(object : BaiduMap.OnMarkerDragListener {
                override fun onMarkerDrag(marker: Marker) {
                    // 拖拽中
                }
                
                override fun onMarkerDragEnd(marker: Marker) {
                    val newPosition = marker.position
                    _selectedLocation.value = newPosition
                    performReverseGeocode(newPosition)
                    Log.i(TAG, "标记拖拽结束: ${formatCoordinate(newPosition)}")
                }
                
                override fun onMarkerDragStart(marker: Marker) {
                    // 开始拖拽
                }
            })
        }
    }
    
    /**
     * 执行反地理编码
     */
    private fun performReverseGeocode(latLng: LatLng) {
        geocodeSearch?.reverseGeoCode(ReverseGeoCodeOption().location(latLng))
    }
    
    /**
     * 执行POI搜索
     */
    private fun performPoiSearch(query: String) {
        val searchOption = PoiNearbySearchOption()
            .keyword(query)
            .location(baiduMap?.mapStatus?.target ?: LatLng(39.915, 116.404))
            .radius(5000) // 5公里搜索半径
            .pageNum(0)
            .pageCapacity(20)
        
        poiSearch?.searchNearby(searchOption)
    }
    
    /**
     * 处理坐标输入
     */
    private fun handleCoordinateInput(input: String) {
        try {
            val coordinates = parseCoordinateString(input)
            if (coordinates != null) {
                val (lat, lng) = coordinates
                val latLng = LatLng(lat, lng)
                
                moveToLocation(latLng)
                
                _searchResults.value = listOf(
                    SearchResultItem(
                        name = "输入坐标",
                        address = formatCoordinate(latLng),
                        location = latLng,
                        type = "坐标"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析坐标失败: ${e.message}")
        } finally {
            _isSearching.value = false
        }
    }
    
    /**
     * 地理编码监听器
     */
    private val geocodeListener = object : OnGetGeoCoderResultListener {
        override fun onGetGeoCodeResult(result: GeoCodeResult?) {
            // 正地理编码结果
        }
        
        override fun onGetReverseGeoCodeResult(result: ReverseGeoCodeResult?) {
            result?.let { reverseResult ->
                if (reverseResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    val address = reverseResult.address
                    Log.i(TAG, "反地理编码成功: $address")
                    
                    // 更新搜索结果
                    _searchResults.value = listOf(
                        SearchResultItem(
                            name = "当前位置",
                            address = address,
                            location = reverseResult.location,
                            type = "地址"
                        )
                    )
                }
            }
            _isSearching.value = false
        }
    }
    
    /**
     * POI搜索监听器
     */
    private val poiListener = object : OnGetPoiResultListener {
        override fun onGetPoiResult(result: PoiResult?) {
            result?.let { poiResult ->
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    val results = poiResult.allPoi.map { poi ->
                        SearchResultItem(
                            name = poi.name,
                            address = poi.address,
                            location = poi.location,
                            type = "POI"
                        )
                    }
                    _searchResults.value = results
                    Log.i(TAG, "POI搜索成功，找到${results.size}个结果")
                }
            }
            _isSearching.value = false
        }
        
        override fun onGetPoiDetailResult(result: PoiDetailResult?) {
            // POI详情结果
        }
        
        override fun onGetPoiDetailResult(result: PoiDetailSearchResult?) {
            // POI详情搜索结果
        }
        
        override fun onGetPoiIndoorResult(result: PoiIndoorResult?) {
            // 室内POI结果
        }
    }
    
    /**
     * 检查是否为坐标格式
     */
    private fun isCoordinateFormat(input: String): Boolean {
        val coordinatePattern = Regex("""^-?\d+\.?\d*\s*,\s*-?\d+\.?\d*$""")
        return coordinatePattern.matches(input.trim())
    }
    
    /**
     * 解析坐标字符串
     */
    private fun parseCoordinateString(input: String): Pair<Double, Double>? {
        return try {
            val parts = input.split(",")
            if (parts.size == 2) {
                val lat = parts[0].trim().toDouble()
                val lng = parts[1].trim().toDouble()
                
                // 验证坐标范围
                if (lat in -90.0..90.0 && lng in -180.0..180.0) {
                    Pair(lat, lng)
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 格式化坐标显示
     */
    private fun formatCoordinate(latLng: LatLng): String {
        return String.format("%.6f, %.6f", latLng.latitude, latLng.longitude)
    }
    
    /**
     * 获取坐标系转换信息
     */
    fun getCoordinateConversions(latLng: LatLng): Map<String, String> {
        val bd09 = latLng
        val wgs84 = CoordinateConverter.bd09ToWgs84(latLng.latitude, latLng.longitude)
        val gcj02 = CoordinateConverter.bd09ToGcj02(latLng.latitude, latLng.longitude)
        
        return mapOf(
            "BD09LL(百度)" to formatCoordinate(bd09),
            "WGS84(GPS)" to String.format("%.6f, %.6f", wgs84.first, wgs84.second),
            "GCJ02(火星)" to String.format("%.6f, %.6f", gcj02.first, gcj02.second)
        )
    }
    
    /**
     * 清理资源
     */
    fun cleanup() {
        geocodeSearch?.destroy()
        poiSearch?.destroy()
        currentMarker?.remove()
        
        Log.i(TAG, "地图交互管理器已清理")
    }
}
