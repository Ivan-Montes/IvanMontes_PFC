package com.pfc.ui.requestfrag;

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

public class NewRequestFragment extends DialogFragment {

    public static String TAG = "NewRequestFragment";

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View viewModal = inflater.inflate(R.layout.dialog_newrequest, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(viewModal);
        builder.setTitle(R.string.new_req)
                .setPositiveButton(R.string.save,(dialog, id)-> {
                    EditText etTitle = viewModal.findViewById(R.id.etNewReqTitle);
                    EditText etMulText = viewModal.findViewById(R.id.etNewReqDescription);
                    if ( !etTitle.getText().toString().isEmpty() &&
                            !etMulText.getText().toString().isEmpty() ){
                        replyToParent(etTitle.getText().toString(), etMulText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel,((dialog, which) -> {

                }));

        return builder.create();
    }

    private void replyToParent(String title, String descrip){
        Bundle result = new Bundle();
        result.putString("title",title);
        result.putString("descrip",descrip);
        getParentFragmentManager().setFragmentResult(TAG, result);
    }


}//End
