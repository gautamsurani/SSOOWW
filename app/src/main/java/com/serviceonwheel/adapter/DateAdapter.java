package com.serviceonwheel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.model.DateList;

import java.util.ArrayList;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder> {

    private ArrayList<DateList> dataSet;
    private Context context;
    int row_index = 0;

    private OnClickListener onClickListener;

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        TextView tvDate;
        LinearLayout llDate;

        MyViewHolder(View itemView) {
            super(itemView);

            this.tvDate = itemView.findViewById(R.id.tvDate);
            this.tvDay = itemView.findViewById(R.id.tvDay);
            this.llDate = itemView.findViewById(R.id.llDate);
        }
    }

    public DateAdapter(ArrayList<DateList> data, Context context) {
        this.dataSet = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_date, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int listPosition) {

        holder.tvDay.setText(dataSet.get(listPosition).getDisplay_date());

        holder.llDate.setOnClickListener(new View.OnClickListener() {
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
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvDay.setTextColor(context.getResources().getColor(R.color.white));
            holder.llDate.setBackground(context.getResources().getDrawable(R.drawable.btn_backgrond));
        } else {
            holder.tvDate.setTextColor(context.getResources().getColor(R.color.color_hint));
            holder.tvDay.setTextColor(context.getResources().getColor(R.color.color_hint));
            holder.llDate.setBackground(context.getResources().getDrawable(R.drawable.empty_otp_edit_text));
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}