package com.dinghong.locationmock

import android.app.Application
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer

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
     */
    private fun initBaiduMapSDK() {
        try {
            // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
            SDKInitializer.initialize(this)
            
            // 自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标
            // 用此方法设置您使用的坐标类型.
            // 包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
            SDKInitializer.setCoordType(CoordType.BD09LL)
            
            // 记录初始化成功日志
            android.util.Log.i("LocationMock", "百度地图SDK初始化成功")
            
        } catch (e: Exception) {
            android.util.Log.e("LocationMock", "百度地图SDK初始化失败: ${e.message}")
        }
    }
}
