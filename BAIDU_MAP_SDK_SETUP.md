# 百度地图SDK集成指南

## 🎯 当前状态

✅ **已完成的配置**：
- Gradle依赖配置完成
- AndroidManifest.xml权限和API Key配置完成
- 地址搜索服务已集成
- 真实地图组件已创建
- UI界面已更新

⚠️ **需要完成的步骤**：
- 下载百度地图SDK AAR文件
- 放置到正确目录
- 启用SDK初始化代码

## 📥 下载百度地图SDK

### 步骤1：访问百度地图开放平台
访问：https://lbs.baidu.com/index.php?title=sdk/download

### 步骤2：自定义下载
点击"自定义下载"按钮，选择以下功能模块：
- ✅ **基础地图** (必选)
- ✅ **检索功能** (必选，用于地址搜索)
- ✅ **计算工具** (推荐)

### 步骤3：下载SDK包
1. 点击"生成下载链接"
2. 下载生成的ZIP文件
3. 解压缩文件

### 步骤4：复制AAR文件
将解压后的以下文件复制到 `app/libs/` 目录：
```
app/libs/
├── BaiduMapSDK_Map-7.x.x.aar
├── BaiduMapSDK_Search-7.x.x.aar
└── BaiduMapSDK_Util-7.x.x.aar
```

## 🔧 启用SDK代码

### 步骤5：启用Application初始化
编辑 `LocationMockApplication.kt`，取消注释以下代码：
```kotlin
// 取消注释这些导入
import com.baidu.mapapi.CoordType
import com.baidu.mapapi.SDKInitializer

// 取消注释初始化代码
SDKInitializer.initialize(this)
SDKInitializer.setCoordType(CoordType.BD09LL)
```

### 步骤6：启用地图组件
编辑 `RealBaiduMapView.kt`，取消注释百度地图相关代码：
```kotlin
// 取消注释导入
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.model.LatLng

// 取消注释地图创建代码
val mapView = MapView(context)
val baiduMap = mapView.map
```

## 🧪 测试功能

### 地址搜索测试
1. 启动应用
2. 在搜索框输入："北京天安门"
3. 点击搜索或回车
4. 查看地图是否移动到正确位置

### 坐标输入测试
1. 在搜索框输入："39.9042,116.4074"
2. 点击搜索或回车
3. 查看当前坐标是否更新

### 地图点击测试
1. 点击地图任意位置
2. 查看当前坐标是否更新
3. 查看调试日志是否显示坐标信息

## 🔍 故障排除

### 常见问题

**问题1：地图显示空白**
- 检查API Key是否正确配置
- 检查网络权限是否添加
- 查看Logcat中的错误信息

**问题2：搜索功能不工作**
- 检查网络连接
- 查看调试日志中的错误信息
- 确认API Key有搜索权限

**问题3：编译错误**
- 确认AAR文件已正确放置在libs目录
- 检查Gradle同步是否成功
- 清理并重新构建项目

### 调试日志
应用启动后查看以下日志：
```
[SUCCESS] 定红定位模拟器已启动
[INFO] 正在初始化地图组件...
[SUCCESS] ✅ 百度地图SDK初始化完成
[INFO] API Key: RHIrMFCec8xoScSBBtbCMtrTNpLYrwjt
```

## 📱 功能验证清单

- [ ] 应用正常启动
- [ ] 地图正常显示（非网格背景）
- [ ] 地址搜索功能正常
- [ ] 坐标输入功能正常
- [ ] 地图点击功能正常
- [ ] 位置模拟功能正常
- [ ] 收藏功能正常

## 🎉 完成

完成以上步骤后，您的应用将拥有：
- ✅ 真实的百度地图显示
- ✅ 完整的地址搜索功能
- ✅ 坐标输入和显示
- ✅ 地图交互功能
- ✅ 位置模拟功能

如有问题，请查看调试日志或联系技术支持。
