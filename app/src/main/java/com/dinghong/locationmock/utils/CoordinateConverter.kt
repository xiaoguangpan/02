package com.dinghong.locationmock.utils

// 临时注释百度地图SDK导入
// import com.baidu.mapapi.model.LatLng
// import com.baidu.mapapi.utils.CoordinateConverter

// 临时数据类定义
data class LatLng(val latitude: Double, val longitude: Double)
import kotlin.math.*

/**
 * 坐标系转换工具类
 * 支持BD09LL(百度坐标)、GCJ02(火星坐标)、WGS84(GPS坐标)之间的转换
 */
object CoordinateConverter {
    
    private const val PI = 3.1415926535897932384626
    private const val A = 6378245.0
    private const val EE = 0.00669342162296594323
    
    /**
     * 百度坐标转WGS84坐标
     */
    fun bd09ToWgs84(bdLat: Double, bdLng: Double): Pair<Double, Double> {
        // 先转换为GCJ02
        val gcj02 = bd09ToGcj02(bdLat, bdLng)
        // 再转换为WGS84
        return gcj02ToWgs84(gcj02.first, gcj02.second)
    }
    
    /**
     * WGS84坐标转百度坐标
     */
    fun wgs84ToBd09(wgsLat: Double, wgsLng: Double): Pair<Double, Double> {
        // 先转换为GCJ02
        val gcj02 = wgs84ToGcj02(wgsLat, wgsLng)
        // 再转换为BD09
        return gcj02ToBd09(gcj02.first, gcj02.second)
    }
    
    /**
     * 百度坐标转火星坐标
     */
    fun bd09ToGcj02(bdLat: Double, bdLng: Double): Pair<Double, Double> {
        val x = bdLng - 0.0065
        val y = bdLat - 0.006
        val z = sqrt(x * x + y * y) - 0.00002 * sin(y * PI)
        val theta = atan2(y, x) - 0.000003 * cos(x * PI)
        val gcjLng = z * cos(theta)
        val gcjLat = z * sin(theta)
        return Pair(gcjLat, gcjLng)
    }
    
    /**
     * 火星坐标转百度坐标
     */
    fun gcj02ToBd09(gcjLat: Double, gcjLng: Double): Pair<Double, Double> {
        val z = sqrt(gcjLng * gcjLng + gcjLat * gcjLat) + 0.00002 * sin(gcjLat * PI)
        val theta = atan2(gcjLat, gcjLng) + 0.000003 * cos(gcjLng * PI)
        val bdLng = z * cos(theta) + 0.0065
        val bdLat = z * sin(theta) + 0.006
        return Pair(bdLat, bdLng)
    }
    
    /**
     * WGS84坐标转火星坐标
     */
    fun wgs84ToGcj02(wgsLat: Double, wgsLng: Double): Pair<Double, Double> {
        if (outOfChina(wgsLat, wgsLng)) {
            return Pair(wgsLat, wgsLng)
        }
        
        var dLat = transformLat(wgsLng - 105.0, wgsLat - 35.0)
        var dLng = transformLng(wgsLng - 105.0, wgsLat - 35.0)
        val radLat = wgsLat / 180.0 * PI
        var magic = sin(radLat)
        magic = 1 - EE * magic * magic
        val sqrtMagic = sqrt(magic)
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI)
        dLng = (dLng * 180.0) / (A / sqrtMagic * cos(radLat) * PI)
        val mgLat = wgsLat + dLat
        val mgLng = wgsLng + dLng
        return Pair(mgLat, mgLng)
    }
    
    /**
     * 火星坐标转WGS84坐标
     */
    fun gcj02ToWgs84(gcjLat: Double, gcjLng: Double): Pair<Double, Double> {
        if (outOfChina(gcjLat, gcjLng)) {
            return Pair(gcjLat, gcjLng)
        }
        
        var dLat = transformLat(gcjLng - 105.0, gcjLat - 35.0)
        var dLng = transformLng(gcjLng - 105.0, gcjLat - 35.0)
        val radLat = gcjLat / 180.0 * PI
        var magic = sin(radLat)
        magic = 1 - EE * magic * magic
        val sqrtMagic = sqrt(magic)
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI)
        dLng = (dLng * 180.0) / (A / sqrtMagic * cos(radLat) * PI)
        val mgLat = gcjLat - dLat
        val mgLng = gcjLng - dLng
        return Pair(mgLat, mgLng)
    }
    
    /**
     * 使用百度SDK进行坐标转换（临时简化版本）
     * TODO: 添加百度地图SDK后启用
     */
    fun convertWithBaiduSDK(
        latitude: Double,
        longitude: Double,
        from: String = "WGS84",
        to: String = "BD09LL"
    ): LatLng {
        // 临时返回原坐标，等SDK集成后实现真实转换
        return LatLng(latitude, longitude)
    }
    
    /**
     * 计算两点间距离（米）
     */
    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371000.0 // 地球半径（米）
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
    
    private fun transformLat(lng: Double, lat: Double): Double {
        var ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * sqrt(abs(lng))
        ret += (20.0 * sin(6.0 * lng * PI) + 20.0 * sin(2.0 * lng * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(lat * PI) + 40.0 * sin(lat / 3.0 * PI)) * 2.0 / 3.0
        ret += (160.0 * sin(lat / 12.0 * PI) + 320 * sin(lat * PI / 30.0)) * 2.0 / 3.0
        return ret
    }
    
    private fun transformLng(lng: Double, lat: Double): Double {
        var ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * sqrt(abs(lng))
        ret += (20.0 * sin(6.0 * lng * PI) + 20.0 * sin(2.0 * lng * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(lng * PI) + 40.0 * sin(lng / 3.0 * PI)) * 2.0 / 3.0
        ret += (150.0 * sin(lng / 12.0 * PI) + 300.0 * sin(lng / 30.0 * PI)) * 2.0 / 3.0
        return ret
    }
    
    private fun outOfChina(lat: Double, lng: Double): Boolean {
        return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271
    }
}
