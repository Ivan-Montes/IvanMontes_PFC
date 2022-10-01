package com.pfc.ui.popups;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.pfc.R;
import com.pfc.support.FirestoreCallbackBool;

public class DialogBox {

    public static void dialogErrorCredentials(Context context){
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setMessage(R.string.log_error_txt)
                .setTitle(R.string.log_error)
                .setPositiveButton(R.string.acep, (dialog, id) -> {
                });
        AlertDialog d = b.create();
        d.show();
    }

    public static void dialogErrorProcess(Context context){
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setMessage(R.string.proc_error_txt)
                .setTitle(R.string.log_error)
                .setPositiveButton(R.string.acep, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog d = b.create();
        d.show();
    }

    public static void confirmAction(Context context, FirestoreCallbackBool firestoreCallbackBool){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(R.string.areyousure);
        builder.setTitle(R.string.delete_user);
        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> firestoreCallbackBool.onCallback(true));
        builder.create().show();
    }

}//End