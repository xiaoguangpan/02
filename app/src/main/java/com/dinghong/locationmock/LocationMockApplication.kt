package com.dinghong.locationmock

import android.app.Application
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
     * TODO: 需要添加百度地图SDK依赖后启用
     */
    private fun initBaiduMapSDK() {
        try {
            // 临时注释百度地图SDK初始化，等添加依赖后启用
            // SDKInitializer.initialize(this)
            // SDKInitializer.setCoordType(CoordType.BD09LL)

            // 记录初始化日志
            android.util.Log.i("LocationMock", "百度地图SDK初始化已跳过（待添加依赖）")

        } catch (e: Exception) {
            android.util.Log.e("LocationMock", "百度地图SDK初始化失败: ${e.message}")
        }
    }
}
