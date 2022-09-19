package com.pfc.support;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tools {

    @NonNull
    public static String timeStampToEasy(@NonNull Timestamp timestamp){
        String result;
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("es","ES"));
        result = sdf.format(date);
        return result;
    }

    @NonNull
    public static String fullTimeStampToEasy(@NonNull Timestamp timestamp){
        String result;
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("es","ES"));
        result = sdf.format(date);
        return result;
    }


}//End
