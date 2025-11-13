# 华为手表定时提醒应用 (Huawei Notify)

[![Android](https://img.shields.io/badge/Android-8.0+-green.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-26+-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 🔔 一款通过本地推送实现定时提醒的 Android 应用，自动同步到华为手表并震动提醒

## 📖 目录

- [项目简介](#-项目简介)
- [功能特性](#-功能特性)
- [技术栈](#-技术栈)
- [项目结构](#-项目结构)
- [快速开始](#-快速开始)
- [详细功能说明](#-详细功能说明)
- [权限说明](#-权限说明)
- [华为手表同步](#-华为手表同步)
- [常见问题 (FAQ)](#-常见问题-faq)
- [故障排查](#-故障排查)
- [开发者信息](#-开发者信息)
- [版本历史](#-版本历史)

## 💡 项目简介

**华为手表定时提醒应用**是一款专为需要定时提醒功能的用户设计的 Android 应用。应用通过本地推送通知的方式，在每小时的固定时间点（默认 0 分和 55 分）自动发送提醒通知，并能够自动同步到华为手表，通过震动和通知的方式提醒用户。

适用场景：
- ⏰ 每小时定时喝水提醒
- 💊 服药提醒
- 🧘 休息提醒
- 👁️ 护眼提醒
- 🏃 运动提醒

## ✨ 功能特性

### 核心功能
- ⏱️ **定时推送提醒**：每小时 0 分和 55 分自动推送通知，精准到秒
- 🕐 **灵活时间配置**：可自定义起床和睡觉时间（默认 7:00-23:00），避免休息时段打扰
- ✏️ **智能推送内容**：支持自定义推送文本内容，满足个性化需求
- ⌚ **华为手表同步**：推送自动同步到华为手表，支持震动通知
- 🔄 **后台运行**：使用 AlarmManager 精准定时任务，系统重启后自动恢复
- 🧪 **手动测试**：内置手动推送功能，便于快速测试通知效果
- 🔐 **权限管理**：友好的权限引导提示，智能检测通知、精准定时和电池优化权限
- 📊 **实时状态显示**：显示下次提醒时间和权限状态，一目了然

### 技术亮点
- 🚀 采用 AlarmManager 的 `setExactAndAllowWhileIdle` API，确保在 Doze 模式下依然能准时触发
- 🔋 智能电池优化白名单引导，保证后台稳定运行
- 📱 Material Design 3 设计规范，界面简洁美观
- 💾 使用 SharedPreferences 持久化配置，数据安全可靠

## 🛠️ 技术栈

### 开发环境
- **开发语言**：Java 8
- **最低 SDK**：API 26 (Android 8.0 Oreo)
- **目标 SDK**：API 34 (Android 14)
- **构建工具**：Android Gradle Plugin 8.3.2

### 核心框架与库
| 技术 | 版本 | 用途 |
|------|------|------|
| WorkManager | 2.9.1 | 后台任务备用机制 |
| NotificationCompat | 1.12.0 | 兼容性通知推送 |
| Material Design 3 | 1.11.0 | UI 设计组件 |
| ConstraintLayout | 2.1.4 | 响应式布局 |
| SharedPreferences | - | 数据持久化 |
| AlarmManager | - | 精准定时任务 |

## 📁 项目结构

```
huawei_notify/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/timedreminder/
│   │   │   ├── MainActivity.java                # 主界面配置页面
│   │   │   ├── NotificationHelper.java          # 通知管理器
│   │   │   ├── ReminderScheduler.java           # 提醒调度器（AlarmManager）
│   │   │   ├── ReminderTrigger.java             # 提醒触发逻辑
│   │   │   ├── ReminderPreferences.java         # 配置存储管理
│   │   │   ├── ReminderTimeUtils.java           # 时间计算工具类
│   │   │   ├── worker/
│   │   │   │   └── ReminderWorker.java          # WorkManager 后台任务
│   │   │   └── receiver/
│   │   │       ├── ReminderAlarmReceiver.java   # AlarmManager 广播接收器
│   │   │       └── ReminderBootReceiver.java    # 系统启动广播接收器
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml            # 主界面布局
│   │   │   ├── drawable/
│   │   │   │   └── ic_notification.xml          # 通知图标
│   │   │   ├── values/
│   │   │   │   ├── colors.xml                   # 颜色资源
│   │   │   │   ├── strings.xml                  # 字符串资源
│   │   │   │   └── themes.xml                   # 主题样式
│   │   │   └── xml/
│   │   │       ├── backup_rules.xml             # 备份规则
│   │   │       └── data_extraction_rules.xml    # 数据提取规则
│   │   └── AndroidManifest.xml                  # 权限和组件声明
│   ├── build.gradle                             # 模块构建配置
│   └── proguard-rules.pro                       # 混淆规则
├── gradle/                                      # Gradle 配置
├── build.gradle                                 # 项目级构建配置
├── settings.gradle                              # 项目设置
├── gradlew                                      # Gradle 包装器（Unix）
├── gradlew.bat                                  # Gradle 包装器（Windows）
└── README.md                                    # 项目文档
```

## 🚀 快速开始

### 环境要求

- **Android Studio**：Hedgehog (2023.1.1) 或更高版本
- **JDK**：Java 8 或更高版本
- **Android SDK**：API 26 及以上
- **测试设备**：Android 8.0 (API 26) 及以上系统的手机

### 克隆项目

```bash
git clone <repository-url>
cd huawei_notify
```

### 导入项目

1. 打开 Android Studio
2. 选择 `File` → `Open`
3. 选择项目根目录
4. 等待 Gradle 同步完成

### 首次运行

1. **连接设备或启动模拟器**
   - 确保设备已开启开发者模式和 USB 调试
   - 或使用 Android Studio 的 AVD 模拟器

2. **运行应用**
   - 点击工具栏的 Run 按钮（绿色三角形）
   - 或使用快捷键 `Shift + F10`（Windows/Linux）/ `Control + R`（Mac）

3. **授予必要权限**
   - 首次启动会提示授予通知权限
   - 启用提醒功能时会引导授予精准定时权限
   - 建议将应用加入电池优化白名单

### 基本配置

1. **设置起床/睡觉时间**
   - 通过时间选择器设置您的清醒时段
   - 默认为 7:00 - 23:00

2. **自定义提醒内容**
   - 在输入框中填写您的提醒文本
   - 例如："喝水时间到了" 或 "休息一下吧"

3. **启用定时提醒**
   - 打开"启用定时提醒"开关
   - 应用会自动在每小时 0 分和 55 分推送通知

4. **保存配置**
   - 点击"保存配置"按钮保存设置
   - 配置会立即生效

5. **测试功能**
   - 点击"手动测试推送"按钮
   - 立即查看通知效果

## 📚 详细功能说明

### 配置管理

#### 起床/睡觉时间
- **功能**：定义清醒时段，只在该时段内发送提醒
- **默认值**：7:00 - 23:00
- **使用建议**：根据个人作息调整，避免休息时段打扰
- **示例**：设置 8:00 - 22:00，则只在这个时间段内推送

#### 推送内容
- **功能**：自定义提醒通知的文本内容
- **默认值**：定时提醒
- **字数限制**：建议 20 字以内，保证手表显示完整
- **示例**："喝水提醒 💧"、"休息一下 👁️"

#### 总开关
- **功能**：启用或禁用整个定时提醒功能
- **开启**：开始按设定时间推送通知
- **关闭**：停止所有定时任务，不再推送

### 后台定时任务

应用采用双重机制确保提醒的可靠性：

#### 主要机制：AlarmManager
- 使用 `setExactAndAllowWhileIdle` API 实现精准定时
- 每小时的 0 分和 55 分自动触发
- 支持 Doze 模式下的唤醒
- 系统重启后通过 `BOOT_COMPLETED` 广播自动恢复

#### 备用机制：WorkManager
- 作为 AlarmManager 的补充机制
- 在某些情况下提供额外保障
- 自动处理系统限制和约束

#### 触发时间点
每小时固定在以下两个时间点触发：
- `:00` 整点（例如：8:00, 9:00, 10:00）
- `:55` 分（例如：8:55, 9:55, 10:55）

### 推送通知

#### 通知特性
- **通知渠道**：使用独立的通知渠道，可在系统设置中单独管理
- **优先级**：HIGH 优先级，确保及时显示
- **震动**：默认开启震动反馈
- **声音**：使用系统默认通知声音
- **自动消失**：点击通知后自动关闭
- **锁屏显示**：支持锁屏状态下显示（隐私模式）

#### 华为手表同步
- 通知会自动通过华为健康 App 同步到手表
- 手表会震动并显示通知内容
- 支持在手表上查看完整通知
- 点击通知可打开应用

### 手动测试

- **功能**：无需等待定时触发，立即发送测试通知
- **用途**：
  - 验证通知权限是否正常
  - 测试手表同步是否成功
  - 查看通知显示效果
  - 调试自定义内容

## 🔐 权限说明

应用需要以下权限以确保功能正常运行：

### 通知权限 (POST_NOTIFICATIONS)
- **用途**：发送定时提醒通知
- **授予方式**：首次启动时弹窗请求（Android 13+）
- **重要性**：⭐⭐⭐⭐⭐ 必需权限
- **说明**：没有此权限无法发送任何通知

### 精准定时权限 (SCHEDULE_EXACT_ALARM)
- **用途**：在精确的时间点触发提醒
- **授予方式**：需要手动在系统设置中开启
- **重要性**：⭐⭐⭐⭐⭐ 必需权限（Android 12+）
- **说明**：确保提醒准时触发，而不是延迟或批量执行

### 开机启动权限 (RECEIVE_BOOT_COMPLETED)
- **用途**：系统启动后自动恢复定时任务
- **授予方式**：安装时自动授予
- **重要性**：⭐⭐⭐⭐ 重要权限
- **说明**：无需手动授予，系统会自动处理

### 电池优化白名单 (REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
- **用途**：防止系统在省电模式下杀掉后台任务
- **授予方式**：应用会引导用户前往设置
- **重要性**：⭐⭐⭐⭐ 强烈建议
- **说明**：虽非强制，但能显著提升提醒的稳定性

## ⌚ 华为手表同步

### 同步原理

华为手表通过华为健康 App 自动同步手机通知到手表。应用发送的通知会被华为健康 App 捕获并转发到手表，实现震动提醒。

### 使用前准备

1. **确保手表与手机配对成功**
   - 打开华为健康 App
   - 在设备列表中查看手表连接状态
   - 确保蓝牙连接正常

2. **开启通知同步**
   - 打开华为健康 App
   - 进入 `设备` → `您的手表` → `通知管理`
   - 找到"定时提醒"应用
   - 开启通知同步开关

3. **设置震动强度**（可选）
   - 在手表设置中调整震动强度
   - 建议选择"强"或"中"档，确保能感知到提醒

### 验证同步

1. 在应用中点击"手动测试推送"
2. 观察手表是否震动
3. 在手表上查看通知内容
4. 如果未收到，检查华为健康 App 的通知同步设置

### 支持的手表型号

理论上支持所有能运行华为健康 App 并具有通知同步功能的华为/荣耀手表：
- HUAWEI WATCH GT 系列
- HUAWEI WATCH Fit 系列
- HONOR MagicWatch 系列
- 其他支持华为健康 App 的智能手表

## ❓ 常见问题 (FAQ)

### Q1: 为什么手表没有收到提醒？

**A**: 请按以下步骤排查：
1. 确保手表与手机蓝牙连接正常
2. 检查华为健康 App 中是否开启了本应用的通知同步
3. 在手机上发送测试通知，观察手表是否收到
4. 检查手表的勿扰模式是否开启
5. 尝试重启华为健康 App 或重新配对手表

### Q2: 怎样修改提醒时间间隔？

**A**: 
- 当前版本固定为每小时的 0 分和 55 分触发
- 可以通过设置起床/睡觉时间来限制提醒的时段
- 例如，设置 8:00 - 22:00，则只在这个时间段内的整点和 55 分推送
- 如需自定义间隔，需要修改源码中的 `ReminderTimeUtils` 类

### Q3: 如何关闭提醒功能？

**A**: 
- 方式 1：在应用主界面关闭"启用定时提醒"开关
- 方式 2：在系统设置中关闭应用的通知权限（不推荐）
- 方式 3：卸载应用

### Q4: 为什么后台运行被杀掉？

**A**: 
1. 将应用加入电池优化白名单：
   - 点击应用内的"电池优化设置"按钮
   - 或前往 `设置` → `应用` → `定时提醒` → `电池` → `不限制后台活动`

2. 检查系统的自启动管理：
   - `设置` → `应用` → `应用启动管理` → 找到"定时提醒"
   - 选择"手动管理"
   - 开启"自动启动"、"后台活动"和"关联启动"

3. 关闭省电模式：
   - 极端省电模式会限制后台任务
   - 建议使用普通模式或智能省电模式

### Q5: 通知内容在手表上显示不全？

**A**: 
- 手表屏幕有限，建议提醒内容控制在 20 字以内
- 使用简短明了的文字，如"喝水"、"休息"
- 可以使用 emoji 表情符号增强识别度

### Q6: 应用耗电量大吗？

**A**: 
- 应用采用 AlarmManager 机制，只在特定时间点唤醒
- 平时几乎不消耗后台资源
- 预计每日额外耗电量 < 1%
- 相比其他后台应用非常节能

### Q7: 支持其他品牌的智能手表吗？

**A**: 
- 理论上支持所有能接收手机通知的智能手表
- 但最佳体验是华为/荣耀手表配合华为健康 App
- 其他品牌手表请在各自的配套 App 中开启通知同步
- 例如：小米手环、Amazfit、Apple Watch（需配对 iPhone）等

## 🔧 故障排查

### 问题 1：无法发送通知

**排查步骤**：
1. 检查应用是否已授予通知权限
   - 查看应用内权限状态显示
   - 或前往 `设置` → `应用` → `定时提醒` → `通知` 检查

2. 检查通知渠道是否被关闭
   - 长按应用发送的通知
   - 查看"定时提醒"渠道是否开启

3. 检查系统勿扰模式
   - 关闭勿扰模式或将应用设为例外

### 问题 2：提醒不准时或遗漏

**排查步骤**：
1. 检查精准定时权限
   - 查看应用内"精准提醒"权限状态
   - Android 12+ 需要手动授予此权限

2. 检查电池优化设置
   - 将应用加入白名单
   - 关闭后台限制

3. 检查自启动管理
   - 在系统的应用启动管理中允许自动启动

4. 查看下次提醒时间
   - 在应用主界面查看"下次提醒时间"
   - 确认时间是否符合预期

### 问题 3：系统重启后提醒失效

**排查步骤**：
1. 检查是否禁用了开机启动
   - `设置` → `应用` → `定时提醒` → `自动启动`
   - 确保开启自动启动

2. 重新打开应用
   - 系统重启后首次打开应用会重新注册任务

3. 重新保存配置
   - 点击"保存配置"按钮强制刷新

### 问题 4：手表震动太弱或太强

**解决方案**：
1. 在手表设置中调整震动强度
2. 在华为健康 App 中调整通知提醒方式
3. 检查手表的勿扰模式时间设置

### 问题 5：应用闪退或无法打开

**解决方案**：
1. 清除应用缓存：`设置` → `应用` → `定时提醒` → `存储` → `清除缓存`
2. 清除应用数据（会丢失配置）：`清除数据`
3. 卸载后重新安装
4. 检查系统版本是否 ≥ Android 8.0
5. 查看 Logcat 日志定位具体错误

## 👨‍💻 开发者信息

### 项目维护

本项目由个人开发者维护，欢迎贡献代码和提出建议。

### 贡献指南

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

### 反馈与建议

如果您在使用过程中遇到问题或有改进建议，欢迎通过以下方式反馈：
- 提交 Issue
- 发送邮件

### 技术支持

如需技术支持或合作，请通过 Issue 联系。

### 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

```
MIT License

Copyright (c) 2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 📋 版本历史

### v1.0.0 (2024-11-13)

**首次发布**

- ✅ 实现定时推送提醒功能（每小时 0 分和 55 分）
- ✅ 支持自定义起床/睡觉时间
- ✅ 支持自定义推送内容
- ✅ 华为手表自动同步和震动
- ✅ 后台稳定运行（AlarmManager + WorkManager）
- ✅ 系统重启后自动恢复
- ✅ 手动测试推送功能
- ✅ 完善的权限管理和引导
- ✅ 实时显示下次提醒时间和权限状态
- ✅ Material Design 3 界面设计

### 未来计划

- [ ] 支持自定义提醒时间间隔
- [ ] 添加多组提醒方案
- [ ] 支持每日提醒次数统计
- [ ] 添加提醒历史记录
- [ ] 支持深色模式主题切换
- [ ] 添加桌面小部件
- [ ] 支持多语言（English）
- [ ] 云端配置同步

---

<p align="center">
  <b>⭐ 如果这个项目对您有帮助，请给一个 Star！⭐</b>
</p>

<p align="center">
  Made with ❤️ for Huawei Watch Users
</p>
