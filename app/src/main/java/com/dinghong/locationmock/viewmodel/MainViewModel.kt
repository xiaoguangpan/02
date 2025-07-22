package com.dinghong.locationmock.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// 临时注释百度地图SDK导入
// import com.baidu.mapapi.map.BaiduMap
// import com.baidu.mapapi.model.LatLng

// 使用本地定义的数据类
import com.dinghong.locationmock.manager.LatLng
import com.dinghong.locationmock.manager.BaiduMap
import com.dinghong.locationmock.manager.LocationMockManager
import com.dinghong.locationmock.manager.MapInteractionManager
import com.dinghong.locationmock.manager.FavoriteManager
import com.dinghong.locationmock.manager.PermissionManager
import com.dinghong.locationmock.data.FavoriteLocation
import com.dinghong.locationmock.manager.SearchResultItem
import com.dinghong.locationmock.utils.PermissionHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 主界面ViewModel
 * 管理UI状态和业务逻辑
 */
class MainViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "MainViewModel"
    }
    
    private lateinit var locationMockManager: LocationMockManager
    private lateinit var mapInteractionManager: MapInteractionManager
    private lateinit var favoriteManager: FavoriteManager
    private var permissionManager: PermissionManager? = null
    private lateinit var context: Context
    
    // UI状态
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    // 调试日志
    private val _debugLogs = MutableStateFlow<List<String>>(emptyList())
    
    /**
     * 主界面UI状态
     */
    data class MainUiState(
        val searchText: String = "",
        val currentCoordinate: String = "",
        val isSimulating: Boolean = false,
        val showDebugPanel: Boolean = false,
        val showHelpDialog: Boolean = false,
        val showAddFavoriteDialog: Boolean = false,
        val showFavoriteListDialog: Boolean = false,
        val debugLogs: List<String> = emptyList(),
        val selectedLocation: LatLng? = null,
        val isSearching: Boolean = false,
        val hasLocationPermission: Boolean = false,
        val hasMockLocationPermission: Boolean = false,
        val favoriteLocations: List<FavoriteLocation> = emptyList(),
        val searchSuggestions: List<SearchResultItem> = emptyList(),
        val showPermissionErrorDialog: Boolean = false,
        val permissionErrorTitle: String = "",
        val permissionErrorMessage: String = ""
    )
    
    /**
     * 初始化ViewModel
     */
    fun initialize(context: Context, permissionManager: PermissionManager? = null) {
        this.context = context
        locationMockManager = LocationMockManager(context)
        mapInteractionManager = MapInteractionManager(context)
        favoriteManager = FavoriteManager(context)
        this.permissionManager = permissionManager
        
        // 检查权限状态
        checkPermissions(context)
        
        // 监听位置模拟状态
        viewModelScope.launch {
            locationMockManager.isSimulating.collect { isSimulating ->
                _uiState.value = _uiState.value.copy(isSimulating = isSimulating)
                addDebugLog("位置模拟状态: ${if (isSimulating) "已启动" else "已停止"}")
            }
        }
        
        // 监听当前位置
        viewModelScope.launch {
            locationMockManager.currentLocation.collect { location ->
                location?.let { (lat, lng) ->
                    val coordinate = locationMockManager.formatCoordinates(lat, lng)
                    _uiState.value = _uiState.value.copy(currentCoordinate = coordinate)
                }
            }
        }
        
        // 监听地图选择的位置
        viewModelScope.launch {
            mapInteractionManager.selectedLocation.collect { location ->
                _uiState.value = _uiState.value.copy(selectedLocation = location)
                location?.let {
                    val coordinate = String.format("%.6f, %.6f", it.latitude, it.longitude)
                    _uiState.value = _uiState.value.copy(currentCoordinate = coordinate)
                    addDebugLog("选择位置: $coordinate")
                }
            }
        }
        
        // 监听搜索状态
        viewModelScope.launch {
            mapInteractionManager.isSearching.collect { isSearching ->
                _uiState.value = _uiState.value.copy(isSearching = isSearching)
            }
        }

        // 监听收藏列表
        viewModelScope.launch {
            favoriteManager.favoriteLocations.collect { favorites ->
                _uiState.value = _uiState.value.copy(favoriteLocations = favorites)
            }
        }

        // 监听搜索结果
        viewModelScope.launch {
            mapInteractionManager.searchResults.collect { results ->
                _uiState.value = _uiState.value.copy(searchSuggestions = results)
            }
        }

        // 监听权限状态
        permissionManager?.let { manager ->
            viewModelScope.launch {
                manager.hasLocationPermission.collect { hasPermission ->
                    _uiState.value = _uiState.value.copy(hasLocationPermission = hasPermission)
                }
            }
            viewModelScope.launch {
                manager.hasMockLocationPermission.collect { hasPermission ->
                    _uiState.value = _uiState.value.copy(hasMockLocationPermission = hasPermission)
                }
            }
        }
        
        addDebugLog("定红定位模拟器已启动", "SUCCESS")
        addDebugLog("正在初始化地图组件...", "INFO")

        // 设置默认位置（北京天安门）
        val defaultLocation = LatLng(39.904200, 116.407400)
        _uiState.value = _uiState.value.copy(
            selectedLocation = defaultLocation,
            currentCoordinate = formatCoordinate(defaultLocation)
        )
        addDebugLog("设置默认位置: ${defaultLocation.latitude}, ${defaultLocation.longitude}", "INFO")

        // 应用启动时自动获取当前位置
        getCurrentLocationSilently()
    }
    
    /**
     * 地图准备完成
     */
    fun onMapReady(baiduMap: BaiduMap) {
        mapInteractionManager.initializeMap(baiduMap)
        addDebugLog("地图组件初始化完成", "SUCCESS")
        addDebugLog("可以点击地图选择位置或输入坐标", "INFO")
    }
    
    /**
     * 地图点击事件
     */
    fun onMapClick(latLng: LatLng) {
        mapInteractionManager.onMapClick(latLng)

        // 更新UI状态
        _uiState.value = _uiState.value.copy(
            selectedLocation = latLng,
            currentCoordinate = formatCoordinate(latLng)
        )

        addDebugLog("选择位置: ${String.format("%.6f", latLng.latitude)}, ${String.format("%.6f", latLng.longitude)}", "SUCCESS")
        addDebugLog("现在可以点击'开始模拟'按钮", "INFO")

        // 显示坐标转换信息
        val conversions = mapInteractionManager.getCoordinateConversions(latLng)
        conversions.forEach { (system, coordinate) ->
            addDebugLog("$system: $coordinate", "COORDINATE")
        }
    }
    
    /**
     * 更新搜索文本
     */
    fun updateSearchText(text: String) {
        _uiState.value = _uiState.value.copy(searchText = text)
        // 实时搜索建议
        if (text.isNotBlank() && text.length >= 2) {
            mapInteractionManager.searchAddress(text)
        } else {
            // 清空搜索建议
            _uiState.value = _uiState.value.copy(searchSuggestions = emptyList())
        }
    }
    
    /**
     * 执行搜索
     */
    fun performSearch() {
        val searchText = _uiState.value.searchText.trim()
        if (searchText.isNotBlank()) {
            // 检查是否为坐标格式 (纬度,经度)
            val coordinatePattern = Regex("""^(-?\d+\.?\d*)\s*,\s*(-?\d+\.?\d*)$""")
            val matchResult = coordinatePattern.find(searchText)

            if (matchResult != null) {
                // 处理坐标输入
                try {
                    val lat = matchResult.groupValues[1].toDouble()
                    val lng = matchResult.groupValues[2].toDouble()

                    if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                        val latLng = LatLng(lat, lng)
                        mapInteractionManager.moveToLocation(latLng)
                        _uiState.value = _uiState.value.copy(
                            searchText = "",
                            searchSuggestions = emptyList()
                        )
                        addDebugLog("移动到坐标: $lat, $lng")
                    } else {
                        addDebugLog("坐标超出有效范围")
                    }
                } catch (e: NumberFormatException) {
                    addDebugLog("坐标格式错误")
                }
            } else {
                // 处理地址搜索
                mapInteractionManager.searchAddress(searchText)
                addDebugLog("搜索地址: $searchText")
            }
        }
    }

    /**
     * 处理搜索建议点击
     */
    fun onSearchSuggestionClick(suggestion: SearchResultItem) {
        // 移动到选中位置
        mapInteractionManager.moveToLocation(suggestion.location)
        // 清空搜索文本和建议
        _uiState.value = _uiState.value.copy(
            searchText = "",
            searchSuggestions = emptyList()
        )
        addDebugLog("选择搜索建议: ${suggestion.name}")
    }
    
    /**
     * 获取当前位置
     */
    fun getCurrentLocation() {
        mapInteractionManager.getCurrentLocation()
        addDebugLog("获取当前位置")
    }

    /**
     * 静默获取当前位置（不显示日志）
     */
    private fun getCurrentLocationSilently() {
        mapInteractionManager.getCurrentLocation()
    }
    
    /**
     * 切换位置模拟
     */
    fun toggleSimulation() {
        val selectedLocation = _uiState.value.selectedLocation
        if (selectedLocation == null) {
            addDebugLog("请先选择要模拟的位置", "ERROR")
            return
        }
        
        if (_uiState.value.isSimulating) {
            // 停止模拟
            if (locationMockManager.stopLocationMock()) {
                addDebugLog("位置模拟已停止", "SUCCESS")
            } else {
                addDebugLog("停止位置模拟失败", "ERROR")
            }
        } else {
            // 开始模拟
            if (locationMockManager.startLocationMock(
                    selectedLocation.latitude,
                    selectedLocation.longitude,
                    false // 使用标准模式
                )) {
                addDebugLog("位置模拟已启动", "SUCCESS")
            } else {
                addDebugLog("启动位置模拟失败，请检查权限设置", "ERROR")
                showPermissionError()
            }
        }
    }
    
    /**
     * 直接添加收藏（简化流程）
     */
    fun showAddFavoriteDialog() {
        val selectedLocation = _uiState.value.selectedLocation
        if (selectedLocation != null) {
            // 自动生成收藏名称
            val timestamp = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            val name = "位置 $timestamp"
            val address = "坐标: ${String.format("%.6f, %.6f", selectedLocation.latitude, selectedLocation.longitude)}"

            favoriteManager.addFavorite(name, address, selectedLocation)
            Toast.makeText(context, "收藏成功!", Toast.LENGTH_SHORT).show()
            addDebugLog("已添加收藏: $name")
        } else {
            addDebugLog("请先选择位置")
        }
    }

    /**
     * 隐藏添加收藏对话框
     */
    fun hideAddFavoriteDialog() {
        _uiState.value = _uiState.value.copy(showAddFavoriteDialog = false)
    }

    /**
     * 添加收藏
     */
    fun addFavorite(name: String, address: String) {
        val selectedLocation = _uiState.value.selectedLocation
        if (selectedLocation != null) {
            favoriteManager.addFavorite(name, address, selectedLocation)
            _uiState.value = _uiState.value.copy(showAddFavoriteDialog = false)
            addDebugLog("已添加收藏: $name")
        }
    }

    /**
     * 显示收藏列表对话框
     */
    fun showFavoriteListDialog() {
        _uiState.value = _uiState.value.copy(showFavoriteListDialog = true)
    }

    /**
     * 隐藏收藏列表对话框
     */
    fun hideFavoriteListDialog() {
        _uiState.value = _uiState.value.copy(showFavoriteListDialog = false)
    }

    /**
     * 选择收藏位置
     */
    fun selectFavoriteLocation(favorite: FavoriteLocation) {
        mapInteractionManager.moveToLocation(favorite.latLng)
        _uiState.value = _uiState.value.copy(showFavoriteListDialog = false)
        addDebugLog("选择收藏位置: ${favorite.name}")
    }

    /**
     * 删除收藏
     */
    fun deleteFavorite(favoriteId: String) {
        favoriteManager.removeFavorite(favoriteId)
        Toast.makeText(context, "删除成功!", Toast.LENGTH_SHORT).show()
        addDebugLog("已删除收藏")
    }

    /**
     * 显示权限错误提示
     */
    private fun showPermissionError() {
        val hasLocationPermission = _uiState.value.hasLocationPermission
        val hasMockPermission = _uiState.value.hasMockLocationPermission

        val (title, message) = when {
            !hasLocationPermission && !hasMockPermission -> {
                "权限不足" to "需要位置权限和模拟位置权限才能开始位置模拟。\n\n请点击右上角的权限图标进行设置。"
            }
            !hasLocationPermission -> {
                "缺少位置权限" to "需要位置权限才能开始位置模拟。\n\n请点击右上角的位置权限图标进行设置。"
            }
            !hasMockPermission -> {
                "缺少模拟位置权限" to "需要在开发者选项中设置本应用为模拟位置应用。\n\n请点击右上角的模拟权限图标进行设置。"
            }
            else -> {
                "模拟失败" to "位置模拟启动失败，请检查权限设置或重试。"
            }
        }

        _uiState.value = _uiState.value.copy(
            showPermissionErrorDialog = true,
            permissionErrorTitle = title,
            permissionErrorMessage = message
        )
    }

    /**
     * 隐藏权限错误对话框
     */
    fun hidePermissionErrorDialog() {
        _uiState.value = _uiState.value.copy(showPermissionErrorDialog = false)
    }

    /**
     * 处理位置权限点击
     */
    fun onLocationPermissionClick() {
        permissionManager?.openAppSettings()
        addDebugLog("打开应用设置页面")
    }

    /**
     * 处理模拟权限点击
     */
    fun onMockPermissionClick() {
        permissionManager?.openDeveloperSettings()
        addDebugLog("打开开发者选项页面")
    }
    
    /**
     * 显示/隐藏调试面板
     */
    fun toggleDebugPanel() {
        val showPanel = !_uiState.value.showDebugPanel
        _uiState.value = _uiState.value.copy(showDebugPanel = showPanel)
        addDebugLog("${if (showPanel) "显示" else "隐藏"}调试面板")
    }
    
    fun hideDebugPanel() {
        _uiState.value = _uiState.value.copy(showDebugPanel = false)
    }
    
    /**
     * 显示帮助
     */
    fun showHelp() {
        _uiState.value = _uiState.value.copy(showHelpDialog = true)
        addDebugLog("显示帮助信息")
    }

    /**
     * 隐藏帮助对话框
     */
    fun hideHelpDialog() {
        _uiState.value = _uiState.value.copy(showHelpDialog = false)
    }
    
    /**
     * 启动导航
     */
    fun startNavigation() {
        val selectedLocation = _uiState.value.selectedLocation
        if (selectedLocation != null) {
            mapInteractionManager.startNavigation(selectedLocation)
            addDebugLog("启动导航到: ${String.format("%.6f, %.6f", selectedLocation.latitude, selectedLocation.longitude)}")
        } else {
            addDebugLog("请先选择目标位置")
        }
    }
    
    /**
     * 放大地图
     */
    fun zoomIn() {
        mapInteractionManager.zoomIn()
        addDebugLog("地图放大")
    }
    
    /**
     * 缩小地图
     */
    fun zoomOut() {
        mapInteractionManager.zoomOut()
        addDebugLog("地图缩小")
    }
    
    /**
     * 清除调试日志
     */
    fun clearDebugLogs() {
        _debugLogs.value = emptyList()
        _uiState.value = _uiState.value.copy(debugLogs = emptyList())
        addDebugLog("调试日志已清除")
    }
    
    /**
     * 复制调试日志
     */
    fun copyDebugLogs(context: Context) {
        val logs = _uiState.value.debugLogs.joinToString("\n")
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("调试日志", logs)
        clipboard.setPrimaryClip(clip)
        addDebugLog("调试日志已复制到剪贴板")
    }
    
    /**
     * 检查权限状态
     */
    private fun checkPermissions(context: Context) {
        val hasLocationPermission = PermissionHelper.hasLocationPermissions(context)
        val hasMockLocationPermission = locationMockManager.checkMockLocationPermission()
        
        _uiState.value = _uiState.value.copy(
            hasLocationPermission = hasLocationPermission,
            hasMockLocationPermission = hasMockLocationPermission
        )
        
        if (!hasLocationPermission) {
            addDebugLog("缺少位置权限", "ERROR")
        }
        
        if (!hasMockLocationPermission) {
            addDebugLog("缺少模拟位置权限，请在开发者选项中启用", "ERROR")
        }
    }
    
    /**
     * 添加调试日志
     */
    private fun addDebugLog(message: String, type: String = "INFO") {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val logEntry = "[$timestamp] [$type] $message"
        
        val currentLogs = _debugLogs.value.toMutableList()
        currentLogs.add(0, logEntry) // 添加到顶部
        
        // 限制日志数量
        if (currentLogs.size > 100) {
            currentLogs.removeAt(currentLogs.size - 1)
        }
        
        _debugLogs.value = currentLogs
        _uiState.value = _uiState.value.copy(debugLogs = currentLogs)
        
        // 同时输出到系统日志
        when (type) {
            "ERROR" -> Log.e(TAG, message)
            "SUCCESS" -> Log.i(TAG, message)
            "COORDINATE" -> Log.d(TAG, message)
            else -> Log.i(TAG, message)
        }
    }
    
    /**
     * 格式化坐标显示
     */
    private fun formatCoordinate(latLng: LatLng): String {
        return "${String.format("%.6f", latLng.latitude)}, ${String.format("%.6f", latLng.longitude)}"
    }

    override fun onCleared() {
        super.onCleared()
        mapInteractionManager.cleanup()
        addDebugLog("ViewModel已清理")
    }
}
