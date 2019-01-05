package com.serviceonwheel.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.serviceonwheel.R;
import com.serviceonwheel.model.BookTime;

import java.util.List;

public class BookTimeAdapter extends RecyclerView.Adapter<BookTimeAdapter.ViewHolder> {

    Context context;
    private List<BookTime> listData;
    private LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;

    public BookTimeAdapter(Context context, List<BookTime> bean) {
        this.listData = bean;
        this.context = context;
        this.inflater = (LayoutInflater.from(context));
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_book_time, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final BookTime bean = listData.get(position);
        holder.tvPickupTime.setText(bean.getTime());
        holder.tvLabel.setText(bean.getTitle());
        holder.tvPickupAddress.setText(bean.getSub_title());
        holder.tvPickupDate.setText(bean.getDate());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPickupTime, tvLabel, tvPickupAddress, tvPickupDate;

        ViewHolder(View v) {
            super(v);
            tvPickupTime = v.findViewById(R.id.tvPickupTime);
            tvLabel = v.findViewById(R.id.tvLabel);
            tvPickupAddress = v.findViewById(R.id.tvPickupAddress);
            tvPickupDate = v.findViewById(R.id.tvPickupDate);
        }
    }
}