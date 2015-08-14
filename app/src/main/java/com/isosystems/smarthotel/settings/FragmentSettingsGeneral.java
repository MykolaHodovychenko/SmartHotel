package com.isosystems.smarthotel.settings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.isosystems.smarthotel.R;

public class FragmentSettingsGeneral extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_general);

        Preference back_button = (Preference)findPreference("button_back");
        back_button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().finish();
                return true;
            }
        });

        Preference quit_button = (Preference)findPreference("button_quit");
        quit_button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Activity a = (Activity)getActivity();
                a.finishAffinity();
                return true;
            }
        });

    }
}
