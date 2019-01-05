package com.serviceonwheel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.model.AddressList;
import com.serviceonwheel.model.OrderList;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private List<OrderList> addressLists;
    private Context context;

    private OnClickListener onClickListener;

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvOrderId, tvDate, tvAmount, tvStatus;
        LinearLayout llMain;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            llMain = itemView.findViewById(R.id.llMain);
        }
    }

    public OrderAdapter(List<OrderList> data, Context context) {
        addressLists = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int listPosition) {
        holder.tvName.setText(addressLists.get(listPosition).getName());
        holder.tvOrderId.setText("BOOKING ID : " + addressLists.get(listPosition).getOrderID());
        holder.tvDate.setText(addressLists.get(listPosition).getDate());
        holder.tvAmount.setText(addressLists.get(listPosition).getAmount());
        holder.tvStatus.setText(addressLists.get(listPosition).getStatus());

        if (addressLists.get(listPosition).getStatus().equalsIgnoreCase("progress")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.themeColorBlue));
        } else if (addressLists.get(listPosition).getStatus().equalsIgnoreCase("CONFIRM")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.themeColorGreen));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
        }

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClick(listPosition);
            }
        });

    }

    @Override
    public int getItemCount() {
        return addressLists.size();
    }
}