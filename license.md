<!-- Modified by mqmqgo, 2026-09-25: replaced upstream agreement with a GPL-2.0 notice -->
# 指纹支付 AES-256版 - 使用须知

版权所有 (C) eritpchy (原作者, FingerprintPay)
修改部分版权所有 (C) 2026 mqmqgo

本软件是 eritpchy/FingerprintPay 的非官方修改版, 由 mqmqgo 维护, 与原作者无关, 也不受原作者支持.

本软件是自由软件, 按 GNU 通用公共许可证第 2 版 (GPL-2.0) 发布. 您可以在该许可证的条款下重新分发和/或修改本软件.

本软件不提供任何担保 (NO WARRANTY), 包括但不限于适销性或特定用途适用性的默示担保. 使用风险由您自行承担.

- 您的支付密码只保存在本机, 并用硬件 Android Keystore 中的 AES-256-GCM 密钥加密, 每次使用都需要强生物识别认证.
- 本软件不包含任何网络功能, 不收集、不上传任何数据.
- 任何识别失败都会回退到原生密码输入.

源代码: https://github.com/MQMQgo/FingerprintPay (完整许可证见源码中的 LICENSE 与 NOTICE.md)
