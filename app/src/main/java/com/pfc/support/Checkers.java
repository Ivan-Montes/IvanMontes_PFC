package com.pfc.support;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checkers {

    public static boolean checkEmail(String email){
        String regEx = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches() && email.length() <= 30;
    }

    public static boolean checkPass(@NonNull String pass){
        //Firestore set a min password long of 6 chars!!. Saw it while debugging
        return !pass.isEmpty() && pass.length() >= 6 && pass.length() <= 20;
    }

    public static boolean checkCity(@NonNull String city){

        return !city.isEmpty() && city.length() <= 30;
    }

    public static boolean checkPhone(@NonNull String phone){

        return !phone.isEmpty() && phone.length() <= 9;
    }

    public static boolean checkRequestTitle(@NonNull String title){

        return !title.isEmpty() && title.length() <= 30;
    }

    public static boolean checkRequestDescription(@NonNull String descrip){

        return !descrip.isEmpty() && descrip.length() <= 100;
    }


}
