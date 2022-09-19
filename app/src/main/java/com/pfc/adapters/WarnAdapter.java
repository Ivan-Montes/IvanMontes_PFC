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
import com.pfc.pojos.Warning;
import com.pfc.support.Tools;

import java.util.List;

//https://github.com/android/views-widgets-samples/tree/f22069a57d5df0b58ce0be08086c3e9db35870b8/RecyclerView
public class WarnAdapter extends RecyclerView.Adapter<WarnAdapter.ViewHolder>{
    private final List<Warning> listWarn;

    public WarnAdapter(List<Warning> listWarn) {
        this.listWarn = listWarn;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvLugar;
        private final TextView tvFechaInicio;
        private final TextView tvFechaFin;
        private final ImageView ivWarnImg;
        private final TextView tvActivo;
        private final TextView tvNivel;

        public ViewHolder(View v) {
            super(v);

            tvTitle = v.findViewById(R.id.tvWarnTitle);
            tvLugar = v.findViewById(R.id.tvLugar);
            tvFechaInicio = v.findViewById(R.id.tvFechaInicio);
            tvFechaFin = v.findViewById(R.id.tvFechaFin);
            ivWarnImg = v.findViewById(R.id.ivWarnImg);
            tvActivo = v.findViewById(R.id.tvActivo);
            tvNivel = v.findViewById(R.id.tvNivel);

            //v.setOnClickListener(v1 -> Log.d(TAG, "Element " + getAdapterPosition() + " clicked."));
        }

        public TextView getTvTitle() {
            return tvTitle;
        }
        public TextView getTvLugar() {return tvLugar;        }
        public TextView getTvFechaInicio() {            return tvFechaInicio;        }
        public TextView getTvFechaFin() {return tvFechaFin;        }
        public ImageView getIvWarnImg() {return ivWarnImg;}
        public TextView getTvActivo() {return tvActivo;        }
        public TextView getTvNivel() {return tvNivel;        }
    }

    @NonNull
    @Override
    public WarnAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.warn_line, parent, false);

        return new WarnAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WarnAdapter.ViewHolder holder, int position) {

        String titulo = holder.itemView.getContext().getResources()
                .getStringArray(R.array.warning_titles)[(int)listWarn.get(position).getTipo()];
        holder.getTvTitle().setText(titulo);
        holder.getTvLugar().setText(listWarn.get(position).getLugar());
        String fechaInicio = Tools.timeStampToEasy(listWarn.get(position).getFechaInicio());
        String fechaFin = Tools.timeStampToEasy(listWarn.get(position).getFechaFin());
        holder.getTvFechaInicio().setText(fechaInicio);
        holder.getTvFechaFin().setText(fechaFin);
        TypedArray aIntTyped = holder.itemView.getContext().getResources().obtainTypedArray(R.array.warning_img_int);
        int resourceId = aIntTyped.getResourceId((int)listWarn.get(position).getTipo(),0);
        holder.getIvWarnImg().setImageResource(resourceId);
        aIntTyped.recycle();

        String level = holder.itemView.getContext().getResources().getString(R.string.level) + ": "
                + (int) listWarn.get(position).getNivel();
        holder.getTvNivel().setText(level);
        String active = holder.itemView.getContext().getResources().getString(R.string.active);
        String inactive = holder.itemView.getContext().getResources().getString(R.string.inactive);
        String status = ((boolean)listWarn.get(position).isActivo())? active:inactive;
        holder.getTvActivo().setText(status);

    }

    @Override
    public int getItemCount() {
        return listWarn.size();
    }
}
