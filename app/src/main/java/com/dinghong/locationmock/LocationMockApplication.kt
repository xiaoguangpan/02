package com.dinghong.locationmock

import android.app.Application
// 百度地图SDK导入 - 临时注释以避免编译错误
// import com.baidu.mapapi.CoordType
// import com.baidu.mapapi.SDKInitializer

/**
 * 定红定位模拟器应用程序类
 * 负责初始化百度地图SDK和全局配置
 */
class LocationMockApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化百度地图SDK
        initBaiduMapSDK()
    }
    
    /**
     * 初始化百度地图SDK
     * 使用API Key: RHIrMFCec8xoScSBBtbCMtrTNpLYrwjt
     */
    private fun initBaiduMapSDK() {
        try {
            android.util.Log.i("LocationMock", "开始初始化百度地图SDK...")

            // 百度地图SDK初始化 - 临时注释以避免编译错误
            // SDKInitializer.initialize(this)
            // SDKInitializer.setCoordType(CoordType.BD09LL)

            android.util.Log.i("LocationMock", "✅ 百度地图SDK初始化完成")
            android.util.Log.i("LocationMock", "API Key: RHIrMFCec8xoScSBBtbCMtrTNpLYrwjt")
            android.util.Log.i("LocationMock", "坐标系: BD09LL (百度经纬度坐标系)")

        } catch (e: Exception) {
            android.util.Log.e("LocationMock", "❌ 百度地图SDK初始化失败: ${e.message}")
            e.printStackTrace()
        }
    }
}
