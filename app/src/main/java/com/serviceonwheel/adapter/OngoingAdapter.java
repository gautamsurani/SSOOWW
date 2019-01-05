package com.serviceonwheel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.serviceonwheel.R;
import com.serviceonwheel.model.Ongoing_Model;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

public class OngoingAdapter extends RecyclerView.Adapter<OngoingAdapter.DataObjectHolder> {

    private ArrayList<Ongoing_Model> data_set;
    private Context context;
    private SimpleArcDialog mDialog;
    private String resMessage = "", resCode = "", new_status = "";

    public void dismissProgressDialog() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void startProgressDialog() {
        mDialog.show();
    }

    public OngoingAdapter(ArrayList<Ongoing_Model> data, Context context) {
        this.data_set = data;
        this.context = context;
        mDialog = new SimpleArcDialog(context);
        mDialog.setConfiguration(new ArcConfiguration(context));
        mDialog.setCancelable(false);
    }

    static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvBookTitle, tvFeedback, tvMonth, tvDate, tvDesc, tvAmount, tvStatus, tvBookingId;
        TextView tvBookingDetail, tvTime;
        TextView tvViewBook, tvCancel;
        CardView cvMain;

        DataObjectHolder(View itemView) {
            super(itemView);
            tvViewBook = itemView.findViewById(R.id.tvviewbooking);
            tvBookTitle = itemView.findViewById(R.id.tvbooktitle);
            tvBookingDetail = itemView.findViewById(R.id.tvbookingdetail);
            cvMain = itemView.findViewById(R.id.cvMain);
            tvFeedback = itemView.findViewById(R.id.tvFeedback);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvCancel = itemView.findViewById(R.id.tvCancel);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @NonNull
    @Override
    public DataObjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ongoing, parent, false);

        return new DataObjectHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DataObjectHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tvBookTitle.setText(data_set.get(holder.getAdapterPosition()).getService_name());

        holder.tvBookingDetail.setText(data_set.get(holder.getAdapterPosition()).getService_desc());

        holder.tvMonth.setText(data_set.get(holder.getAdapterPosition()).getOrder_month());
        holder.tvDate.setText(data_set.get(holder.getAdapterPosition()).getOrder_date());
        holder.tvDesc.setText(data_set.get(holder.getAdapterPosition()).getRemark());
        holder.tvAmount.setText(context.getResources().getString(R.string.rs) + data_set.get(holder.getAdapterPosition()).getAmount());

        String status = data_set.get(holder.getAdapterPosition()).getCurrent_status();

        holder.tvStatus.setText(status.replaceAll(" ","\n"));

        if (status.equalsIgnoreCase("PENDING")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (status.equalsIgnoreCase("PROCESSING")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.themeColorBlue));
        } else if (status.equalsIgnoreCase("COMPLETED")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.themeColorGreen));
        } else if (status.equalsIgnoreCase("PAYMENT PENDING")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.dark_orange));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
        }

        holder.tvBookingId.setText(data_set.get(holder.getAdapterPosition()).getDisplay_orderID());

        String time = "<b>Time</b> : " + data_set.get(holder.getAdapterPosition()).getDate_of_appointment();
        holder.tvTime.setText(Html.fromHtml(time));

        holder.cvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onItemClick(position, v);
            }
        });

        if (data_set.get(position).getCurrent_status().equalsIgnoreCase("PENDING")) {
            holder.tvFeedback.setVisibility(View.GONE);
            holder.tvCancel.setVisibility(View.VISIBLE);
        } else if (data_set.get(position).getCurrent_status().equalsIgnoreCase("COMPLETED")) {
            holder.tvFeedback.setVisibility(View.VISIBLE);
            holder.tvCancel.setVisibility(View.GONE);
        } else {
            holder.tvFeedback.setVisibility(View.GONE);
            holder.tvCancel.setVisibility(View.GONE);
        }

        holder.tvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position;
                openFeedbackDialog();
            }
        });

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position;
                openCancelDialog();
            }
        });
    }

    private String feedback_msg = "", cancel_msg = "";

    private int selectedPosition = -1;

    private void openFeedbackDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.create();
        AlertDialog alertDialog;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.popup_feedback, null);

        TextView tvSubmit;
        ImageView ivClose;
        final EditText etMsg;

        tvSubmit = alertLayout.findViewById(R.id.tvSubmit);
        ivClose = alertLayout.findViewById(R.id.ivClose);
        etMsg = alertLayout.findViewById(R.id.etMsg);

        alertDialogBuilder.setView(alertLayout);

        alertDialog = alertDialogBuilder.create();

        final AlertDialog finalAlertDialog = alertDialog;
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback_msg = etMsg.getText().toString();
                if (feedback_msg.isEmpty()) {
                    Utils.showToast(context, "Please write something...");
                } else {
                    finalAlertDialog.dismiss();
                    new SubmitFeedback().execute();
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalAlertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void openCancelDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.create();
        AlertDialog alertDialog;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.popup_cancel, null);

        TextView tvSubmit;
        ImageView ivClose;
        final EditText etMsg;

        tvSubmit = alertLayout.findViewById(R.id.tvSubmit);
        ivClose = alertLayout.findViewById(R.id.ivClose);
        etMsg = alertLayout.findViewById(R.id.etMsg);

        alertDialogBuilder.setView(alertLayout);

        alertDialog = alertDialogBuilder.create();

        final AlertDialog finalAlertDialog = alertDialog;
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_msg = etMsg.getText().toString();
                if (cancel_msg.isEmpty()) {
                    Utils.showToast(context, "Please write something...");
                } else {
                    finalAlertDialog.dismiss();
                    new SubmitCancel().execute();
                }
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalAlertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class SubmitFeedback extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resCode = "";
            resMessage = "";
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Feedback;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("order_feedback", feedback_msg);
                    restClient.AddParam("orderID", data_set.get(selectedPosition).getOrderID());
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("Register", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                Utils.showToast(context, resMessage);
            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SubmitCancel extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resCode = "";
            resMessage = "";
            new_status = "";
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Order_Cancel;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("order_cancel_reason", cancel_msg);
                    restClient.AddParam("orderID", data_set.get(selectedPosition).getOrderID());
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("Register", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        new_status = jsonObjectList.getString("new_status");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                Utils.showToast(context, resMessage);
                data_set.get(selectedPosition).setCurrent_status(new_status);
                notifyDataSetChanged();
            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data_set.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);

    }

    private MyClickListener myClickListener;

    public void setOnClickListener(MyClickListener onClickListener) {
        myClickListener = onClickListener;
    }
}