package com.pfc.ui.profiletabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pfc.R;

public class SettingsFragment extends Fragment {


    private final String email;
    private View viewFragment;

    public SettingsFragment(String email){
        this.email = email;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewFragment = inflater.inflate(R.layout.fragment_settings, container, false);

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