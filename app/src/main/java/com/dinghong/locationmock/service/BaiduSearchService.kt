package com.dinghong.locationmock.service

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 百度地图地址搜索服务
 * 使用百度地图Web API进行地址搜索
 */
class BaiduSearchService {
    
    companion object {
        private const val BASE_URL = "https://api.map.baidu.com/"
        private const val API_KEY = "RHIrMFCec8xoScSBBtbCMtrTNpLYrwjt"
    }
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val api = retrofit.create(BaiduSearchAPI::class.java)
    
    /**
     * 搜索地址
     * @param query 搜索关键词
     * @param region 搜索区域，默认为全国
     * @return 搜索结果
     */
    suspend fun searchPlace(query: String, region: String = "全国"): BaiduSearchResponse? {
        return try {
            val response = api.searchPlace(
                query = query,
                region = region,
                output = "json",
                ak = API_KEY
            )
            if (response.isSuccessful) {
                response.body()
            } else {
                android.util.Log.e("BaiduSearch", "搜索失败: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("BaiduSearch", "搜索异常: ${e.message}")
            null
        }
    }
    
    /**
     * 地理编码 - 将地址转换为坐标
     */
    suspend fun geocoding(address: String, city: String = ""): BaiduGeocodingResponse? {
        return try {
            val response = api.geocoding(
                address = address,
                city = city,
                output = "json",
                ak = API_KEY
            )
            if (response.isSuccessful) {
                response.body()
            } else {
                android.util.Log.e("BaiduSearch", "地理编码失败: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("BaiduSearch", "地理编码异常: ${e.message}")
            null
        }
    }
}

/**
 * 百度地图搜索API接口
 */
interface BaiduSearchAPI {
    
    /**
     * POI搜索
     */
    @GET("place/v2/search")
    suspend fun searchPlace(
        @Query("query") query: String,
        @Query("region") region: String,
        @Query("output") output: String = "json",
        @Query("ak") ak: String
    ): Response<BaiduSearchResponse>
    
    /**
     * 地理编码
     */
    @GET("geocoding/v3/")
    suspend fun geocoding(
        @Query("address") address: String,
        @Query("city") city: String = "",
        @Query("output") output: String = "json",
        @Query("ak") ak: String
    ): Response<BaiduGeocodingResponse>
}

/**
 * 百度搜索响应数据类
 */
data class BaiduSearchResponse(
    val status: Int,
    val message: String?,
    val results: List<BaiduSearchResult>?
)

data class BaiduSearchResult(
    val name: String,
    val location: BaiduLocation,
    val address: String,
    val province: String?,
    val city: String?,
    val area: String?,
    val detail: Int?
)

data class BaiduLocation(
    val lat: Double,
    val lng: Double
)

/**
 * 百度地理编码响应数据类
 */
data class BaiduGeocodingResponse(
    val status: Int,
    val result: BaiduGeocodingResult?
)

data class BaiduGeocodingResult(
    val location: BaiduLocation,
    val precise: Int,
    val confidence: Int,
    val comprehension: Int,
    val level: String
)
