package com.pfc.ui.popups;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.pfc.R;

public class DialogFragRequestSimpleData extends DialogFragment {

        public static String TAG = "DialogFragRequestSimpleData";

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
                String sender = "";
                String inputText = "";

                if ( getArguments() != null ){
                       if( !getArguments().getString("sender").isEmpty() ){
                               sender =  getArguments().getString("sender");
                       }
                        if( !getArguments().getString("oldValue").isEmpty() ){
                                inputText =  getArguments().getString("oldValue");
                        }
                }
                //Effectively final values
                String finalSender = sender;
                String finalInputText = inputText;

                LayoutInflater inflater = requireActivity().getLayoutInflater();
                View viewModal = inflater.inflate(R.layout.dialog_edit_data_profile, null);
                EditText etDato = viewModal.findViewById(R.id.etDataProfile);
                etDato.setText(inputText);

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(viewModal);
                builder.setMessage(R.string.write_new_data)
                        .setPositiveButton(R.string.acep,(dialog, id)-> {

                                if ( !finalSender.isEmpty() ){
                                        replyToParentFrag(finalSender, etDato.getText().toString(), finalInputText);
                                }
                        })
                        .setNegativeButton(R.string.cancel,((dialog, which) -> {

                        }));

                return builder.create();
        }

        private void replyToParentFrag(String sender, String info, String oldValue){
                Bundle result = new Bundle();
                result.putString("newData", info);
                result.putString("sender", sender);
                result.putString("oldValue",oldValue);
                getParentFragmentManager().setFragmentResult(sender, result);
        }

}//End
