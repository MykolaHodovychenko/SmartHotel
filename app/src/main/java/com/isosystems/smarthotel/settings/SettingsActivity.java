package com.isosystems.smarthotel.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.isosystems.smarthotel.R;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);

        loadHeadersFromResource(R.xml.settings_headers, target);
    }

    @Override
    protected boolean isValidFragment (String fragmentName) {
        return true;
    }
}
