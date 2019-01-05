package com.serviceonwheel.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.model.History_Model;

import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.DataObjectHolder> {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<History_Model> dataset;
    private Context context;
    private static MyClickListener myClickListener;
    LayoutInflater inflater;


    public HistoryAdapter(ArrayList<History_Model> data, Context context) {
        this.dataset = data;
        this.context = context;
    }

    static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        TextView tvbooktitle;
        TextView tvbookingstatus;
        CardView bookingbox;

        DataObjectHolder(View itemView) {
            super(itemView);

            tvbooktitle = itemView.findViewById(R.id.tvbooktitle);
            tvbookingstatus = itemView.findViewById(R.id.tvbookingstatus);
            bookingbox = itemView.findViewById(R.id.bookingbox);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_history, parent, false);

        return new DataObjectHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(DataObjectHolder holder, final int position) {

        holder.tvbookingstatus.setText(dataset.get(holder.getAdapterPosition()).getStatus());
        holder.tvbooktitle.setText(dataset.get(holder.getAdapterPosition()).getTitle());
        holder.bookingbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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