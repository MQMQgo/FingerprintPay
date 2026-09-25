// Modified by mqmqgo, 2026-09-25: removed donate/update/network strings, added key-invalidated strings
package com.surcumference.fingerprint;

import java.util.Locale;

/**
 * Created by Jason on 2017/9/17.
 */

public class Lang {

    private static int sLang;

    public static final int LANG_ZH_CN = 0;
    public static final int LANG_ZH_TW = 1;
    public static final int LANG_EN = 2;

    static {
        Locale locale = Locale.getDefault();
        if (locale.getLanguage().toLowerCase().contains("zh")) {
            String country = locale.getCountry().toLowerCase();
            if (country.contains("tw") || country.contains("hk")) {
                sLang = LANG_ZH_TW;
            } else {
                sLang = LANG_ZH_CN;
            }
        } else {
            sLang = LANG_EN;
        }
    }

    public static String getString(int res) {
        switch (res) {
            case R.string.app_name:
                return tr("指纹支付", "指纹支付", "Fingerprint Pay");
            case R.id.settings_title_license:
                return tr("许可协议", "許可協議", "License");
            case R.id.settings_title_version:
                return tr("当前版本", "当前版本", "Version");
            case R.id.settings_sub_title_license:
                return tr("查看许可协议", "查看許可協議", "Check the License Agreement");
            case R.id.cancel:
                return tr("取消", "取消", "Cancel");
            case R.id.ok:
                return tr("确定", "确定", "OK");
            case R.id.settings_title_taobao:
                return tr("淘宝", "淘寶", "Taobao");
            case R.id.settings_title_alipay:
                return tr("支付宝", "支付寶", "Alipay");
            case R.id.settings_title_wechat:
                return tr("微信", "微信", "WeChat");
            case R.id.settings_title_qq:
                return tr("腾讯QQ", "騰訊QQ", "Tencent QQ");
            case R.id.settings_title_unionpay:
                return tr("云闪付", "雲閃付", "Union Pay");
            case R.id.enter_password:
                return tr("使用密码", "使用密碼", "Enter password");
            case R.id.settings_title_switch:
                return tr("启用", "啟用", "Enable");
            case R.id.settings_title_password:
                return tr("支付密码", "支付密碼", "Payment Password");
            case R.id.settings_title_no_fingerprint_icon:
                return tr("显示指纹图标", "顯示指紋圖標", "Fingerprint Icon");
            case R.id.settings_title_advance:
                return tr("通用设置", "一般选项", "General");
            case R.id.settings_title_volume_down_fingerprint_temporary_disable:
                return tr("音量\uD83D\uDC47切换密码输入", "音量\uD83D\uDC47禁用切換密碼輸入", "Vol- for password input");
            case R.id.settings_title_start_logcat:
                return tr("开始记录日志", "開始記錄日誌", "Start logging");
            case R.id.settings_title_stop_logcat:
                return tr("停止记录日志", "停止記錄日誌", "Stop logging");
            case R.id.settings_sub_title_switch_alipay:
                return tr("启用支付宝指纹支付", "啟用支付宝指紋支付", "Enable fingerprint payment for Alipay");
            case R.id.settings_sub_title_switch_wechat:
                return tr("启用微信指纹支付", "啟用微信指紋支付", "Enable fingerprint payment for WeChat");
            case R.id.settings_sub_title_switch_qq:
                return tr("启用QQ指纹支付", "啟用QQ指紋支付", "Enable fingerprint payment for QQ");
            case R.id.settings_sub_title_switch_unionpay:
                return tr("启用云闪付指纹支付", "啟用雲閃付指紋支付", "Enable fingerprint payment for Union Pay");
            case R.id.settings_sub_title_password_alipay:
                return tr("请输入支付宝的支付密码, 密码会加密后保存, 请放心", "請輸入支付宝的支付密碼, 密碼會加密后保存, 請放心", "Please enter your Payment password");
            case R.id.settings_sub_title_password_wechat:
                return tr("请输入微信的支付密码, 密码会加密后保存, 请放心", "請輸入微信的支付密碼, 密碼會加密后保存, 請放心", "Please enter your Payment password");
            case R.id.settings_sub_title_no_fingerprint_icon:
                return tr("非屏下指纹手机需要显示指纹图标", "非屏下指紋手機需要顯示指紋圖標", "Non IN-DISPLAY fingerprint phone need to display the fingerprint icon");
            case R.id.settings_sub_title_password_qq:
                return tr("请输入QQ的支付密码, 密码会加密后保存, 请放心", "請輸入QQ的支付密碼, 密碼會加密后保存, 請放心", "Please enter your Payment password");
            case R.id.settings_sub_title_password_unionpay:
                return tr("请输入云闪付的支付密码, 密码会加密后保存, 请放心", "請輸入雲閃付的支付密碼, 密碼會加密后保存, 請放心", "Please enter your Payment password");
            case R.id.settings_sub_title_advance:
                return tr("指纹图标、音量键临时禁用、日志...", "指紋圖標、音量鍵臨時禁用、日誌...", "Fingerprint icon, volume key disable, logs...");
            case R.id.settings_sub_title_volume_down_fingerprint_temporary_disable:
                return tr("按下按键会临时禁用指纹支付1分钟(仅应用内认证有效)", "按下按鍵會臨時禁用指紋支付1分鐘(僅應用內認證有效)", "Pressing the button will temporarily disable fingerprint payment for 1 minute (only valid for in-app authentication)");
            case R.id.settings_sub_title_start_logcat:
                return tr("开始 --> 你的表演 --> 停止 --> 发送给开发者", "開始 --> 你的表演 --> 停止 --> 發送給開發者", "Start --> Payment operation --> Stop --> Send to developer");
            case R.id.settings_sub_title_stop_logcat:
                return tr("开始 --> 你的表演 --> 停止 --> 发送给开发者", "開始 --> 你的表演 --> 停止 --> 發送給開發者", "Start --> Payment operation --> Stop --> Send to developer");
            case R.id.fingerprint_verification:
                return tr("请验证指纹", "請驗證指紋", "Fingerprint verification");
            case R.id.wechat_general:
                return tr("通用", "一般", "General");
            case R.id.app_settings_name:
                return tr("指纹设置", "指紋設置", "Fingerprint");
            case R.id.wechat_payview_fingerprint_title:
                return tr("　请验证指纹　", "　請驗證指紋　", "　Verify fingerprint　");
            case R.id.wechat_payview_password_title:
                return tr("请输入支付密码", "請輸入付款密碼", "Enter payment password");
            case R.id.wechat_payview_password_switch_text:
                return tr("使用密码", "使用密碼", "Password");
            case R.id.wechat_payview_fingerprint_switch_text:
                return tr("使用指纹", "使用指紋", "Fingerprint");
            case R.id.qq_payview_fingerprint_title:
                return tr("请验证指纹", "請驗證指紋", "Verify fingerprint");
            case R.id.qq_payview_password_title:
                return tr("请输入支付密码", "請輸入付款密碼", "Enter payment password");
            case R.id.qq_payview_password_switch_text:
                return tr("使用密码", "使用密碼", "Password");
            case R.id.qq_payview_fingerprint_switch_text:
                return tr("使用指纹", "使用指紋", "Fingerprint");
            case R.id.disagree:
                return tr("不同意", "不同意", "Disagree");
            case R.id.agree:
                return tr("同意", "同意", "I agree");

            case R.id.toast_fingerprint_not_match:
                return tr("指纹识别失败", "指紋識別失敗", "Fingerprint NOT MATCH");
            case R.id.toast_fingerprint_retry_ended:
                return tr("多次尝试错误，请使用密码输入", "多次嘗試錯誤，請使用密碼輸入", "Too many incorrect verification attempts, switch to password verification");
            case R.id.toast_fingerprint_unlock_reboot:
                return tr("系统限制，重启后必须验证密码后才能使用指纹验证", "系統限制，重啟後必須驗證密碼後才能使用指紋驗證", "Reboot and enable fingerprint verification with your PIN");
            case R.id.toast_fingerprint_not_enable:
                return tr("系统指纹功能未启用", "系統指紋功能未啟用", "Fingerprint verification has been closed by system");
            case R.id.toast_fingerprint_password_enc_success:
                return tr("支付密码加密成功", "支付密碼加密成功", "Payment password encryption successful");
            case R.id.toast_fingerprint_key_invalidated:
                return tr("指纹信息已变更或密钥已失效, 已清除保存的支付密码, 请重新设置支付密码", "指紋信息已變更或密鑰已失效, 已清除保存的支付密碼, 請重新設置支付密碼", "Biometric enrollment changed or key invalidated. The saved payment password was cleared, please set it again");
            case R.id.toast_fingerprint_not_supported:
                return tr("当前系统版本不支持硬件密钥保护的指纹支付 (需要 Android 6.0+)", "當前系統版本不支持硬件密鑰保護的指紋支付 (需要 Android 6.0+)", "Hardware-backed fingerprint payment requires Android 6.0+");
            case R.id.toast_fingerprint_password_enc_failed:
                return tr("支付密码加密失败, 未保存", "支付密碼加密失敗, 未保存", "Payment password encryption failed, nothing was saved");
            case R.id.toast_fingerprint_password_dec_failed:
                return tr("支付密码解密失败, 请重新设定支付密码", "支付密码解密失败, 请重新设定支付密码", "Decryption of payment password failed, please reset the payment password");
            case R.id.toast_fingerprint_operation_cancel:
                return tr("操作已取消", "操作已取消", "The operation has been canceled");
            case R.id.toast_fingerprint_temporary_disabled:
                return tr("指纹支付已临时禁用1分钟", "指紋支付已臨時禁用1分鐘", "Fingerprint payment has been temporarily disabled for 1 minute");
            case R.id.toast_password_not_set_alipay:
                return tr("未设定支付密码，请前往設置->指紋設置中设定支付宝的支付密码", "未設定支付密碼，請前往設置 -> 指紋設置中設定支付寶的支付密碼", "Payment password not set, please goto Settings -> Fingerprint to enter you payment password");
            case R.id.toast_password_not_set_taobao:
                return tr("未设定支付密码，请前往設置->指紋設置中设定淘宝的支付密码", "未設定支付密碼，請前往設置 -> 指紋設置中設定淘寶的支付密碼", "Payment password not set, please goto Settings -> Fingerprint to enter you payment password");
            case R.id.toast_password_not_set_wechat:
                return tr("未设定支付密码，请前往設置->指紋設置中设定微信的支付密码", "未設定支付密碼，請前往設置 -> 指紋設置中設定微信的支付密碼", "Payment password not set, please goto Settings -> Fingerprint to enter you payment password");
            case R.id.toast_password_not_set_qq:
                return tr("未设定支付密码，请前往設置->指紋設置中设定QQ的支付密码", "未設定支付密碼，請前往設置 -> 指紋設置中設定QQ的支付密碼", "Payment password not set, please goto Settings -> Fingerprint to enter you payment password");
            case R.id.toast_password_not_set_generic:
                return tr("未设定支付密码，请前往設置->指紋設置中设定支付密码", "未設定支付密碼，請前往設置 -> 指紋設置中設定支付密碼", "Payment password not set, please goto Settings -> Fingerprint to enter you payment password");
            case R.id.toast_password_not_set_switch_on_failed:
                return tr("启用失败, 请先设定支付密码", "啟用失敗, 請先設定支付密碼", "Enabled failed, please set a payment password first");
            case R.id.toast_password_auto_enter_fail:
                return tr("Oops.. 输入失败了. 请手动输入密码", "Oops.. 輸入失敗了. 請手動輸入密碼", "Oops... auto input failure, switch to manual input");
            case R.id.toast_need_qq_7_2_5:
                return tr("您的QQ版本过低, 不支持指纹功能, 请升级至7.2.5以上的版本", "您的QQ版本過低, 不支持指紋功能, 請升級至7.2.5以上的版本", "Your QQ version is too low, does not support the fingerprint function, please upgrade to version 7.2.5 and above");
            case R.id.toast_start_logging:
                return tr("请开始你的表演, 日志已开始记录\n日志路径: %s", "請開始你的表演, 日誌已開始記錄\n日誌路徑: %s", "Star logging\nlog path: %s");
            case R.id.toast_stop_logging:
                return tr("表演结束, 请将日志文件分享给开发者\n日志路径: %s", "表演结束, 请将日志文件分享给开发者\n日誌路徑: %s", "Stop logging\nlog path: %s");
            case R.id.message_version_not_supported:
                return tr("当前应用版本%s(%s)与模块版本%s不兼容，请反馈问题\uD83D\uDC1B并使用兼容的模块版本", "當前應用版本%s(%s)與模塊版本%s不兼容，請反饋問題\uD83D\uDC1B並使用兼容的模塊版本", "The current application version %s(%s) is incompatible with module version %s. Please report this issue \uD83D\uDC1B and use a compatible module version.");
            case R.id.template:
                return tr("", "", "");
        }
        return "";
    }

    private static String tr(String ...c) {
        return c[sLang];
    }
}
