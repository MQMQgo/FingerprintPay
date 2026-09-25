// Modified by mqmqgo, 2026-09-25: removed network/update/donate/QQ-group items and Biometric API toggle
package com.surcumference.fingerprint.activity;



import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.surcumference.fingerprint.BuildConfig;
import com.surcumference.fingerprint.Lang;
import com.surcumference.fingerprint.R;
import com.surcumference.fingerprint.adapter.PreferenceAdapter;
import com.surcumference.fingerprint.util.bugfixer.TagManagerBugFixer;
import com.surcumference.fingerprint.view.LicenseView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private PreferenceAdapter mListAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ListView listView = (ListView) findViewById(R.id.list);
        List<PreferenceAdapter.Data> list = new ArrayList<>();
        list.add(new PreferenceAdapter.Data(Lang.getString(R.id.settings_title_license), Lang.getString(R.id.settings_sub_title_license)));
        list.add(new PreferenceAdapter.Data(Lang.getString(R.id.settings_title_version), BuildConfig.VERSION_NAME));
        mListAdapter = new PreferenceAdapter(list);
        listView.setAdapter(mListAdapter);
        listView.setOnItemClickListener(this);
        TagManagerBugFixer.fix();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        PreferenceAdapter.Data data = mListAdapter.getItem(i);
        if (data == null || TextUtils.isEmpty(data.title)) {
            return;
        }
        if (Lang.getString(R.id.settings_title_license).equals(data.title)) {
            new LicenseView(this).withOnPositiveButtonClickListener((dialog, which) -> dialog.dismiss())
                    .withOnNegativeButtonClickListener((dialog, which) -> dialog.dismiss())
                    .showInDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings:
                SettingsActivity.open(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

