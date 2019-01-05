package com.serviceonwheel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serviceonwheel.R;
import com.serviceonwheel.model.ServiceList;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<ServiceList> serviceLists = new ArrayList<>();
    private Context context;

    private OnClickListener onClickListener;

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, int witch);
    }

    public ServiceAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setList(List<ServiceList> serviceLists) {
        this.serviceLists = serviceLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ServiceList service = serviceLists.get(position);
        Glide.with(context)
                .load(service.getImage())
                .into(holder.img);
        holder.text.setText(service.getName());
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(position, 0);
            }
        });
    }

    public void clearData() {
        serviceLists.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return serviceLists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;
        LinearLayout llMain;

        ViewHolder(View item) {
            super(item);
            img = item.findViewById(R.id.serviceImg);
            text = item.findViewById(R.id.serviceText);
            llMain = item.findViewById(R.id.llMain);
        }
    }
}