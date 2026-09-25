// Modified by mqmqgo, 2026-09-25: local GPL-2.0 notice, no WebView/network
package com.surcumference.fingerprint.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.surcumference.fingerprint.Lang;
import com.surcumference.fingerprint.LicenseText;
import com.surcumference.fingerprint.R;
import com.surcumference.fingerprint.util.DpUtils;
import com.surcumference.fingerprint.util.StyleUtils;

/**
 * Hardened: shows the license text bundled in the dex. No WebView, no network.
 * (Assets are not available in the Zygisk variant because only the dex is injected into the host.)
 */
public class LicenseView extends DialogFrameLayout {

    public LicenseView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LicenseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LicenseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ScrollView scrollView = new ScrollView(context);
        TextView textView = new TextView(context);
        int padding = DpUtils.dip2px(context, 16);
        textView.setPadding(padding, DpUtils.dip2px(context, 5), padding, padding);
        StyleUtils.apply(textView);
        textView.setTextIsSelectable(true);
        textView.setText(LicenseText.TEXT);
        scrollView.addView(textView);
        int maxHeight = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.6f);
        this.addView(scrollView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, maxHeight));
        withNegativeButtonText(Lang.getString(R.id.disagree));
        withPositiveButtonText(Lang.getString(R.id.agree));
    }

    @Override
    public String getDialogTitle() {
        return Lang.getString(R.id.settings_title_license);
    }
}
