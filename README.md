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

## 安装 (Release zip)
1. 从本仓库 [Releases](https://github.com/MQMQgo/FingerprintPay/releases) 下载 `zygisk-fingerprintpay-wechat-aes256-v7.0.1-release.zip`, 并核对 SHA256SUMS.txt
2. 如果装过**官方模块**或**旧版加固模块** (`zygisk-module-xfingerprint-pay-wechat`), 请先在 KernelSU 中**卸载**并重启. 本模块 ID 是新的 (`zygisk_fingerprintpay_wechat_aes256`), 不卸载会导致两个模块同时加载
3. KernelSU → 模块 → 从本地安装 → 选择 zip, 然后重启 (Magisk + Zygisk 同理)
4. 微信 → 我 → 设置 → 指纹支付 → 输入支付密码 (从官方版迁移过来的必须重新输入; 从 7.0.0 升级不需要)
5. 要求: 有指纹硬件, Android 6.0+ (Android 11+ 使用系统 BiometricPrompt)

## 构建
```
./gradlew :app:assembleRelease
cd module && bash ./build.sh :module:assembleRelease ./src/gradle/wechat.gradle Zygisk
# 输出: module/build/release/*.zip
```

## 致谢
* [eritpchy/FingerprintPay](https://github.com/eritpchy/FingerprintPay) (上游项目, 原作者 eritpchy)
* [FingerprintIdentify](https://github.com/uccmawei/FingerprintIdentify) (Awei)
* [MagiskModuleTemplate](https://github.com/RikkaApps/Riru-ModuleTemplate) (Rikka)
* [Magisk](https://github.com/topjohnwu/Magisk), [KernelSU](https://github.com/tiann/KernelSU), [Zygisk Next](https://github.com/Dr-TSNG/ZygiskNext), [WechatFp](https://github.com/dss16694/WechatFp)

更多信息请参阅原始项目：https://github.com/eritpchy/FingerprintPay
