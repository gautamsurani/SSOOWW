package com.serviceonwheel.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.activity.OrderDetailActivity;
import com.serviceonwheel.adapter.OngoingAdapter;
import com.serviceonwheel.base.BaseFragment;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.Ongoing_Model;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.Global;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class BookingOngoingFragment extends BaseFragment {

    Activity context;
    View view;
    RecyclerView rvOngoing;
    private OngoingAdapter ongoingAdapter;
    ArrayList<Ongoing_Model> ongoing_models = new ArrayList<>();
    Global global;

    String resMessage = "", resCode = "";

    int this_visible_item_count = 0;

    int visibleItemCount, totalItemCount, pastVisibleItems;

    boolean IsLAstLoading = true;

    int page = 0;

    ProgressBar pbProgress;

    TextView tvNoFound;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_ongoing, container, false);

        context = getActivity();

        global = new Global(context);

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetBookingList extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (page == 0) {
                startProgressDialog();
            } else {
                pbProgress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_order_list;
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("cityID", Utils.getLocationId(context));
                    restClient.AddParam("type", "Pending");
                    restClient.AddParam("pagecode", String.valueOf(page));
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
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("order_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Ongoing_Model ongoing_model = new Ongoing_Model();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        ongoing_model.setOrderID(jsonObjectList.getString("orderID"));
                                        ongoing_model.setDisplay_orderID(jsonObjectList.getString("display_orderID"));
                                        ongoing_model.setOrder_date(jsonObjectList.getString("order_date"));
                                        ongoing_model.setService_desc(jsonObjectList.getString("remark"));
                                        ongoing_model.setCurrent_status(jsonObjectList.getString("current_status"));
                                        ongoing_model.setService_name(jsonObjectList.getString("service_name"));
                                        ongoing_model.setRemark(jsonObjectList.getString("service_desc"));
                                        ongoing_model.setAmount(jsonObjectList.getString("amount"));
                                        ongoing_model.setPayment_status(jsonObjectList.getString("payment_status"));
                                        ongoing_model.setOrder_month(jsonObjectList.getString("order_month"));
                                        ongoing_model.setDate_of_appointment(jsonObjectList.getString("date_of_appointment"));
                                        ongoing_models.add(ongoing_model);
                                    }
                                }
                            }
                        }
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
            pbProgress.setVisibility(View.GONE);
            IsLAstLoading = true;
            if (resCode.equalsIgnoreCase("0")) {
                ongoingAdapter.notifyDataSetChanged();
            } else {
                IsLAstLoading = false;
                if (page == 0) {
                    tvNoFound.setVisibility(View.VISIBLE);
                    tvNoFound.setText(resMessage);
                }
            }
        }
    }

    public void retryInternet(String extraValue) {
        Intent i = new Intent(context, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("booking_list")) {
                    new GetBookingList().execute();
                }
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvOngoing = view.findViewById(R.id.rvongoing);
        pbProgress = view.findViewById(R.id.pbProgress);
        tvNoFound = view.findViewById(R.id.tvNoFound);

        ongoingAdapter = new OngoingAdapter(ongoing_models, context);

        ongoingAdapter.setOnClickListener(new OngoingAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderID", ongoing_models.get(position).getOrderID());
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvOngoing.setLayoutManager(mLayoutManager);
        rvOngoing.setAdapter(ongoingAdapter);

        rvOngoing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    this_visible_item_count = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (IsLAstLoading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount &&
                                recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                            IsLAstLoading = false;
                            page++;
                            new GetBookingList().execute();
                        }
                    }
                }
            }
        });

        if (global.isNetworkAvailable()) {
            new GetBookingList().execute();
        } else {
            retryInternet("get_city");
        }
    }
}