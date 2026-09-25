<!-- Modified by mqmqgo, 2026-09-25: unofficial AES-256 hardened fork README -->

![1](./app/src/main/res/mipmap-xhdpi/ic_launcher.png)
# FingerprintPay AES-256版 (非官方修改版)

> 本仓库是 [eritpchy/FingerprintPay](https://github.com/eritpchy/FingerprintPay) 的**非官方**修改版, 由 **mqmqgo** 维护, 与原作者无关, 也不受原作者支持.
> 原项目版权归原作者 eritpchy 所有; 感谢原作者及所有贡献者.
>
> This is an **unofficial** hardened fork of eritpchy/FingerprintPay, maintained by mqmqgo. It is not affiliated with or supported by the original author.

## 本修改版的变化 (v7.0.1)
- 目标: **KernelSU / Magisk (Zygisk) + 微信**. 其它 App 的插件代码仍可编译, 但未测试.
- **无网络**: 移除了更新检查、友盟统计、网页、okhttp/okgo; 清单中移除了网络、电话、存储和安装权限; module.prop 中没有 updateJson.
- **支付密码加密**: 使用硬件 Android Keystore (TEE/StrongBox) 中的 AES-256-GCM 密钥, 每次加密和解密都必须通过强生物识别 (BIOMETRIC_STRONG) 认证; 系统指纹变更后密钥会自动失效. 移除了软件兜底加密和 ANDROID_ID 派生密钥.
- **明文用后即擦除** (7.0.1): 解密后的密码只存在于 char[]/byte[] 中, 不创建 String, 用完或中止时立即擦除.
- **失败自动回退**: 取消、锁定、密钥失效、解密失败或任何异常时, 都会恢复微信原生的密码输入.
- 移除了捐赠界面和 QQ 群入口. 新模块 ID 为 `zygisk_fingerprintpay_wechat_aes256`, 可与官方模块区分 (不要同时安装两者).
- 详见 [NOTICE.md](./NOTICE.md) 与 [CHANGELOG.md](./CHANGELOG.md).

## 许可证
GPL-2.0, 与上游相同, 见 [LICENSE](./LICENSE). 第三方组件: FingerprintIdentify (MIT, Copyright (c) 2017 Awei, 已修改), MagiskModuleTemplate (MIT, Copyright (c) 2020 Rikka). 本软件**不提供任何担保**, 使用风险自负.

## 安装 (本修改版)
1. 如果装过官方模块或旧版加固模块, 请**先卸载**并重启
2. 从本仓库自行构建, 或使用维护者发布的 `zygisk-fingerprintpay-wechat-aes256-v7.0.1-release.zip`, 在 KernelSU/Magisk 中安装, 然后重启
3. 在 微信 → 我 → 设置 → 指纹支付 中重新输入支付密码

## 构建
```
./gradlew :app:assembleRelease
cd module && bash ./build.sh :module:assembleRelease ./src/gradle/wechat.gradle Zygisk
# 输出: module/build/release/*.zip
```

---
以下为上游 README 的原有说明 (已删除官方下载、镜像、捐赠和 QQ 群链接; 部分内容不适用于本修改版):

## 请注意: 支付宝支持刷脸支付, 体验感官跟苹果的Face ID差不多, 请考虑优先使用

## 最低要求
* 有指纹硬件
* Android 6.0+
* [Magisk](https://github.com/topjohnwu/Magisk)、[Zygisk](https://github.com/topjohnwu/Magisk) 、 [Xposed](https://github.com/ElderDrivers/EdXposed) 或 [APatch](https://github.com/bmax121/APatch) + [Zygisk Next](https://github.com/Dr-TSNG/ZygiskNext)

## 实现原理
1. 利用 [Magisk](https://github.com/topjohnwu/Magisk) 的 [Riru](https://github.com/RikkaApps/Riru)模块 或 Zygisk 加载指纹支付模块
2. 在指纹支付模块中录入应用的"支付密码"
3. 使用硬件 Android Keystore (TEE/StrongBox) 中的 AES-256-GCM 密钥将"支付密码"加密保存 (本修改版)
4. 对应程序在支付界面时, 验证手机指纹, 验证成功解密"支付密码"
5. 自动替代用户输入"支付密码", 完成支付操作

## 使用步骤 Magisk + Zygisk
1. 确认 Magisk Manager 应用设置中启用 Zygisk功能
2. 下载插件: zygisk-module-xfingerprint-pay-all-release.zip
3. 进入 Magisk Manager, 模块, 安装这几个模块, 不要重启
4. 确认启用模块, 重启手机
5. Enjoy

## 使用步骤 Apatch + Zygisk Next
1. 下载插件: [Zygisk-Next-release.zip](https://github.com/Dr-TSNG/ZygiskNext/releases)
2. 下载插件: zygisk-module-xfingerprint-pay-all-release.zip
3. 进入 Apatch 管理器, 模块, 安装这几个模块, 没装完不要重启, 安装完毕后再重启手机
4. 开机后确认模块工作是否正常, 若不正常再次重启手机
5. Enjoy

## 使用步骤 Magisk + Riru
<details> 
<summary>点击展开(过时, Riru已停止维护)</summary>

1. 下载插件: [riru-release.zip](https://github.com/RikkaApps/Riru/releases)
2. 下载插件: riru-module-xfingerprint-pay-all-release.zip
3. 进入 Magisk Manager, 模块, 安装这几个模块, 不要重启
4. 确认启用模块, 重启手机
5. Enjoy
</details>

## 使用步骤 Xposed 
> (2025.03.13, 不推荐, Xposed框架会导致大概率触发面部识别验证)

> (2023.12.25 面部识别验证暂未发现可行解决方案, 建议不使用本插件)
1. 下载并安装插件: xposed.com.surcumference.fingerprintpay.release.apk
2. 在Xposed管理器启用插件
3. 重启手机
4. Enjoy

## 设置入口
| 软件名称 | 路径 |
| ----- | -------------------------------- |
| 支付宝 | 我的 --> 设置 --> 支付设置 --> 指纹设置 |
| 淘宝   | 我的淘宝 --> 设置 --> 支付设置 --> 指纹设置|
| 微信   | 我 --> 设置 --> 指纹设置 |
| QQ     | 头像 --> 设置 --> 指纹设置|
| 云闪付 | 我的 --> 设置 --> 指纹设置 |


## 详细教程
1. [支付宝](./doc/Alipay)
2. [淘宝](./doc/Taobao)
3. [微信](./doc/WeChat)
4. [QQ](./doc/QQ)
5. [云闪付](./doc/UnionPay)

## 常见问题
1. 插件已安装, 但在微信或支付宝中看不见菜单?\
   3.1 请逐个检查支付宝、淘宝、微信的菜单项， 是否有任何一个已激活\
   3.2 请同时安装其它插件, 确保框架是正常的工作的\
   3.3 尝试, 取消勾选插件, 再次勾选插件, 关机, 再开机(仅旧版Xposed需要, LSPosed 以及 Magisk模块不需要)
2. Xposed版只能使用play版本云闪付, 否则打开闪退! riru, zygisk版本暂未发现相关问题

## 致谢
* [eritpchy/FingerprintPay](https://github.com/eritpchy/FingerprintPay) (上游项目, 原作者 eritpchy)
* [FingerprintIdentify](https://github.com/uccmawei/FingerprintIdentify) (Awei)
* [MagiskModuleTemplate](https://github.com/RikkaApps/Riru-ModuleTemplate) (Rikka)
* [Riru](https://github.com/RikkaApps/Riru)
* [EdXposed](https://github.com/ElderDrivers/EdXposed)
* [Magisk](https://github.com/topjohnwu/Magisk)
* [WechatFp](https://github.com/dss16694/WechatFp)
* [Zygisk Next](https://github.com/Dr-TSNG/ZygiskNext)
* [APatch](https://github.com/bmax121/APatch)
* [Magisk Delta](https://huskydg.github.io/magisk-files/)
* [LSPosed](https://github.com/LSPosed/LSPosed)

## 提示
1. 本分支不包含任何网络功能.
2. 支付宝、淘宝、微信、QQ、云闪付随意升级新版本可能不兼容
3. 自4.7.4版本开始, 为减少打扰, 非紧急更新暂缓推送
4. Magisk Delta + Zygisk Next 组合 截止2023年11月8日目前这两软件尚未互相适配, 切勿尝试!
5. Magisk 本身自带Zygisk功能, 切勿尝试 Magisk + Zygisk Next 这么无聊的组合
6. 自5.0.0版本开始, 如果您**每次**(请注意, 是**每次**!)都识别出错第一次, 属于不正常现象, 正常现象应为首次出错一次,后续正常, 您可以删除系统指纹再重新添加并重新录入支付密码尝试
7. Zygisk Next 需要开启"遵守排除列表", 如果取消, 会导致框架全局排除列表失效. 不保证每个框架都如上述表现, 具体以自己测试结果为准
8. 目前已知人脸出现的概率会随着你的设备的风控等级升高而增加, 比如启用了LSPosed而没对指定应用加入排除列表
9. 由于本人主用APatch进行开发测试, 因此优先推荐使用APatch, KSU相关问题只能延后处理, 或者看社区有没有解决方案, 理论上他们都是同一个东西

