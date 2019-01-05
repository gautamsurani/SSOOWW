package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONObject;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class ContactUsActivity extends BaseActivity {

    TextView tvTitle, tvAddress, tvWebSite, tvPhone, tvCall, tvEmail;
    Activity context;
    String UserId = "", resMessage = "", resCode = "", address = "", email = "", phone = "", website = "", call = "";
    TextView btnCallNow;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);

        context = this;

        initToolbar();

        context = this;

        UserId = Utils.getUserId(context);

        init();

        if (global.isNetworkAvailable()) {
            new GetContactData().execute();
        } else {
            retryInternet("contact");
        }

        btnCallNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.callIntent(context, call);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetContactData extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_GET_CONTACT_US;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPI);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String register = restClient.getResponse();
                Log.e("API : ", register);

                if (register != null && register.length() != 0) {
                    jsonObjectList = new JSONObject(register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONObject jsonObject = jsonObjectList.getJSONObject("contact");
                            {
                                address = jsonObject.getString("address_1");
                                email = jsonObject.getString("email");
                                phone = jsonObject.getString("phone");
                                website = jsonObject.getString("website");
                                call = jsonObject.getString("call");
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
                tvAddress.setText(address);
                tvCall.setText(call);
                tvEmail.setText(email);
                tvPhone.setText(phone);
                tvWebSite.setText(website);
            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tvTitle = context.findViewById(R.id.tvTitle);
        tvAddress = findViewById(R.id.tvAddress);
        tvWebSite = findViewById(R.id.tvWebSite);
        btnCallNow = findViewById(R.id.btnCallnow);
        tvPhone = findViewById(R.id.tvPhone);
        tvCall = findViewById(R.id.tvCall);
        tvEmail = findViewById(R.id.tvEmail);
        tvTitle.setText("Contact Us");
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
                if (extraValue.equalsIgnoreCase("contact")) {
                    new GetContactData().execute();
                }
            }
        }
    }

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
        toolbarTitle.setText(getResources().getString(R.string.toolbar_contactus));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}