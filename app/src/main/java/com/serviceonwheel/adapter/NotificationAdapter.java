package com.serviceonwheel.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serviceonwheel.R;
import com.serviceonwheel.activity.NotificationDetailActivity;
import com.serviceonwheel.model.NotificationList;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.DataObjectHolder> {

    private ArrayList<NotificationList> dataset;

    private Context context;

    private OnClickListener onClickListener;

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, int witch);
    }

    public NotificationAdapter(ArrayList<NotificationList> data, Context context) {
        this.dataset = data;
        this.context = context;
    }

    static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        ImageView image;
        TextView tvOffertitle;
        TextView etaddedOn;
        RelativeLayout relaedroot;

        DataObjectHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgrowoffers);
            tvOffertitle = itemView.findViewById(R.id.tvOffertitle);
            etaddedOn = itemView.findViewById(R.id.etaddedOn);
            relaedroot = itemView.findViewById(R.id.relofferroot);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {

        holder.tvOffertitle.setText(dataset.get(holder.getAdapterPosition()).getTitle());
        holder.etaddedOn.setText(dataset.get(holder.getAdapterPosition()).getAdded_on());
        Glide.with(context)
                .load(dataset.get(holder.getAdapterPosition()).getImage())
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(position, 0);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);

    }
}