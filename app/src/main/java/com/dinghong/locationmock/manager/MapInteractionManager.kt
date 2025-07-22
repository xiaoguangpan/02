package com.dinghong.locationmock.manager

import android.content.Context
import android.content.Intent
import android.util.Log
import com.dinghong.locationmock.utils.CoordinateConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 临时注释百度地图SDK导入
// import com.baidu.mapapi.map.*
// import com.baidu.mapapi.model.LatLng
// import com.baidu.mapapi.search.core.SearchResult
// import com.baidu.mapapi.search.geocode.*
// import com.baidu.mapapi.search.poi.*

// 临时数据类定义
data class LatLng(val latitude: Double, val longitude: Double)
data class BaiduMap(val dummy: String = "placeholder")
data class Marker(val position: LatLng)
data class SearchResult(val error: String = "")
data class GeoCoder(val dummy: String = "placeholder")
data class PoiSearch(val dummy: String = "placeholder")

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
     * 初始化地图（模拟版本）
     * 注意：当前使用模拟地图组件，等待百度地图SDK集成
     */
    fun initializeMap(map: BaiduMap) {
        this.baiduMap = map

        // 临时注释搜索服务初始化
        // geocodeSearch = GeoCoder.newInstance()
        // poiSearch = PoiSearch.newInstance()

        Log.i(TAG, "模拟地图组件已初始化，等待百度地图SDK集成")
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
     * 执行地址搜索（增强模拟版本）
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
            // 增强的模拟搜索结果，提供更真实的建议
            val suggestions = generateSearchSuggestions(query)
            _searchResults.value = suggestions
            _isSearching.value = false
        }
    }

    /**
     * 生成搜索建议（模拟百度地图API响应）
     */
    private fun generateSearchSuggestions(query: String): List<SearchResultItem> {
        val suggestions = mutableListOf<SearchResultItem>()

        // 基于查询词生成相关建议
        when {
            query.contains("北京", ignoreCase = true) || query.contains("beijing", ignoreCase = true) -> {
                suggestions.addAll(listOf(
                    SearchResultItem("北京天安门广场", "北京市东城区东长安街", LatLng(39.9042, 116.4074), "景点"),
                    SearchResultItem("北京故宫博物院", "北京市东城区景山前街4号", LatLng(39.9163, 116.3972), "景点"),
                    SearchResultItem("北京王府井大街", "北京市东城区王府井大街", LatLng(39.9097, 116.4142), "商业区")
                ))
            }
            query.contains("上海", ignoreCase = true) || query.contains("shanghai", ignoreCase = true) -> {
                suggestions.addAll(listOf(
                    SearchResultItem("上海外滩", "上海市黄浦区中山东一路", LatLng(31.2397, 121.4990), "景点"),
                    SearchResultItem("上海东方明珠", "上海市浦东新区世纪大道1号", LatLng(31.2397, 121.4990), "景点"),
                    SearchResultItem("上海南京路步行街", "上海市黄浦区南京东路", LatLng(31.2342, 121.4707), "商业区")
                ))
            }
            query.contains("广州", ignoreCase = true) || query.contains("guangzhou", ignoreCase = true) -> {
                suggestions.addAll(listOf(
                    SearchResultItem("广州塔", "广州市海珠区阅江西路222号", LatLng(23.1081, 113.3245), "景点"),
                    SearchResultItem("广州白云山", "广州市白云区广园中路801号", LatLng(23.1693, 113.2927), "景点")
                ))
            }
            else -> {
                // 通用搜索建议
                suggestions.addAll(listOf(
                    SearchResultItem("$query - 地点1", "模拟地址：${query}附近", LatLng(39.915 + Math.random() * 0.01, 116.404 + Math.random() * 0.01), "地点"),
                    SearchResultItem("$query - 地点2", "模拟地址：${query}周边", LatLng(39.915 + Math.random() * 0.01, 116.404 + Math.random() * 0.01), "地点"),
                    SearchResultItem("$query - 商圈", "模拟商圈：${query}商业区", LatLng(39.915 + Math.random() * 0.01, 116.404 + Math.random() * 0.01), "商圈")
                ))
            }
        }

        return suggestions.take(5) // 最多返回5个建议
    }
    
    /**
     * 移动到指定位置（临时简化版本）
     */
    fun moveToLocation(latLng: LatLng, zoom: Float = 16f) {
        // 临时注释地图操作
        // baiduMap?.let { map ->
        //     val mapStatus = MapStatusUpdateFactory.newLatLngZoom(latLng, zoom)
        //     map.animateMapStatus(mapStatus)
        // }

        _selectedLocation.value = latLng
        // addLocationMarker(latLng) // 临时注释
        Log.i(TAG, "移动到位置: ${formatCoordinate(latLng)}")
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
     * 缩放地图（模拟版本）
     */
    fun zoomIn() {
        // 模拟地图缩放操作
        Log.i(TAG, "🔍 地图放大 - 模拟操作")
    }

    fun zoomOut() {
        // 模拟地图缩放操作
        Log.i(TAG, "🔍 地图缩小 - 模拟操作")
    }

    /**
     * 启动导航
     */
    fun startNavigation(destination: LatLng) {
        try {
            // 使用百度地图导航
            val intent = Intent().apply {
                data = android.net.Uri.parse(
                    "baidumap://map/direction?destination=${destination.latitude},${destination.longitude}&mode=driving&src=reding"
                )
            }

            // 检查是否安装了百度地图
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                Log.i(TAG, "启动百度地图导航到: ${formatCoordinate(destination)}")
            } else {
                // 如果没有百度地图，使用系统默认地图应用
                val fallbackIntent = Intent().apply {
                    data = android.net.Uri.parse(
                        "geo:${destination.latitude},${destination.longitude}?q=${destination.latitude},${destination.longitude}"
                    )
                }
                context.startActivity(fallbackIntent)
                Log.i(TAG, "使用系统地图导航到: ${formatCoordinate(destination)}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "启动导航失败", e)
        }
    }
    
    /**
     * 添加位置标记（临时简化版本）
     */
    private fun addLocationMarker(latLng: LatLng) {
        // 临时注释标记操作
        Log.i(TAG, "添加位置标记: ${formatCoordinate(latLng)}")
    }

    /**
     * 执行反地理编码（临时简化版本）
     */
    private fun performReverseGeocode(latLng: LatLng) {
        // 临时注释反地理编码
        Log.i(TAG, "执行反地理编码: ${formatCoordinate(latLng)}")
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
    
    // 临时注释监听器，等添加百度地图SDK后启用
    // private val geocodeListener = ...
    // private val poiListener = ...
    
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
     * 清理资源（临时简化版本）
     */
    fun cleanup() {
        // 临时注释资源清理
        // geocodeSearch?.destroy()
        // poiSearch?.destroy()
        // currentMarker?.remove()

        Log.i(TAG, "地图交互管理器已清理")
    }
}
