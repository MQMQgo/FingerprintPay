# NOTICE

## 中文

**FingerprintPay AES-256版** 是 [eritpchy/FingerprintPay](https://github.com/eritpchy/FingerprintPay) 的**非官方修改版**.

- 原项目版权所有 (C) eritpchy 及其贡献者.
- 修改部分版权所有 (C) 2026 mqmqgo.
- 本修改版由 mqmqgo 维护, **与原作者无关, 也不受原作者支持**. 请不要就本修改版的问题打扰原作者.
- 许可证: **GNU 通用公共许可证第 2 版 (GPL-2.0)**, 与上游相同, 全文见 [LICENSE](./LICENSE).

### 第三方组件
| 组件 | 许可证 | 版权 | 说明 |
| --- | --- | --- | --- |
| [FingerprintIdentify](https://github.com/uccmawei/FingerprintIdentify) (`3rdparty/FingerprintIdentify`) | MIT | Copyright (c) 2017 Awei | **已修改** (fork: https://github.com/MQMQgo/FingerprintIdentify, 分支 `main`) |
| [MagiskModuleTemplate](https://github.com/eritpchy/MagiskModuleTemplate) (`3rdparty/MagiskModuleTemplate`) | MIT | Copyright (c) 2020 Rikka | 未修改 (构建时由 `module/build.sh` 在副本上打补丁) |

模块 zip 根目录附带: `LICENSE` (GPL-2.0), `NOTICE.md`, `LICENSE-FingerprintIdentify-MIT.txt`, `LICENSE-MagiskModuleTemplate-MIT.txt`.

### 主要修改 (2026-09-25, v7.0.0)
1. 移除全部网络代码: 更新检查、友盟统计、WebActivity/网页许可协议、okhttp/okgo/gson 依赖; 清单中移除网络、电话、存储和安装权限; module.prop 中没有 updateJson.
2. 移除捐赠界面、求 Star 提示、QQ 群、帮助和官方网站入口, 以及 QQ 黑名单逻辑.
3. 支付密码只用硬件 Android Keystore (优先 StrongBox, 否则 TEE) 中的 AES-256-GCM 密钥加密. 每次加密和解密都必须通过 BIOMETRIC_STRONG 认证 (API 30+ 用 BiometricPrompt, API 23–29 用 FingerprintManager, 都带 CryptoObject); 指纹变更后密钥失效. 移除 ECB/ANDROID_ID 软件加密、三星和魅族 SDK; 旧密文不迁移.
4. 任何失败都会自动回退到微信原生密码输入, 带防重入保护; 支付流程入口有异常保护.
5. 新的模块 ID、名称、作者和版本 (7.0.0 / 40); 更换应用内协议为 GPL-2.0 声明; 在修改过的源文件顶部添加修改声明.
6. (2026-09-25, v7.0.1) 明文支付密码全程只保存在 char[]/byte[] 中, 不创建 String; 使用后 (或中止时) 立即擦除; 设置密码时输入框在使用后清空. 移除依赖上游密钥或外部上传 (WebDAV/gitee) 的 GitHub Actions 工作流.

### 源代码
完整源代码 (包括构建脚本) 见 https://github.com/MQMQgo/FingerprintPay (分支 `main`；v7.0.1 对应标签 `v7.0.1`), 子模块见 https://github.com/MQMQgo/FingerprintIdentify.

### 无担保声明
本程序是自由软件, 分发它是希望它有用, 但**不提供任何担保**, 甚至不包括适销性或特定用途适用性的默示担保. 详见 GPL-2.0 第 11、12 条. 使用本软件保存和自动输入支付密码的风险 (包括资金风险) 由使用者自行承担.

## English

**FingerprintPay AES-256** is an **unofficial modified version** of [eritpchy/FingerprintPay](https://github.com/eritpchy/FingerprintPay).
Original work copyright (C) eritpchy and contributors; modifications copyright (C) 2026 mqmqgo.
It is maintained by mqmqgo and is **not affiliated with or supported by the original author**.

- License: GPL-2.0 (same as upstream), see [LICENSE](./LICENSE).
- Third-party: FingerprintIdentify (MIT, Copyright (c) 2017 Awei, **modified**); MagiskModuleTemplate (MIT, Copyright (c) 2020 Rikka).
- Main changes (2026-09-25, v7.0.0): all network code removed; donate UI removed; the payment password is encrypted only with a hardware-backed, biometric-bound (BIOMETRIC_STRONG) AES-256-GCM Android Keystore key; any failure falls back to the app's native password input; new module id `zygisk_fingerprintpay_wechat_aes256`.
- v7.0.1 (2026-09-25): the decrypted password is kept only in char[]/byte[] (never a String) and wiped right after use or on abort; the password input field is cleared after setup.
- Source code: https://github.com/MQMQgo/FingerprintPay
- **NO WARRANTY.** This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License v2 for details.
