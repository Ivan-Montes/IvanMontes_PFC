package com.pfc.db;

import androidx.annotation.Nullable;

public class DbLittleHelperFactory {

    public static final int FIRE = 1;
    public static final int WATER = 2;

    @Nullable
    public static DbLittleHelper getDbLittleHelper(int i){
        switch (i) {

            case FIRE:
                return new FireDbLittleHelper();

            case WATER:
                //return new WaterDbLittleHelper();

            default:
                return null;
        }
    }
}//END