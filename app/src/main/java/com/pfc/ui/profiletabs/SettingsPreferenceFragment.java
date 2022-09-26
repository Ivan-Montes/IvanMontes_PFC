package com.pfc.ui.profiletabs;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.pfc.R;

public class SettingsPreferenceFragment extends PreferenceFragmentCompat {
    //https://www.geeksforgeeks.org/how-to-implement-preferences-settings-screen-in-android/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference_fragment);
    }

   @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

   }

}//End