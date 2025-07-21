# 定红定位模拟器 (DingHong Location Mock)

[![Android CI/CD](https://github.com/xiaoguangpan/dinghong-location-mock/actions/workflows/android-build.yml/badge.svg)](https://github.com/xiaoguangpan/dinghong-location-mock/actions/workflows/android-build.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)

一款功能强大、界面现代化的Android定位模拟应用，采用Material Design 3设计，集成百度地图SDK，提供专业级的位置模拟解决方案。

## ✨ 核心特性

### 🎯 智能位置模拟系统
- **精确坐标输入**: 支持手动输入经纬度坐标，自动识别坐标格式
- **智能地址搜索**: 集成百度地图搜索API，支持模糊搜索和历史记录
- **可视化地图选点**: 深色卫星地图主题，支持点击选择目标位置
- **一键模拟控制**: 智能开始/停止位置模拟，支持标准和增强模式
- **实时位置追踪**: 显示当前模拟位置状态和坐标信息

### 🗺️ 高级地图集成
- **深色主题地图**: 卫星图模式，提供专业的视觉体验
- **自定义控件**: 隐藏默认控件，使用Material Design 3自定义UI
- **智能导航**: 指南针重置、缩放控制、手势操作支持
- **坐标系转换**: BD09LL(百度)↔WGS84(GPS)自动转换，显示偏移量

### 🚀 增强兼容性系统
- **双模式架构**: 标准模式(100ms更新) + 增强模式(50ms更新)
- **钉钉专用优化**: 针对钉钉打卡的专用兼容性优化
- **百度地图适配**: 检测百度系应用，启用专用兼容模式
- **多提供者支持**: 同时更新GPS、NETWORK、PASSIVE位置提供者
- **防检测机制**: 微坐标变化、高频更新、预启动机制

### 🛠️ 专业调试系统
- **实时调试面板**: 专用调试按钮，实时显示系统状态
- **详细日志记录**: 地图初始化、坐标转换、模拟定位全流程日志
- **一键复制导出**: 支持调试日志复制到剪贴板
- **彩色状态显示**: 错误红色、成功绿色、调试蓝色、坐标紫色
- **时间戳记录**: 精确到毫秒的操作时间记录

## 🏗️ 技术架构

### 开发技术栈
- **开发语言**: Kotlin 1.9+
- **UI框架**: Jetpack Compose + Material Design 3
- **最低SDK**: Android 6.0 (API 23)
- **目标SDK**: Android 14 (API 34)
- **编译工具**: Gradle 8.14.3 + AGP 8.7.3
- **地图服务**: 百度地图SDK v7.6.0

### 核心架构原则
采用 **"原生API + 人性化引擎 + 流程引导器"** 的纯客户端解决方案，在**非Root、非Shizuku**环境下运行，通过算法和流程引导对抗高级行为检测机制。

## 📱 安装使用

### 系统要求
- Android 6.0 (API 23) 及以上版本
- 支持ARM64、ARM32架构
- 建议内存2GB以上

### 安装步骤
1. 从[Releases页面](https://github.com/xiaoguangpan/dinghong-location-mock/releases)下载最新APK
2. 安装APK文件
3. 在系统设置中启用开发者选项
4. 在开发者选项中选择本应用作为"模拟位置信息应用"
5. 授予应用所需权限

### 百度地图API Key配置
1. 访问[百度地图开放平台](https://lbsyun.baidu.com/apiconsole/key)
2. 创建应用，选择Android平台
3. 输入包名: `com.dinghong.locationmock`
4. 输入SHA1签名（见构建信息）
5. 获取API Key后替换AndroidManifest.xml中的配置

## 🔧 开发构建

### 环境要求
- JDK 17+
- Android Studio Hedgehog | 2023.1.1+
- Gradle 8.14.3+

### 本地构建
```bash
# 克隆项目
git clone https://github.com/xiaoguangpan/dinghong-location-mock.git
cd dinghong-location-mock

# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease
```

### GitHub Actions自动构建
项目配置了完整的CI/CD流程，每次提交都会自动构建并生成APK文件。

## 📋 使用说明

### 基础使用
1. **选择位置**: 在地图上点击或搜索地址选择目标位置
2. **开始模拟**: 点击"开始模拟"按钮启动位置模拟
3. **模式选择**: 根据需要选择标准模式或增强模式
4. **停止模拟**: 点击"停止模拟"按钮结束位置模拟

### 高级功能
- **调试面板**: 点击右侧调试按钮查看详细运行状态
- **坐标转换**: 自动显示不同坐标系的转换结果
- **地图控制**: 使用右侧控件进行地图缩放和导航

## ⚠️ 重要说明

### 使用限制
- 本应用仅供学习研究和合法测试使用
- 请勿用于任何违法违规活动
- 使用前请了解相关法律法规

### 权限说明
- **位置权限**: 用于获取和模拟GPS位置
- **存储权限**: 用于保存调试日志和配置文件
- **网络权限**: 用于地图数据加载和地址搜索

## 🤝 贡献指南

欢迎提交Issue和Pull Request来改进项目！

### 开发规范
- 遵循Kotlin编码规范
- 使用Material Design 3设计原则
- 添加适当的注释和文档
- 确保代码通过所有测试

## 📄 开源协议

本项目采用 [Apache License 2.0](LICENSE) 开源协议。

## 🙏 致谢

- [百度地图开放平台](https://lbsyun.baidu.com/) - 提供地图服务支持
- [Android Jetpack](https://developer.android.com/jetpack) - 现代Android开发框架
- [Material Design 3](https://m3.material.io/) - 设计系统支持

## 📞 联系方式

- 项目地址: https://github.com/xiaoguangpan/dinghong-location-mock
- 问题反馈: [Issues](https://github.com/xiaoguangpan/dinghong-location-mock/issues)

---

**免责声明**: 本软件仅供学习研究使用，开发者不承担任何因使用本软件而产生的法律责任。请用户自觉遵守相关法律法规，合理合法使用。
