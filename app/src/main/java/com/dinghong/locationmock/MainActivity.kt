package com.dinghong.locationmock

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.dinghong.locationmock.manager.PermissionManager
import com.dinghong.locationmock.ui.screens.MainScreen
import com.dinghong.locationmock.ui.theme.LocationMockTheme

/**
 * 定红定位模拟器主活动
 * 应用程序入口点，负责设置主题、导航和权限管理
 */
class MainActivity : ComponentActivity() {

    private lateinit var permissionManager: PermissionManager

    // 权限请求启动器
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val grantResults = permissions.values.map { if (it) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED }.toIntArray()
        permissionManager.onPermissionResult(permissions.keys.toTypedArray(), grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 初始化权限管理器
        permissionManager = PermissionManager(this)

        // 请求位置权限
        requestLocationPermissions()

        setContent {
            LocationMockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        permissionManager = permissionManager,
                        onRequestPermissions = { requestLocationPermissions() }
                    )
                }
            }
        }
    }

    /**
     * 请求位置权限
     */
    private fun requestLocationPermissions() {
        val permissionsToRequest = permissionManager.getLocationPermissionsToRequest()
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LocationMockTheme {
        MainScreen()
    }
}
