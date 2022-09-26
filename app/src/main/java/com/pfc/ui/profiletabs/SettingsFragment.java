package com.pfc.ui.profiletabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pfc.R;

public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewFragment = inflater.inflate(R.layout.fragment_settings, container, false);

        init();

        return viewFragment;
    }

    private void init(){
        launchExtraPreferencesFrag();
    }

    private void launchExtraPreferencesFrag(){
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.flExtra, new SettingsPreferenceFragment())
                .commit();
    }

}//End