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
import android.widget.RadioButton;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.model.AddressList;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private List<AddressList> addressLists;
    private Context context;
    private String radio;

    private OnClickListener onClickListener;

    private int selectedPosition = 0;

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position, int witch);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress;
        LinearLayout llMain;
        ImageView ivMore;
        RadioButton radio;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivMore = itemView.findViewById(R.id.ivMore);
            llMain = itemView.findViewById(R.id.llMain);
            radio = itemView.findViewById(R.id.radio);
        }
    }

    public AddressAdapter(List<AddressList> data, Context context, String btn) {
        addressLists = data;
        this.context = context;
        radio = btn;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_address, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int listPosition) {
        holder.tvName.setText(addressLists.get(listPosition).getName() + " | " +
                addressLists.get(listPosition).getPhone());

        holder.tvAddress.setText(addressLists.get(listPosition).getAddress1()
                + ", " + addressLists.get(listPosition).getAddress2()
                + ", " + addressLists.get(listPosition).getCity()
                + ", " + addressLists.get(listPosition).getPincode());

        if (listPosition == selectedPosition) {
            holder.radio.setChecked(true);
            onClickListener.onClick(listPosition, 3);
        } else {
            holder.radio.setChecked(false);
        }

        if (radio.equals("No")) {
            holder.radio.setVisibility(View.GONE);
        } else {
            holder.radio.setVisibility(View.VISIBLE);
        }

        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.ivMore);
                //inflating menu from xml resource
                popup.inflate(R.menu.address_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                onClickListener.onClick(listPosition, 1);
                                return true;
                            case R.id.delete:
                                onClickListener.onClick(listPosition, 2);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = listPosition;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressLists.size();
    }
}