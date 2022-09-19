package com.pfc.ui.requestfrag;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.pfc.R;
import com.pfc.pojos.Request;
import com.pfc.support.Tools;

public class DetailRequestFragment extends DialogFragment {

    private final Request request;

    public DetailRequestFragment(Request request) {
        this.request = request;
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View viewModal = inflater.inflate(R.layout.dialog_detailrequest, null);
        viewModal.setOnClickListener(l->dismissAllowingStateLoss());

        TextView tvIdDoc = viewModal.findViewById(R.id.tvIdDoc);
        TextView tvRequestor = viewModal.findViewById(R.id.tvDetailRequestor);
        TextView tvTitle = viewModal.findViewById(R.id.tvDetailTitle);
        TextView tvLocation = viewModal.findViewById(R.id.tvDetailLocation);
        TextView tvStatus = viewModal.findViewById(R.id.tvDetailStatus);
        TextView tvDate = viewModal.findViewById(R.id.tvDetailCreationDate);
        TextView tvDescription = viewModal.findViewById(R.id.tvDetailDescription);

        tvIdDoc.setText(String.valueOf(request.getIdDoc()));
        tvRequestor.setText(request.getRequestor());
        tvTitle.setText(request.getTitle());
        tvLocation.setText( getResources().getString(R.string.lat_long, String.valueOf(request.getLocation().getLatitude()),String.valueOf(request.getLocation().getLongitude())) );
        tvStatus.setText((request.isStatus())?getResources().getString(R.string.active):getResources().getString(R.string.inactive));
        tvDate.setText(Tools.fullTimeStampToEasy(request.getCreationDate()));
        tvDescription.setText(request.getDescription());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(viewModal);
        builder.setTitle(R.string.detail_req);

        return builder.create();
    }


}