package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.adapter.DateAdapter;
import com.serviceonwheel.adapter.TimeAdapter;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.DateList;
import com.serviceonwheel.model.TimeList;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class OrderSummeryActivity extends BaseActivity {

    Context context;
    RecyclerView rvDate, rvTime;
    DateAdapter dateAdapter;
    TimeAdapter timeAdapter;

    ArrayList<DateList> dateLists = new ArrayList<>();

    ArrayList<TimeList> timeLists = new ArrayList<>();

    LinearLayout llBottom, llCoupon, llAfterCouponApply, llOpenPrice, llPriceList;

    int priceListOpen = 0;

    RadioButton rbPrimeOrder, rbNormalOrder;

    TextView tvNormalMsg, tvPrimeMsg, tvServiceAmount, tvBottomServiceAmount;

    String resMessage = "", resCode = "", prime_order = "", addressID = "",
            normal_order = "", prime_order_title = "", prime_order_desc = "",
            normal_order_title = "", normal_order_desc = "", prime_order_charge = "";

    String serviceID = "", service_price = "", display_service_price = "", selected_payment_method = "";

    RadioGroup radioGroup;

    String selected_date_value = "", selected_time_value = "", display_orderID = "",
            title = "", short_desc = "", orderID = "", amount = "", userID = "";

    private int Apply_Coupon_Request_Code = 102;

    TextView tvRemove, tvCode, tvDesc;

    String coupon_id = "", coupon_code = "", coupon_desc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summery);

        context = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            serviceID = bundle.getString("serviceID", "");
            addressID = bundle.getString("addressID", "");
        }

        initToolbar();

        initComp();

        dateAdapter = new DateAdapter(dateLists, context);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false);
        rvDate.setLayoutManager(gridLayoutManager);
        rvDate.setAdapter(dateAdapter);

        dateAdapter.setOnItemClickListener(new DateAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                selected_date_value = dateLists.get(position).getValue();
            }
        });

        timeAdapter = new TimeAdapter(timeLists, context);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(context, 1, LinearLayoutManager.HORIZONTAL, false);
        rvTime.setLayoutManager(gridLayoutManager1);
        rvTime.setAdapter(timeAdapter);

        timeAdapter.setOnItemClickListener(new TimeAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                selected_time_value = timeLists.get(position).getValue();
            }
        });

        llBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (global.isNetworkAvailable()) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId == R.id.rbNormalOrder) {
                        selected_payment_method = "Normal";
                    } else if (selectedId == R.id.rbPrimeOrder) {
                        selected_payment_method = "Prime";
                    }
                    if (selected_date_value.isEmpty()) {
                        Utils.showToast(context, "Select date!");
                    } else if (selected_time_value.isEmpty()) {
                        Utils.showToast(context, "Select time!");
                    } else if (selected_payment_method.isEmpty()) {
                        Utils.showToast(context, "Select payment method!");
                    } else {
                        new Checkout().execute();
                    }
                } else {
                    retryInternet("checkout");
                }
            }
        });

        llCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ApplyCouponActivity.class);
                i.putExtra("serviceID", serviceID);
                startActivityForResult(i, Apply_Coupon_Request_Code);
            }
        });

        llOpenPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priceListOpen == 0) {
                    llPriceList.setVisibility(View.VISIBLE);
                    priceListOpen = 1;
                } else {
                    llPriceList.setVisibility(View.GONE);
                    priceListOpen = 0;
                }
            }
        });

        if (global.isNetworkAvailable()) {
            new GetCheckout().execute();
        } else {
            retryInternet("get_checkout");
        }

        tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coupon_id = "";
                coupon_code = "";
                coupon_desc = "";
                llAfterCouponApply.setVisibility(View.GONE);
                llCoupon.setVisibility(View.VISIBLE);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class Checkout extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_Checkout;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("cityID", Utils.getLocationId(context));
                    restClient.AddParam("addressID", addressID);
                    restClient.AddParam("serviceID", serviceID);
                    restClient.AddParam("service_price", service_price);
                    restClient.AddParam("display_service_price", display_service_price);
                    restClient.AddParam("service_date", selected_date_value);
                    restClient.AddParam("service_time", selected_time_value);
                    restClient.AddParam("order_book_type", selected_payment_method);
                    restClient.AddParam("discount_coupon_code", coupon_code);
                    restClient.AddParam("discount_coupon_val", coupon_id);
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
                        display_orderID = jsonObjectList.getString("display_orderID");
                        title = jsonObjectList.getString("title");
                        short_desc = jsonObjectList.getString("short_desc");
                        if (resCode.equalsIgnoreCase("9")) {
                            orderID = jsonObjectList.getString("orderID");
                            amount = jsonObjectList.getString("amount");
                            userID = jsonObjectList.getString("userID");
                            short_desc = jsonObjectList.getString("short_desc");
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
                Utils.showToast(context, resMessage);
                Intent intent = new Intent(context, SuccessActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("short_desc", short_desc);
                intent.putExtra("display_orderID", display_orderID);
                intent.putExtra("booking_status", "0");
                startActivity(intent);
                finish();
            } else if (resCode.equalsIgnoreCase("9")) {
                String url_book = "https://www.serviceonwheel.com/mapp/index.php?" +
                        "view=pay_prime_order&app_type=Android&userID=";

                url_book = url_book + userID +
                        "&orderID=" + orderID +
                        "&amount=" + amount;

                Intent intent = new Intent(context, PaymentWebViewActivity.class);
                intent.putExtra("url", url_book);
                startActivityForResult(intent, 1001);

            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getStringExtra("status").equals("success")) {
                    Intent intent = new Intent(context, SuccessActivity.class);
                    intent.putExtra("title", data.getStringExtra("title"));
                    intent.putExtra("short_desc", data.getStringExtra("short_desc"));
                    intent.putExtra("display_orderID", display_orderID);
                    intent.putExtra("booking_status", "0");
                    startActivity(intent);
                    finish();
                } else if (data.getStringExtra("status").equals("failed")) {
                    Intent intent = new Intent(context, SuccessActivity.class);
                    intent.putExtra("title", data.getStringExtra("title"));
                    intent.putExtra("short_desc", data.getStringExtra("short_desc"));
                    intent.putExtra("display_orderID", display_orderID);
                    intent.putExtra("booking_status", "1");
                    startActivity(intent);
                    finish();
                }
            }
        } else if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("get_checkout")) {
                    new GetCheckout().execute();
                } else if (extraValue.equalsIgnoreCase("checkout")) {
                    new Checkout().execute();
                }
            }
        } else if (requestCode == Apply_Coupon_Request_Code) {
            if (resultCode == Activity.RESULT_OK) {
                coupon_id = data.getStringExtra("id");
                coupon_code = data.getStringExtra("code");
                coupon_desc = data.getStringExtra("desc");
                llCoupon.setVisibility(View.GONE);
                llAfterCouponApply.setVisibility(View.VISIBLE);
                tvCode.setText(coupon_code);
                tvDesc.setText(coupon_desc);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetCheckout extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Checkout_Info;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("cityID", Utils.getLocationId(context));
                    restClient.AddParam("serviceID", serviceID);
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
                        prime_order = jsonObjectList.getString("prime_order");
                        normal_order = jsonObjectList.getString("normal_order");
                        prime_order_title = jsonObjectList.getString("prime_order_title");
                        prime_order_desc = jsonObjectList.getString("prime_order_desc");
                        normal_order_title = jsonObjectList.getString("normal_order_title");
                        normal_order_desc = jsonObjectList.getString("normal_order_desc");
                        prime_order_charge = jsonObjectList.getString("prime_order_charge");
                        service_price = jsonObjectList.getString("service_price");
                        display_service_price = jsonObjectList.getString("display_service_price");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("time_data");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        TimeList timeList = new TimeList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        timeList.setDisplay_name(jsonObjectList.getString("display_name"));
                                        timeList.setValue(jsonObjectList.getString("value"));
                                        timeLists.add(timeList);
                                    }
                                }
                            }
                            JSONArray jsonArray1 = jsonObjectList.getJSONArray("prefer_date_data");
                            {
                                if (jsonArray1 != null && jsonArray1.length() != 0) {
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        DateList dateList = new DateList();
                                        JSONObject jsonObjectList = jsonArray1.getJSONObject(i);
                                        dateList.setDisplay_date(jsonObjectList.getString("display_date"));
                                        dateList.setValue(jsonObjectList.getString("value"));
                                        dateLists.add(dateList);
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
                dateAdapter.notifyDataSetChanged();
                timeAdapter.notifyDataSetChanged();

                if (prime_order.equalsIgnoreCase("no")) {
                    rbPrimeOrder.setVisibility(View.GONE);
                    tvPrimeMsg.setVisibility(View.GONE);
                } else {
                    rbPrimeOrder.setVisibility(View.VISIBLE);
                    tvPrimeMsg.setVisibility(View.VISIBLE);
                }

                if (normal_order.equalsIgnoreCase("no")) {
                    rbNormalOrder.setVisibility(View.GONE);
                    tvNormalMsg.setVisibility(View.GONE);
                } else {
                    rbNormalOrder.setVisibility(View.VISIBLE);
                    tvNormalMsg.setVisibility(View.VISIBLE);
                }

                rbNormalOrder.setText(normal_order_title);
                tvNormalMsg.setText(normal_order_desc);
                rbPrimeOrder.setText(prime_order_title);
                tvPrimeMsg.setText(prime_order_desc);

                try {
                    Double aDouble = Double.parseDouble(display_service_price);
                    tvServiceAmount.setText(getResources().getString(R.string.rs) + " " + display_service_price);
                    tvBottomServiceAmount.setText(getResources().getString(R.string.rs) + " " + display_service_price);
                } catch (Exception e) {
                    tvServiceAmount.setText(display_service_price);
                    tvBottomServiceAmount.setText(display_service_price);
                }

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

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tvTitle);
        ImageView profile = toolbar.findViewById(R.id.imgProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MyAccountActivity.class);
                startActivity(i);
            }
        });
        profile.setVisibility(View.GONE);
        toolbarTitle.setText("Booking Summery");
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComp() {
        rvDate = findViewById(R.id.rvDate);
        rvTime = findViewById(R.id.rvTime);
        llBottom = findViewById(R.id.llBottom);
        llCoupon = findViewById(R.id.llCoupon);
        llAfterCouponApply = findViewById(R.id.llAfterCouponApply);
        llPriceList = findViewById(R.id.llPricelist);
        llOpenPrice = findViewById(R.id.llOpenprice);
        rbPrimeOrder = findViewById(R.id.rbPrimeOrder);
        rbNormalOrder = findViewById(R.id.rbNormalOrder);
        tvNormalMsg = findViewById(R.id.tvNormalMsg);
        tvPrimeMsg = findViewById(R.id.tvPrimeMsg);
        tvServiceAmount = findViewById(R.id.tvServiceAmount);
        tvBottomServiceAmount = findViewById(R.id.tvBottomServiceAmount);
        radioGroup = findViewById(R.id.radioGroup);
        tvRemove = findViewById(R.id.tvRemove);
        tvCode = findViewById(R.id.tvCode);
        tvDesc = findViewById(R.id.tvDesc);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}