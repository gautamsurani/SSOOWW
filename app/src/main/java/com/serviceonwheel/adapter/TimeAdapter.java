package com.serviceonwheel.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.model.TimeList;

import java.util.ArrayList;


public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.MyViewHolder> {

    private ArrayList<TimeList> dataSet;
    Context context;
    int row_index = 0;

    OnClickListener onClickListener;

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTime;
        LinearLayout llTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvtime);
            llTime = itemView.findViewById(R.id.llTime);
        }
    }

    public TimeAdapter(ArrayList<TimeList> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_time, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        holder.tvTime.setText(dataSet.get(listPosition).getDisplay_name());

        holder.llTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = listPosition;
                notifyDataSetChanged();
            }
        });

        if (row_index == listPosition) {
            onClickListener.onClick(listPosition);
        }

        if (row_index == listPosition) {
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.white));
            holder.llTime.setBackground(context.getResources().getDrawable(R.drawable.btn_backgrond));
        } else {
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.color_hint));
            holder.llTime.setBackground(context.getResources().getDrawable(R.drawable.unselected_time_bg));
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}