package com.pfc.support;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.stream.Stream;

public class PermissionHelper {

    public static boolean checkOnePermission(Context context,String permisoManifiesto){
        return PackageManager.PERMISSION_GRANTED == ContextCompat
                                        .checkSelfPermission(context, permisoManifiesto);
        }

    public static void requestOnePermission(Activity activity, String permisoManifiesto, int requetCode){
        ActivityCompat.requestPermissions(activity,new String[]{permisoManifiesto},requetCode);
    }

    public static boolean checkArrayPermission(Context context,String[] aPermission){

        return Stream.of(aPermission).allMatch( p -> checkOnePermission(context,p));

    }

    public static void requestArrayPermission(Activity activity, String[] aPermission,int requestCode){
        ActivityCompat.requestPermissions(activity,aPermission,requestCode);
    }


}//END
