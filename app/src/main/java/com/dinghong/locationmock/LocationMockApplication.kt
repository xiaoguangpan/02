package com.dinghong.locationmock

import android.app.Application
// 百度地图SDK导入 - 临时注释，寻找更好的集成方案
// import com.baidu.mapapi.CoordType
// import com.baidu.mapapi.SDKInitializer
// import com.baidu.mapapi.common.BaiduMapSDKException

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

            // 百度地图SDK初始化 - 临时注释，寻找更好的集成方案
            // SDKInitializer.setAgreePrivacy(this, true)
            // try {
            //     SDKInitializer.initialize(this)
            //     SDKInitializer.setCoordType(CoordType.BD09LL)
            //     android.util.Log.i("LocationMock", "✅ 百度地图SDK初始化完成")
            // } catch (e: BaiduMapSDKException) {
            //     android.util.Log.e("LocationMock", "❌ 百度地图SDK初始化失败: ${e.message}")
            //     e.printStackTrace()
            // }

            android.util.Log.i("LocationMock", "⚠️ 百度地图SDK初始化已跳过，寻找更好的集成方案")
            android.util.Log.i("LocationMock", "API Key: RHIrMFCec8xoScSBBtbCMtrTNpLYrwjt")
            android.util.Log.i("LocationMock", "坐标系: BD09LL (百度经纬度坐标系)")

        } catch (e: Exception) {
            android.util.Log.e("LocationMock", "❌ 百度地图SDK初始化失败: ${e.message}")
            e.printStackTrace()
        }
    }
}
