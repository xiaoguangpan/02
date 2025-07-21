package com.dinghong.locationmock.manager

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import com.dinghong.locationmock.service.LocationMockService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 位置模拟管理器
 * 负责管理位置模拟的状态和操作
 */
class LocationMockManager(private val context: Context) {
    
    companion object {
        private const val TAG = "LocationMockManager"
    }
    
    private val _isSimulating = MutableStateFlow(false)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation.asStateFlow()
    
    private val _simulationMode = MutableStateFlow(SimulationMode.STANDARD)
    val simulationMode: StateFlow<SimulationMode> = _simulationMode.asStateFlow()
    
    /**
     * 模拟模式枚举
     */
    enum class SimulationMode {
        STANDARD,   // 标准模式
        ENHANCED    // 增强模式
    }
    
    /**
     * 检查是否具有模拟位置权限
     */
    fun checkMockLocationPermission(): Boolean {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // 尝试添加测试提供者来检查权限
            locationManager.addTestProvider(
                "test_provider",
                false, false, false, false, false, false, false,
                android.location.Criteria.POWER_LOW,
                android.location.Criteria.ACCURACY_COARSE
            )
            locationManager.removeTestProvider("test_provider")
            true
        } catch (e: SecurityException) {
            Log.w(TAG, "缺少模拟位置权限: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "检查模拟位置权限时出错: ${e.message}")
            false
        }
    }
    
    /**
     * 检查开发者选项中的模拟位置设置
     */
    fun checkMockLocationSetting(): Boolean {
        return try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ALLOW_MOCK_LOCATION,
                0
            ) == 1
        } catch (e: Exception) {
            // Android 6.0以上版本不再需要此设置
            true
        }
    }
    
    /**
     * 开始位置模拟
     */
    fun startLocationMock(
        latitude: Double,
        longitude: Double,
        enhancedMode: Boolean = false
    ): Boolean {
        if (_isSimulating.value) {
            Log.w(TAG, "位置模拟已在运行中")
            return false
        }
        
        if (!checkMockLocationPermission()) {
            Log.e(TAG, "缺少模拟位置权限，无法启动模拟")
            return false
        }
        
        try {
            val intent = Intent(context, LocationMockService::class.java).apply {
                action = LocationMockService.ACTION_START_MOCK
                putExtra(LocationMockService.EXTRA_LATITUDE, latitude)
                putExtra(LocationMockService.EXTRA_LONGITUDE, longitude)
                putExtra(LocationMockService.EXTRA_ENHANCED_MODE, enhancedMode)
            }
            
            context.startService(intent)
            
            _isSimulating.value = true
            _currentLocation.value = Pair(latitude, longitude)
            _simulationMode.value = if (enhancedMode) SimulationMode.ENHANCED else SimulationMode.STANDARD
            
            Log.i(TAG, "位置模拟已启动: ($latitude, $longitude), 模式: ${if (enhancedMode) "增强" else "标准"}")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "启动位置模拟失败: ${e.message}")
            return false
        }
    }
    
    /**
     * 停止位置模拟
     */
    fun stopLocationMock(): Boolean {
        if (!_isSimulating.value) {
            Log.w(TAG, "位置模拟未在运行")
            return false
        }
        
        try {
            val intent = Intent(context, LocationMockService::class.java).apply {
                action = LocationMockService.ACTION_STOP_MOCK
            }
            
            context.startService(intent)
            
            _isSimulating.value = false
            _currentLocation.value = null
            
            Log.i(TAG, "位置模拟已停止")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "停止位置模拟失败: ${e.message}")
            return false
        }
    }
    
    /**
     * 切换模拟模式
     */
    fun toggleSimulationMode() {
        val newMode = if (_simulationMode.value == SimulationMode.STANDARD) {
            SimulationMode.ENHANCED
        } else {
            SimulationMode.STANDARD
        }
        
        _simulationMode.value = newMode
        
        // 如果正在模拟，重启服务以应用新模式
        if (_isSimulating.value) {
            _currentLocation.value?.let { (lat, lng) ->
                stopLocationMock()
                startLocationMock(lat, lng, newMode == SimulationMode.ENHANCED)
            }
        }
        
        Log.i(TAG, "模拟模式已切换为: ${if (newMode == SimulationMode.ENHANCED) "增强" else "标准"}")
    }
    
    /**
     * 获取当前模拟状态信息
     */
    fun getSimulationStatus(): String {
        return if (_isSimulating.value) {
            val location = _currentLocation.value
            val mode = _simulationMode.value
            "正在模拟: ${location?.first ?: "N/A"}, ${location?.second ?: "N/A"} (${if (mode == SimulationMode.ENHANCED) "增强" else "标准"}模式)"
        } else {
            "未在模拟"
        }
    }
    
    /**
     * 验证坐标有效性
     */
    fun validateCoordinates(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }
    
    /**
     * 格式化坐标显示
     */
    fun formatCoordinates(latitude: Double, longitude: Double): String {
        return String.format("%.6f, %.6f", latitude, longitude)
    }
}
