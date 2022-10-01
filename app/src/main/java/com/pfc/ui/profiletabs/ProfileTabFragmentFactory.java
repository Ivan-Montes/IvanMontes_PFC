package com.pfc.ui.profiletabs;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ProfileTabFragmentFactory {

    public static final int PROFILEFRAGMENT = 1;
    public static final int SETTINGSFRAGMENT = 2;
    public static final int SESIONFRAGMENT = 3;

    @Nullable
    public static Fragment getFragment(int i, String email){

        switch (i) {

            case PROFILEFRAGMENT:
                return new ProfileFragment(email);

            case SETTINGSFRAGMENT:
                return new SettingsFragment();

            case SESIONFRAGMENT:
                return new SesionFragment();

            default:
                return null;
        }
    }
}