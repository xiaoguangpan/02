package com.dinghong.locationmock.data

import com.dinghong.locationmock.manager.LatLng

/**
 * 收藏位置数据类
 */
data class FavoriteLocation(
    val id: String,
    val name: String,
    val address: String,
    val latLng: LatLng,
    val createdAt: Long = System.currentTimeMillis()
)
