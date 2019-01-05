package com.serviceonwheel.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serviceonwheel.R;
import com.serviceonwheel.model.JobList;

import java.util.ArrayList;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<JobList> jobLists = new ArrayList<>();
    private Context context;

    private OnClickListener onClickListener;

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, int witch);
    }

    public JobAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setList(List<JobList> jobLists) {
        this.jobLists = jobLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_job, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        JobList service = jobLists.get(position);
        Glide.with(context)
                .load(service.getImage())
                .into(holder.img);
        holder.text.setText(service.getTitle());
        holder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(position, 0);
            }
        });
    }

    public void clearData() {
        jobLists.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return jobLists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;
        RelativeLayout rlMain;

        ViewHolder(View item) {
            super(item);
            img = item.findViewById(R.id.serviceImg);
            text = item.findViewById(R.id.serviceText);
            rlMain = item.findViewById(R.id.rlMain);
        }
    }
}