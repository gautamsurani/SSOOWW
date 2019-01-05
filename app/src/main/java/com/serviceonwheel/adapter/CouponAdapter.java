package com.serviceonwheel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.model.CouponList;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.DataObjectHolder> {

    private List<CouponList> data_set;

    private Context context;

    private OnClickListener onClickListener;

    private String hideApply;

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, int witch);
    }

    public CouponAdapter(List<CouponList> data, Context context, String hideApply) {
        this.data_set = data;
        this.context = context;
        this.hideApply = hideApply;
    }

    static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCode, tvDesc, tvApply, tvEMsg;
        LinearLayout llMain;

        DataObjectHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            llMain = itemView.findViewById(R.id.llMain);
            tvApply = itemView.findViewById(R.id.tvApply);
            tvEMsg = itemView.findViewById(R.id.tvEMsg);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_coupon, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataObjectHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tvCode.setText(data_set.get(holder.getAdapterPosition()).getCode());
        holder.tvDesc.setText(data_set.get(holder.getAdapterPosition()).getMsg());

        if (data_set.get(holder.getAdapterPosition()).getExpire_msg().isEmpty()) {
            holder.tvEMsg.setVisibility(View.GONE);
            holder.tvEMsg.setText("");
        } else {
            holder.tvEMsg.setVisibility(View.VISIBLE);
            holder.tvEMsg.setText(data_set.get(holder.getAdapterPosition()).getExpire_msg());
        }

        if (hideApply.equalsIgnoreCase("Yes")) {
            holder.tvApply.setVisibility(View.GONE);
        } else {
            holder.tvApply.setVisibility(View.VISIBLE);
        }

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(position, 0);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data_set.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);

    }
}