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
import com.dinghong.locationmock.manager.LatLng

// 临时类型定义，避免编译错误
typealias BaiduMap = Any
import com.dinghong.locationmock.ui.components.RealBaiduMapView
import com.dinghong.locationmock.ui.components.BottomControlCard
import com.dinghong.locationmock.ui.components.DebugPanel
import com.dinghong.locationmock.ui.components.DebugStatusIndicator
import com.dinghong.locationmock.ui.components.FloatingControls
import com.dinghong.locationmock.ui.components.HelpDialog
import com.dinghong.locationmock.ui.components.AddFavoriteDialog
import com.dinghong.locationmock.ui.components.FavoriteDialog
import com.dinghong.locationmock.ui.components.PermissionErrorDialog
import com.dinghong.locationmock.manager.PermissionManager
import com.dinghong.locationmock.viewmodel.MainViewModel

/**
 * 主界面屏幕
 * 包含全屏地图、右侧悬浮控件、底部控制卡片
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
    permissionManager: PermissionManager? = null,
    onRequestPermissions: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // 初始化ViewModel
    LaunchedEffect(Unit) {
        viewModel.initialize(context, permissionManager)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 全屏百度地图 - 真实地图组件
        RealBaiduMapView(
            modifier = Modifier.fillMaxSize(),
            onMapReady = { baiduMap ->
                // 传递真实的BaiduMap对象给ViewModel
                viewModel.onMapReady(baiduMap)
            },
            onMapClick = { latLng ->
                viewModel.onMapClick(LatLng(latLng.first, latLng.second))
            }
        )
        
        // 右侧悬浮控件组
        FloatingControls(
            modifier = Modifier.align(Alignment.CenterEnd),
            onDebugClick = { viewModel.toggleDebugPanel() },
            onHelpClick = { viewModel.showHelp() },
            onNavigationClick = { viewModel.startNavigation() },
            onZoomInClick = { viewModel.zoomIn() },
            onZoomOutClick = { viewModel.zoomOut() }
        )
        
        // 底部控制卡片
        BottomControlCard(
            modifier = Modifier.align(Alignment.BottomCenter),
            searchText = uiState.searchText,
            onSearchTextChange = { viewModel.updateSearchText(it) },
            onSearchSubmit = { viewModel.performSearch() },
            isSimulating = uiState.isSimulating,
            onSimulateToggle = { viewModel.toggleSimulation() },
            onAddFavoriteClick = { viewModel.showAddFavoriteDialog() },
            onShowFavoritesClick = { viewModel.showFavoriteListDialog() },
            currentCoordinate = uiState.currentCoordinate,
            searchSuggestions = uiState.searchSuggestions,
            onSuggestionClick = { viewModel.onSearchSuggestionClick(it) }
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
            hasLocationPermission = uiState.hasLocationPermission,
            hasMockLocationPermission = uiState.hasMockLocationPermission,
            onLocationPermissionClick = { viewModel.onLocationPermissionClick() },
            onMockPermissionClick = { viewModel.onMockPermissionClick() }
        )

        // 帮助对话框（如果显示）
        if (uiState.showHelpDialog) {
            HelpDialog(
                onDismiss = { viewModel.hideHelpDialog() }
            )
        }



        // 收藏列表对话框（如果显示）
        if (uiState.showFavoriteListDialog) {
            FavoriteDialog(
                favoriteLocations = uiState.favoriteLocations,
                onDismiss = { viewModel.hideFavoriteListDialog() },
                onSelectFavorite = { favorite -> viewModel.selectFavoriteLocation(favorite) },
                onDeleteFavorite = { favoriteId -> viewModel.deleteFavorite(favoriteId) }
            )
        }

        // 权限错误对话框（如果显示）
        if (uiState.showPermissionErrorDialog) {
            PermissionErrorDialog(
                title = uiState.permissionErrorTitle,
                message = uiState.permissionErrorMessage,
                onDismiss = { viewModel.hidePermissionErrorDialog() },
                onConfirm = {
                    viewModel.hidePermissionErrorDialog()
                    // 根据错误类型执行相应操作
                    if (uiState.permissionErrorMessage.contains("位置权限")) {
                        viewModel.onLocationPermissionClick()
                    } else if (uiState.permissionErrorMessage.contains("模拟权限")) {
                        viewModel.onMockPermissionClick()
                    }
                }
            )
        }
    }
}


