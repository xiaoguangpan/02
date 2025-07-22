package com.dinghong.locationmock.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
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
import com.dinghong.locationmock.data.FavoriteLocation
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
        val favoriteLocations: List<FavoriteLocation> = emptyList()
    )
    
    /**
     * 初始化ViewModel
     */
    fun initialize(context: Context) {
        locationMockManager = LocationMockManager(context)
        mapInteractionManager = MapInteractionManager(context)
        favoriteManager = FavoriteManager(context)
        
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
        
        addDebugLog("定红定位模拟器已启动")

        // 应用启动时自动获取当前位置
        getCurrentLocationSilently()
    }
    
    /**
     * 地图准备完成
     */
    fun onMapReady(baiduMap: BaiduMap) {
        mapInteractionManager.initializeMap(baiduMap)
        addDebugLog("百度地图初始化完成")
    }
    
    /**
     * 地图点击事件
     */
    fun onMapClick(latLng: LatLng) {
        mapInteractionManager.onMapClick(latLng)
        
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
    }
    
    /**
     * 执行搜索
     */
    fun performSearch() {
        val searchText = _uiState.value.searchText
        if (searchText.isNotBlank()) {
            mapInteractionManager.searchAddress(searchText)
            addDebugLog("搜索: $searchText")
        }
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
                    _uiState.value.isEnhancedMode
                )) {
                val mode = if (_uiState.value.isEnhancedMode) "增强模式" else "标准模式"
                addDebugLog("位置模拟已启动 ($mode)", "SUCCESS")
            } else {
                addDebugLog("启动位置模拟失败，请检查权限设置", "ERROR")
            }
        }
    }
    
    /**
     * 显示添加收藏对话框
     */
    fun showAddFavoriteDialog() {
        if (_uiState.value.selectedLocation != null) {
            _uiState.value = _uiState.value.copy(showAddFavoriteDialog = true)
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
        addDebugLog("已删除收藏")
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
    
    override fun onCleared() {
        super.onCleared()
        mapInteractionManager.cleanup()
        addDebugLog("ViewModel已清理")
    }
}
