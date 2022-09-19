package com.pfc.warehouse;

import android.Manifest;

import androidx.annotation.NonNull;

import java.util.Arrays;

public enum EnumPermission {

    CAMERA(Manifest.permission.CAMERA, "CAMERA"),
    READ(Manifest.permission.READ_EXTERNAL_STORAGE, "READ_EXTERNAL_STORAGE"),
    WRITE(Manifest.permission.WRITE_EXTERNAL_STORAGE, "WRITE_EXTERNAL_STORAGE"),
    FINELOC(Manifest.permission.ACCESS_FINE_LOCATION, "ACCESS_FINE_LOCATION"),
    COARSELOC(Manifest.permission.ACCESS_COARSE_LOCATION,"ACCESS_COARSE_LOCATION");

    private final String permiso;
    private final String name;

    EnumPermission(String permiso, String name) {
        this.permiso = permiso;
        this.name = name;
    }

    public static String[] getArrayPermisoFull(){
        return Arrays.stream(EnumPermission.values()).map(e->e.permiso).toArray(String[]::new);
    }

    public static String[] getArrayPermisoGps(){
        return new String[]{FINELOC.permiso, COARSELOC.permiso};
    }

    public static String[] getArrayPermisoAvatar(){
        return new String[]{CAMERA.permiso,READ.permiso, WRITE.permiso};
    }


    @NonNull
    @Override
    public String toString() {
        return "EnumPermission{" +
                "name='" + name + '\'' +
                '}';
    }

}//END