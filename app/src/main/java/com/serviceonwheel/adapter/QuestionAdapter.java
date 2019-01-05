package com.serviceonwheel.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.model.LevelOneService;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<LevelOneService> levelOneServices;

    private Context context;

    private int selectedPosition = -1;

    private OnClickListener onClickListener;

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, int witch);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice;

        LinearLayout llMain;

        RadioButton rbSelect;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            llMain = v.findViewById(R.id.llMain);
            rbSelect = v.findViewById(R.id.rbSelect);
        }
    }

    public QuestionAdapter(Activity activity, List<LevelOneService> levelOneServices) {
        context = activity;
        this.levelOneServices = levelOneServices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_question, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.setIsRecyclable(false);

        final LevelOneService packageList = levelOneServices.get(position);

        if (position == selectedPosition) {
            holder.rbSelect.setChecked(true);
            holder.llMain.setBackgroundColor(Color.parseColor("#23ffffff"));
        } else {
            holder.rbSelect.setChecked(false);
            holder.llMain.setBackgroundColor(Color.parseColor("#00ffffff"));
        }

        try {
            Double aDouble = Double.parseDouble(packageList.getDisplay_service_price());
            if (packageList.getDisplay_service_price().isEmpty()) {
                holder.tvName.setText(packageList.getName());
            } else {
                holder.tvName.setText(packageList.getName() +
                        " (" + context.getResources().getString(R.string.rs) + " " + packageList.getDisplay_service_price() + ")"
                );
            }
        } catch (Exception e) {
            if (packageList.getDisplay_service_price().isEmpty()) {
                holder.tvName.setText(packageList.getName());
            } else {
                holder.tvName.setText(packageList.getName() +
                        " (" + packageList.getDisplay_service_price() + ")");
            }
        }

        if (packageList.getDescription().isEmpty()) {
            holder.tvPrice.setVisibility(View.GONE);
        } else {
            holder.tvPrice.setVisibility(View.VISIBLE);
            holder.tvPrice.setText(packageList.getDescription());
        }

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position;
                notifyDataSetChanged();
                onClickListener.onClick(position, 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return levelOneServices.size();
    }
}