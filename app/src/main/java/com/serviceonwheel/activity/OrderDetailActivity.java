package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.serviceonwheel.R;
import com.serviceonwheel.adapter.BookTimeAdapter;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.BookTime;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.Global;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class OrderDetailActivity extends BaseActivity {

    RecyclerView rvTimes;

    Activity context;

    LinearLayout llMain;

    LinearLayoutManager mLayoutManager;

    List<BookTime> bookTimes = new ArrayList<>();

    BookTimeAdapter bookTimeAdapter;

    String userID = "", app_type = "Android", cityID = "";

    String resMessage = "", resCode = "";

    Global global;

    TextView tvServiceName, tvDesc, tvAmount, tvVendorName, tvVendorNumber, txtService, txtAdditional, txtTotalCharge,
            txtAdvance, txtDiscount, txtPayable, tvTransId, tvTransTime, tvNumber, tvName, tvAddress, tvAddress2, tvTime;

    ImageView ivVendorImage;

    CardView cvPayment, cvVendor;

    String orderID = "", display_orderID = "", order_date = "", service_name = "", service_desc = "", current_status = "",
            service_amount = "", additional_product_charge = "", total_charge = "", advance_payment = "", discount = "",
            final_pay_amount = "", payment_status = "", vendor_name = "", vendor_phone = "", vendor_image = "",
            transction_id = "", transction_date = "", date_of_appointment = "";

    String line_1 = "", line_2 = "", line_3 = "";

    LinearLayout llSupport, llMail, llHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        context = this;

        global = new Global(context);

        initComp();

        initToolbar();

        userID = Utils.getUserId(context);
        cityID = Utils.getLocationId(context);

        orderID = getIntent().getExtras().getString("orderID");

        rvTimes.setFocusable(false);

        llMain.requestFocus();

        mLayoutManager = new LinearLayoutManager(context);
        rvTimes.setLayoutManager(mLayoutManager);
        rvTimes.setHasFixedSize(true);
        bookTimeAdapter = new BookTimeAdapter(context, bookTimes);
        rvTimes.setAdapter(bookTimeAdapter);

        if (global.isNetworkAvailable()) {
            new GetOrderDetail().execute();
        } else {
            retryInternet("order_detail");
        }

        llHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent codeOpen = new Intent(context, HelpActivity.class);
                startActivity(codeOpen);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.callIntent(context, Utils.getSupportPhone(context));
            }
        });

        llMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!current_status.equalsIgnoreCase("COMPLETED")) {
                    Utils.showToast(context, "Your service is not complete yet");
                } else {
                    if (global.isNetworkAvailable()) {
                        new MailInvoice().execute();
                    } else {
                        retryInternet("mail_invoice");
                    }
                }
            }
        });
    }

    private void initComp() {
        rvTimes = findViewById(R.id.rvTimes);
        llMain = findViewById(R.id.llMain);
        tvServiceName = findViewById(R.id.tvservicename);
        tvDesc = findViewById(R.id.tvdec);
        tvAmount = findViewById(R.id.tvAmount);
        tvVendorName = findViewById(R.id.tvvendorname);
        tvVendorNumber = findViewById(R.id.tvvendornumber);
        txtService = findViewById(R.id.txtservice);
        txtAdditional = findViewById(R.id.txtadditional);
        txtTotalCharge = findViewById(R.id.txttotalcharge);
        txtAdvance = findViewById(R.id.txtadvance);
        txtDiscount = findViewById(R.id.txtdiscount);
        txtPayable = findViewById(R.id.txtpayable);
        tvTransId = findViewById(R.id.tvtransid);
        tvTransTime = findViewById(R.id.tvtranstime);
        tvNumber = findViewById(R.id.tvnumber);
        tvName = findViewById(R.id.tvname);
        tvAddress = findViewById(R.id.tvAddress);
        cvPayment = findViewById(R.id.cvpayment);
        ivVendorImage = findViewById(R.id.ivvendorimage);
        cvVendor = findViewById(R.id.cvVendor);
        tvAddress2 = findViewById(R.id.tvAddress2);
        tvTime = findViewById(R.id.tvTime);
        llHelp = findViewById(R.id.llHelp);
        llMail = findViewById(R.id.llMail);
        llSupport = findViewById(R.id.llSupport);
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tvTitle);
        toolbarTitle.setText("Booking Detail");
        ImageView profile = toolbar.findViewById(R.id.imgProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MyAccountActivity.class);
                startActivity(i);
            }
        });
        profile.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private class MailInvoice extends AsyncTask<String, Void, String> {
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
            String strAPI = AppConstant.API_Mail_Invoice;
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("orderID", orderID);
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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            Utils.showToast(context, resMessage);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetOrderDetail extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_order_Detail;
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("cityID", Utils.getLocationId(context));
                    restClient.AddParam("orderID", orderID);
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("order_detail_data");
                            {
                                JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                orderID = jsonObjectList.getString("orderID");
                                display_orderID = jsonObjectList.getString("display_orderID");
                                order_date = jsonObjectList.getString("order_date");
                                service_name = jsonObjectList.getString("service_name");
                                service_desc = jsonObjectList.getString("service_desc");
                                current_status = jsonObjectList.getString("current_status");
                                service_amount = jsonObjectList.getString("service_amount");
                                additional_product_charge = jsonObjectList.getString("additional_product_charge");
                                total_charge = jsonObjectList.getString("total_charge");
                                advance_payment = jsonObjectList.getString("advance_payment");
                                discount = jsonObjectList.getString("discount");
                                final_pay_amount = jsonObjectList.getString("final_pay_amount");
                                payment_status = jsonObjectList.getString("payment_status");
                                vendor_name = jsonObjectList.getString("vendor_name");
                                vendor_phone = jsonObjectList.getString("vendor_phone");
                                vendor_image = jsonObjectList.getString("vendor_image");
                                transction_id = jsonObjectList.getString("transction_id");
                                transction_date = jsonObjectList.getString("transction_date");
                                date_of_appointment = jsonObjectList.getString("date_of_appointment");

                                JSONArray jsonArray1 = jsonObjectList.getJSONArray("history_data");
                                {
                                    if (jsonArray1 != null && jsonArray1.length() != 0) {
                                        for (int i = 0; i < jsonArray1.length(); i++) {
                                            BookTime bookTime = new BookTime();
                                            JSONObject jsonObjectList1 = jsonArray1.getJSONObject(i);
                                            bookTime.setTitle(jsonObjectList1.getString("title"));
                                            bookTime.setTime(jsonObjectList1.getString("time"));
                                            bookTime.setSub_title(jsonObjectList1.getString("short_desc"));
                                            bookTime.setDate(jsonObjectList1.getString("date"));
                                            bookTimes.add(bookTime);
                                        }
                                    }
                                }

                                JSONArray jsonArray2 = jsonObjectList.getJSONArray("address_data");
                                {
                                    if (jsonArray2 != null && jsonArray2.length() != 0) {
                                        for (int i = 0; i < jsonArray2.length(); i++) {
                                            JSONObject jsonObjectList2 = jsonArray2.getJSONObject(0);
                                            line_1 = jsonObjectList2.getString("line_1");
                                            line_2 = jsonObjectList2.getString("line_2");
                                            line_3 = jsonObjectList2.getString("line_3");
                                        }
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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                tvServiceName.setText(service_name);
                tvDesc.setText(service_desc);
                tvAmount.setText(getResources().getString(R.string.rs) + " " + final_pay_amount);
                tvVendorName.setText(vendor_name);
                tvVendorNumber.setText(vendor_phone);
                txtService.setText(getResources().getString(R.string.rs) + " " + service_amount);
                txtAdditional.setText(getResources().getString(R.string.rs) + " " + additional_product_charge);
                txtTotalCharge.setText(getResources().getString(R.string.rs) + " " + total_charge);
                txtAdvance.setText(getResources().getString(R.string.rs) + " " + advance_payment);
                txtDiscount.setText(getResources().getString(R.string.rs) + " " + discount);
                txtPayable.setText(getResources().getString(R.string.rs) + " " + final_pay_amount);
                tvTransId.setText(transction_id);
                tvTransTime.setText(transction_date);

                String time = "<b>Time</b> : " + date_of_appointment;
                tvTime.setText(Html.fromHtml(time));

                tvName.setText(line_1);
                tvAddress.setText(line_2);
                tvAddress2.setText(line_3);

                Glide.with(context)
                        .load(vendor_image)
                        .apply(new RequestOptions()
                                .fitCenter())
                        .into(ivVendorImage);

                if (payment_status.equals("Yes")) {
                    cvPayment.setVisibility(View.VISIBLE);
                } else {
                    cvPayment.setVisibility(View.GONE);
                }

                if (vendor_name.isEmpty()) {
                    cvVendor.setVisibility(View.GONE);
                } else {
                    cvVendor.setVisibility(View.VISIBLE);
                }

                bookTimeAdapter.notifyDataSetChanged();
            } else {
                Utils.showToast(context, resMessage);
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
                if (extraValue.equalsIgnoreCase("order_detail")) {
                    new GetOrderDetail().execute();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}