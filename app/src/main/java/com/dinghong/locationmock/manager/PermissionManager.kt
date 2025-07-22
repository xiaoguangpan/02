package com.dinghong.locationmock.manager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 权限管理器
 * 负责处理位置权限和模拟位置权限的检查和请求
 */
class PermissionManager(private val context: Context) {
    
    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()
    
    private val _hasMockLocationPermission = MutableStateFlow(false)
    val hasMockLocationPermission: StateFlow<Boolean> = _hasMockLocationPermission.asStateFlow()
    
    companion object {
        const val TAG = "PermissionManager"
        
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
    
    init {
        checkPermissions()
    }
    
    /**
     * 检查所有权限状态
     */
    fun checkPermissions() {
        checkLocationPermissions()
        checkMockLocationPermission()
    }
    
    /**
     * 检查位置权限
     */
    private fun checkLocationPermissions() {
        val hasPermission = LOCATION_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        _hasLocationPermission.value = hasPermission
    }
    
    /**
     * 检查模拟位置权限
     */
    private fun checkMockLocationPermission() {
        val hasMockPermission = try {
            // 检查是否在开发者选项中设置了模拟位置应用
            Settings.Secure.getString(context.contentResolver, "mock_location") != "0"
        } catch (e: Exception) {
            false
        }
        _hasMockLocationPermission.value = hasMockPermission
    }
    
    /**
     * 打开应用设置页面（用于位置权限）
     */
    fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // 备用方案：打开应用管理页面
            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
    
    /**
     * 打开开发者选项页面（用于模拟位置权限）
     */
    fun openDeveloperSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // 备用方案：打开设置主页
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
    
    /**
     * 获取需要请求的位置权限列表
     */
    fun getLocationPermissionsToRequest(): Array<String> {
        return LOCATION_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }
    
    /**
     * 处理权限请求结果
     */
    fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        val locationPermissionsGranted = permissions.zip(grantResults.toTypedArray()).all { (permission, result) ->
            if (permission in LOCATION_PERMISSIONS) {
                result == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }
        
        if (locationPermissionsGranted) {
            checkLocationPermissions()
        }
    }
}
