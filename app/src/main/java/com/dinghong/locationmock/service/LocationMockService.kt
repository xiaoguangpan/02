package com.dinghong.locationmock.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.*
import kotlin.random.Random

/**
 * 定位模拟服务
 * 负责执行位置模拟的核心逻辑，支持标准模式和增强模式
 */
class LocationMockService : Service() {
    
    companion object {
        private const val TAG = "LocationMockService"
        const val ACTION_START_MOCK = "com.dinghong.locationmock.START_MOCK"
        const val ACTION_STOP_MOCK = "com.dinghong.locationmock.STOP_MOCK"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
        const val EXTRA_ENHANCED_MODE = "enhanced_mode"
    }
    
    private var locationManager: LocationManager? = null
    private var mockJob: Job? = null
    private var isRunning = false
    
    // 模拟参数
    private var targetLatitude = 0.0
    private var targetLongitude = 0.0
    private var isEnhancedMode = false
    
    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Log.i(TAG, "定位模拟服务已创建")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_MOCK -> {
                targetLatitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0)
                targetLongitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)
                isEnhancedMode = intent.getBooleanExtra(EXTRA_ENHANCED_MODE, false)
                startLocationMock()
            }
            ACTION_STOP_MOCK -> {
                stopLocationMock()
            }
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    /**
     * 开始位置模拟
     */
    private fun startLocationMock() {
        if (isRunning) {
            Log.w(TAG, "位置模拟已在运行中")
            return
        }
        
        try {
            // 启用模拟位置提供者
            enableMockLocationProvider()
            
            isRunning = true
            
            // 启动模拟协程
            mockJob = CoroutineScope(Dispatchers.IO).launch {
                if (isEnhancedMode) {
                    startEnhancedMock()
                } else {
                    startStandardMock()
                }
            }
            
            Log.i(TAG, "位置模拟已启动 - 模式: ${if (isEnhancedMode) "增强" else "标准"}")
            
        } catch (e: Exception) {
            Log.e(TAG, "启动位置模拟失败: ${e.message}")
            isRunning = false
        }
    }
    
    /**
     * 停止位置模拟
     */
    private fun stopLocationMock() {
        if (!isRunning) {
            Log.w(TAG, "位置模拟未在运行")
            return
        }
        
        try {
            // 取消模拟协程
            mockJob?.cancel()
            mockJob = null
            
            // 禁用模拟位置提供者
            disableMockLocationProvider()
            
            isRunning = false
            Log.i(TAG, "位置模拟已停止")
            
        } catch (e: Exception) {
            Log.e(TAG, "停止位置模拟失败: ${e.message}")
        }
    }
    
    /**
     * 启用模拟位置提供者
     */
    private fun enableMockLocationProvider() {
        locationManager?.let { lm ->
            try {
                // 为GPS和NETWORK提供者启用模拟位置
                listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).forEach { provider ->
                    if (!lm.isProviderEnabled(provider)) {
                        lm.addTestProvider(
                            provider,
                            false, false, false, false, true, true, true,
                            android.location.Criteria.POWER_MEDIUM,
                            android.location.Criteria.ACCURACY_FINE
                        )
                        lm.setTestProviderEnabled(provider, true)
                    }
                }
                Log.i(TAG, "模拟位置提供者已启用")
            } catch (e: SecurityException) {
                Log.e(TAG, "启用模拟位置提供者失败，请检查权限: ${e.message}")
                throw e
            }
        }
    }
    
    /**
     * 禁用模拟位置提供者
     */
    private fun disableMockLocationProvider() {
        locationManager?.let { lm ->
            try {
                listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).forEach { provider ->
                    try {
                        lm.setTestProviderEnabled(provider, false)
                        lm.removeTestProvider(provider)
                    } catch (e: Exception) {
                        // 忽略移除失败的错误
                    }
                }
                Log.i(TAG, "模拟位置提供者已禁用")
            } catch (e: Exception) {
                Log.e(TAG, "禁用模拟位置提供者失败: ${e.message}")
            }
        }
    }
    
    /**
     * 标准模式模拟
     * 100ms更新间隔，固定坐标
     */
    private suspend fun startStandardMock() {
        while (isRunning) {
            try {
                setMockLocation(targetLatitude, targetLongitude)
                delay(100) // 100ms更新间隔
            } catch (e: Exception) {
                Log.e(TAG, "标准模式模拟失败: ${e.message}")
                break
            }
        }
    }
    
    /**
     * 增强模式模拟
     * 50ms更新间隔，微坐标抖动，动态精度
     */
    private suspend fun startEnhancedMock() {
        while (isRunning) {
            try {
                // 生成微坐标抖动（5米范围内）
                val jitterLat = generateCoordinateJitter()
                val jitterLng = generateCoordinateJitter()
                
                val mockLat = targetLatitude + jitterLat
                val mockLng = targetLongitude + jitterLng
                
                setMockLocation(mockLat, mockLng, generateDynamicAccuracy())
                delay(50) // 50ms更新间隔
            } catch (e: Exception) {
                Log.e(TAG, "增强模式模拟失败: ${e.message}")
                break
            }
        }
    }
    
    /**
     * 设置模拟位置
     */
    private fun setMockLocation(latitude: Double, longitude: Double, accuracy: Float = 10f) {
        locationManager?.let { lm ->
            try {
                val location = Location(LocationManager.GPS_PROVIDER).apply {
                    this.latitude = latitude
                    this.longitude = longitude
                    this.accuracy = accuracy
                    this.time = System.currentTimeMillis()
                    this.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                    this.altitude = 0.0
                    this.bearing = 0f
                    this.speed = 0f
                }
                
                // 同时为GPS和NETWORK提供者设置位置
                lm.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
                
                val networkLocation = Location(LocationManager.NETWORK_PROVIDER).apply {
                    this.latitude = latitude
                    this.longitude = longitude
                    this.accuracy = accuracy + 5f // 网络定位精度稍低
                    this.time = System.currentTimeMillis()
                    this.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                }
                
                lm.setTestProviderLocation(LocationManager.NETWORK_PROVIDER, networkLocation)
                
            } catch (e: Exception) {
                Log.e(TAG, "设置模拟位置失败: ${e.message}")
            }
        }
    }
    
    /**
     * 生成坐标抖动（约5米范围）
     */
    private fun generateCoordinateJitter(): Double {
        // 1度约等于111km，5米约等于0.000045度
        return (Random.nextDouble() - 0.5) * 0.00009
    }
    
    /**
     * 生成动态精度值
     */
    private fun generateDynamicAccuracy(): Float {
        return Random.nextFloat() * 10f + 5f // 5-15米精度范围
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopLocationMock()
        Log.i(TAG, "定位模拟服务已销毁")
    }
}
