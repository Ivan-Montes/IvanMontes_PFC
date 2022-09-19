package com.pfc.adapters;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pfc.R;
import com.pfc.pojos.Request;

import java.util.List;
import java.util.function.Consumer;

public class ReqAdapter extends RecyclerView.Adapter<ReqAdapter.ViewHolder>{

    private final List<Request> listReq;
    private final Consumer<Request> listener;

    public ReqAdapter(List<Request> listReq, Consumer<Request> consumerReq) {
        this.listReq = listReq;
        this.listener = consumerReq;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvReqStatusLabel;
        private final TextView tvStatus;
        private final ImageView ivReqImg;

        public ViewHolder(View v) {
            super(v);

            tvTitle = v.findViewById(R.id.tvReqTitle);
            tvReqStatusLabel = v.findViewById(R.id.tvReqStatusLabel);
            tvStatus = v.findViewById(R.id.tvReqStatus);
            ivReqImg = v.findViewById(R.id.ivReqImg);

            //v.setOnClickListener(v1 -> Log.e(TAG, "Element " + getAdapterPosition() + " clicked."));
        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public TextView getTvStatus() {
            return tvStatus;
        }

        public ImageView getIvReqImg() {
            return ivReqImg;
        }

        public TextView getTvReqStatusLabel() {
            return tvReqStatusLabel;
        }
    }

    @NonNull
    @Override
    public ReqAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.req_line, parent, false);

        return new ReqAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReqAdapter.ViewHolder holder, int position) {
        holder.getTvTitle().setText(listReq.get(position).getTitle());
        holder.getTvReqStatusLabel().setText(holder.itemView.getContext().getResources().getString(R.string.status));

        String active = holder.itemView.getContext().getResources().getString(R.string.active);
        String inactive = holder.itemView.getContext().getResources().getString(R.string.inactive);
        String status = ((boolean)listReq.get(position).isStatus())? active:inactive;
        holder.getTvStatus().setText(status);

        TypedArray aIntTyped = holder.itemView.getContext().getResources().obtainTypedArray(R.array.warning_img_int);
        int resourceId = aIntTyped.getResourceId((int)2,1);
        holder.getIvReqImg().setImageResource(resourceId);
        aIntTyped.recycle();
        holder.itemView.setOnClickListener( v -> listener.accept(listReq.get(position)) );
    }

    @Override
    public int getItemCount() {
        return listReq.size();
    }
}