package com.serviceonwheel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.model.CityListModel;

import java.util.ArrayList;


public class CitySelectAdapter extends RecyclerView.Adapter<CitySelectAdapter.ViewHolder> {

    Context context;
    ArrayList<CityListModel> listData;
    LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;

    public CitySelectAdapter(Context context, ArrayList<CityListModel> bean) {

        this.listData = bean;
        this.context = context;
        this.inflater = (LayoutInflater.from(context));

    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_city, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CityListModel bean = listData.get(position);
        holder.productSearchTitle.setText(bean.getName());
        holder.productItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, view, 1);
                }
            }
        });
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productSearchTitle;
        LinearLayout productItems;

        public ViewHolder(View v) {
            super(v);

            productSearchTitle = v.findViewById(R.id.productSearchtitle);
            productItems = v.findViewById(R.id.productitems);
        }
    }


}







