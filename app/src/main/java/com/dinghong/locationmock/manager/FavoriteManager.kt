package com.dinghong.locationmock.manager

import android.content.Context
import android.content.SharedPreferences
import com.dinghong.locationmock.data.FavoriteLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.util.UUID

/**
 * 收藏位置管理器
 */
class FavoriteManager(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    
    private val _favoriteLocations = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favoriteLocations: StateFlow<List<FavoriteLocation>> = _favoriteLocations.asStateFlow()
    
    private val json = Json { ignoreUnknownKeys = true }
    
    init {
        loadFavorites()
    }
    
    /**
     * 添加收藏位置
     */
    fun addFavorite(name: String, address: String, latLng: LatLng) {
        val favorite = FavoriteLocation(
            id = UUID.randomUUID().toString(),
            name = name,
            address = address,
            latLng = latLng
        )
        
        val currentList = _favoriteLocations.value.toMutableList()
        currentList.add(0, favorite) // 添加到列表顶部
        _favoriteLocations.value = currentList
        
        saveFavorites()
    }
    
    /**
     * 删除收藏位置
     */
    fun removeFavorite(favoriteId: String) {
        val currentList = _favoriteLocations.value.toMutableList()
        currentList.removeAll { it.id == favoriteId }
        _favoriteLocations.value = currentList
        
        saveFavorites()
    }
    
    /**
     * 检查位置是否已收藏
     */
    fun isFavorite(latLng: LatLng): Boolean {
        return _favoriteLocations.value.any { favorite ->
            // 使用较小的精度比较坐标（约10米精度）
            kotlin.math.abs(favorite.latLng.latitude - latLng.latitude) < 0.0001 &&
            kotlin.math.abs(favorite.latLng.longitude - latLng.longitude) < 0.0001
        }
    }
    
    /**
     * 保存收藏列表到SharedPreferences
     */
    private fun saveFavorites() {
        try {
            val favoritesData = _favoriteLocations.value.map { favorite ->
                SerializableFavorite(
                    id = favorite.id,
                    name = favorite.name,
                    address = favorite.address,
                    latitude = favorite.latLng.latitude,
                    longitude = favorite.latLng.longitude,
                    createdAt = favorite.createdAt
                )
            }
            
            val jsonString = json.encodeToString(favoritesData)
            sharedPreferences.edit()
                .putString("favorites_list", jsonString)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 从SharedPreferences加载收藏列表
     */
    private fun loadFavorites() {
        try {
            val jsonString = sharedPreferences.getString("favorites_list", null)
            if (jsonString != null) {
                val favoritesData = json.decodeFromString<List<SerializableFavorite>>(jsonString)
                val favorites = favoritesData.map { data ->
                    FavoriteLocation(
                        id = data.id,
                        name = data.name,
                        address = data.address,
                        latLng = LatLng(data.latitude, data.longitude),
                        createdAt = data.createdAt
                    )
                }
                _favoriteLocations.value = favorites
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _favoriteLocations.value = emptyList()
        }
    }
    
    /**
     * 可序列化的收藏位置数据类
     */
    @Serializable
    private data class SerializableFavorite(
        val id: String,
        val name: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val createdAt: Long
    )
}
