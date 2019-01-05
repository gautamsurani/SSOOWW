package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.adapter.CouponAdapter;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.CouponList;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class ApplyCouponActivity extends BaseActivity {

    Activity context;

    String serviceID = "", code = "";

    EditText etCode;

    TextView tvApply, tvNoFound;

    RecyclerView rvCoupon;

    List<CouponList> couponLists = new ArrayList<>();

    CouponAdapter couponAdapter;

    ImageView ivBack;

    String resMessage = "", resCode = "", couponID = "", msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_coupon);

        context = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            serviceID = bundle.getString("serviceID", "");
        }

        initComp();

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int l = charSequence.length();
                if (l > 0) {
                    tvApply.setVisibility(View.VISIBLE);
                } else {
                    tvApply.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        couponAdapter = new CouponAdapter(couponLists, context, "No");
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvCoupon.setLayoutManager(mLayoutManager);
        rvCoupon.setAdapter(couponAdapter);

        couponAdapter.setOnItemClickListener(new CouponAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int witch) {
                code = couponLists.get(position).getCode();
                if (global.isNetworkAvailable()) {
                    new ApplyCoupon().execute();
                } else {
                    retryInternet("apply_coupon");
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (global.isNetworkAvailable()) {
            new GetCoupon().execute();
        } else {
            retryInternet("get_coupon");
        }

        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = etCode.getText().toString();
                if (code.isEmpty()) {
                    Utils.showToast(context, "Please enter promocode!");
                } else {
                    if (global.isNetworkAvailable()) {
                        new ApplyCoupon().execute();
                    } else {
                        retryInternet("apply_coupon");
                    }
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class ApplyCoupon extends AsyncTask<String, Void, String> {
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
            String strAPI = AppConstant.API_Apply_Coupon;
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("cityID", Utils.getLocationId(context));
                    restClient.AddParam("serviceID", serviceID);
                    restClient.AddParam("couponCode", code);
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
                        couponID = jsonObjectList.getString("couponID");
                        msg = jsonObjectList.getString("msg");
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
                Intent returnIntent = new Intent();
                returnIntent.putExtra("id", couponID);
                returnIntent.putExtra("code", code);
                returnIntent.putExtra("desc", msg);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetCoupon extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_coupon_list;
            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("cityID", Utils.getLocationId(context));
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("coupon_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        CouponList couponList = new CouponList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        couponList.setCouponID(jsonObjectList.getString("couponID"));
                                        couponList.setCode(jsonObjectList.getString("code"));
                                        couponList.setMsg(jsonObjectList.getString("msg"));
                                        couponList.setExpire_msg(jsonObjectList.getString("expire_msg"));
                                        couponLists.add(couponList);
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
            if (resCode.equalsIgnoreCase("0")) {
                couponAdapter.notifyDataSetChanged();
                tvNoFound.setVisibility(View.GONE);
            } else {
                tvNoFound.setText(resMessage);
                tvNoFound.setVisibility(View.VISIBLE);
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
                if (extraValue.equalsIgnoreCase("get_coupon")) {
                    new GetCoupon().execute();
                } else if (extraValue.equalsIgnoreCase("apply_coupon")) {
                    new ApplyCoupon().execute();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void initComp() {
        etCode = findViewById(R.id.etCode);
        tvApply = findViewById(R.id.tvApply);
        rvCoupon = findViewById(R.id.rvCoupon);
        ivBack = findViewById(R.id.ivBack);
        tvNoFound = findViewById(R.id.tvNoFound);
    }
}
