package com.pfc.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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
        private final TextView tvMail;

        public ViewHolder(View v) {
            super(v);

            tvTitle = v.findViewById(R.id.tvReqTitle);
            tvReqStatusLabel = v.findViewById(R.id.tvReqStatusLabel);
            tvStatus = v.findViewById(R.id.tvReqStatus);
            ivReqImg = v.findViewById(R.id.ivReqImg);
            tvMail = v.findViewById(R.id.tvMail);

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

        public TextView getTvMail() {
            return tvMail;
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
        holder.getTvMail().setText(listReq.get(position).getMail());

        String active = holder.itemView.getContext().getResources().getString(R.string.active);
        String inactive = holder.itemView.getContext().getResources().getString(R.string.inactive);
        String status = ((boolean)listReq.get(position).isStatus())? active:inactive;
        holder.getTvStatus().setText(status);

        Drawable resourceId = ResourcesCompat.getDrawable(holder.itemView.getContext().getResources(),
                                                            R.drawable.whatdo1920,
                                                        null);
        holder.getIvReqImg().setImageDrawable(resourceId);
        holder.itemView.setOnClickListener( v -> listener.accept(listReq.get(position)) );
    }

    @Override
    public int getItemCount() {
        return listReq.size();
    }
}