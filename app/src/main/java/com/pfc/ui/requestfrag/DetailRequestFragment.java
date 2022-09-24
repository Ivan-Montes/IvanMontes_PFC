package com.pfc.ui.requestfrag;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.pfc.R;
import com.pfc.db.DbLittleHelperFactory;
import com.pfc.db.FireDbLittleHelper;
import com.pfc.pojos.Request;
import com.pfc.support.FirestoreCallbackBool;
import com.pfc.support.Tools;
import com.pfc.ui.popups.Pop;

public class DetailRequestFragment extends DialogFragment {

    private final Request request;
    public static String TAG = "DetailRequestFragment";

    public DetailRequestFragment(Request request) {
        this.request = request;
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View viewModal = inflater.inflate(R.layout.dialog_detailrequest, null);
        viewModal.setOnClickListener(l->dismissAllowingStateLoss());

        TextView tvIdDoc = viewModal.findViewById(R.id.tvIdDoc);
        TextView tvTitle = viewModal.findViewById(R.id.tvDetailTitle);
        TextView tvLocation = viewModal.findViewById(R.id.tvDetailLocation);
        TextView tvStatus = viewModal.findViewById(R.id.tvDetailStatus);
        TextView tvDate = viewModal.findViewById(R.id.tvDetailCreationDate);
        TextView tvDescription = viewModal.findViewById(R.id.tvDetailDescription);
        Button btSetStatus = viewModal.findViewById(R.id.btSetStatusR);

        tvIdDoc.setText(String.valueOf(request.getIdDoc()));
        tvTitle.setText(request.getTitle());
        tvLocation.setText( getResources().getString(R.string.lat_long, String.valueOf(request.getLocation().getLatitude()),String.valueOf(request.getLocation().getLongitude())) );
        tvStatus.setText((request.isStatus())?getResources().getString(R.string.active):getResources().getString(R.string.inactive));
        tvDate.setText(Tools.fullTimeStampToEasy(request.getCreationDate()));
        tvDescription.setText(request.getDescription());
        btSetStatus.setText((request.isStatus())?getResources().getString(R.string.desactivarp):getResources().getString(R.string.activarp));

        FirestoreCallbackBool callbackBool = b -> {
            if (b){
                Pop.showPopMsg(requireContext(),getResources().getString(R.string.succ));
                replyToParent();
            }else{
                Pop.showPopMsg(requireContext(),getResources().getString(R.string.perm_denied));
            }
            dismissAllowingStateLoss();
        };

        btSetStatus.setOnClickListener( l -> DbLittleHelperFactory.getDbLittleHelper(1)
                        .changeRequestStatus(request.getIdDoc(), !request.isStatus(), callbackBool));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(viewModal);
        builder.setTitle(R.string.detail_req);

        return builder.create();
    }

    private void replyToParent(){
        Bundle result = new Bundle();
        result.putBoolean("wasaaaap",true);
        getParentFragmentManager().setFragmentResult(TAG, result);
    }

}