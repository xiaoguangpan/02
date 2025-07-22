package com.dinghong.locationmock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
// import com.baidu.mapapi.map.BaiduMap
// import com.baidu.mapapi.model.LatLng
import com.dinghong.locationmock.manager.BaiduMap
import com.dinghong.locationmock.manager.LatLng
import com.dinghong.locationmock.ui.components.BaiduMapView
import com.dinghong.locationmock.ui.components.BottomControlCard
import com.dinghong.locationmock.ui.components.DebugPanel
import com.dinghong.locationmock.ui.components.DebugStatusIndicator
import com.dinghong.locationmock.ui.components.FloatingControls
import com.dinghong.locationmock.viewmodel.MainViewModel

/**
 * 主界面屏幕
 * 包含全屏地图、右侧悬浮控件、底部控制卡片
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // 初始化ViewModel
    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 全屏百度地图
        BaiduMapView(
            modifier = Modifier.fillMaxSize(),
            onMapReady = { baiduMap ->
                viewModel.onMapReady(baiduMap)
            },
            onMapClick = { latLng ->
                viewModel.onMapClick(latLng)
            },
            mapType = 1, // BaiduMap.MAP_TYPE_SATELLITE
            isTrafficEnabled = false,
            isMyLocationEnabled = false
        )
        
        // 右侧悬浮控件组
        FloatingControls(
            modifier = Modifier.align(Alignment.CenterEnd),
            onDebugClick = { viewModel.toggleDebugPanel() },
            onHelpClick = { viewModel.showHelp() },
            onCompassClick = { viewModel.resetCompass() },
            onZoomInClick = { viewModel.zoomIn() },
            onZoomOutClick = { viewModel.zoomOut() }
        )
        
        // 底部控制卡片
        BottomControlCard(
            modifier = Modifier.align(Alignment.BottomCenter),
            searchText = uiState.searchText,
            onSearchTextChange = { viewModel.updateSearchText(it) },
            onSearchSubmit = { viewModel.performSearch() },
            onCurrentLocationClick = { viewModel.getCurrentLocation() },
            isSimulating = uiState.isSimulating,
            onSimulateToggle = { viewModel.toggleSimulation() },
            isEnhancedMode = uiState.isEnhancedMode,
            onEnhancedModeToggle = { viewModel.toggleEnhancedMode() },
            currentCoordinate = uiState.currentCoordinate
        )
        
        // 调试面板（如果显示）
        if (uiState.showDebugPanel) {
            DebugPanel(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .width(350.dp)
                    .height(450.dp),
                debugLogs = uiState.debugLogs,
                onClose = { viewModel.hideDebugPanel() },
                onClearLogs = { viewModel.clearDebugLogs() },
                onCopyLogs = { viewModel.copyDebugLogs(context) }
            )
        }

        // 调试状态指示器
        DebugStatusIndicator(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            isSimulating = uiState.isSimulating,
            hasLocationPermission = uiState.hasLocationPermission,
            hasMockLocationPermission = uiState.hasMockLocationPermission
        )
    }
}


