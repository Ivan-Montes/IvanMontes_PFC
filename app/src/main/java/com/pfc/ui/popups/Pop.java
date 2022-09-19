package com.pfc.ui.popups;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.pfc.R;

public class Pop {


    public static void showPop(Context context){
        Toast t = Toast.makeText(context , R.string.warn_msg, Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();
    }

    public static void showPopMsg(Context context, String msg){
        Toast t = Toast.makeText(context ,msg,Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER,0,0);
        t.show();
    }

}//End