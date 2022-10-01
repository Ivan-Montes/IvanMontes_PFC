package com.pfc.ui.popups;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogFragBox extends DialogFragment {

    public static String TAG = "DialogFragBox";
    private final String message;
    private final String title;


    public DialogFragBox(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(this.title);
        builder.setMessage(this.message);

        return builder.create();
    }

}
