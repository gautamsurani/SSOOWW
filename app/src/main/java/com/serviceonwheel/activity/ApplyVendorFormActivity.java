package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serviceonwheel.R;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.JobList;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONObject;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class ApplyVendorFormActivity extends BaseActivity {

    Activity context;

    JobList jobList;

    public static final String EXTRA_OBJECT = "com.app.sample.chatting";

    CollapsingToolbarLayout collapsingToolbarLayout;

    TextView tvTitle, tvDate, tvDesc;

    ImageView ivImage;

    EditText etName, etPhone, etEmail, etAddress, etDesc;

    Button btnSubmit;

    String name, phone, email, address, desc, resMessage = "", resCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_vendor_form);

        context = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jobList = (JobList) getIntent().getSerializableExtra("job");
        }

        initComp();

        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_OBJECT);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar1));
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        tvTitle.setText(jobList.getTitle());
        tvDate.setText(jobList.getDate());
        tvDesc.setText(jobList.getDescription());

        Glide.with(context)
                .load(jobList.getImage())
                .into(ivImage);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                phone = etPhone.getText().toString();
                email = etEmail.getText().toString();
                email = email.replaceAll(" ", "");
                address = etAddress.getText().toString();
                desc = etDesc.getText().toString();
                if (name.isEmpty()) {
                    Utils.showToast(context, "Enter full name");
                } else if (phone.isEmpty()) {
                    Utils.showToast(context, "Enter phone number");
                } else if (phone.length() < 10) {
                    Utils.showToast(context, "Enter valid phone number");
                } else if (email.isEmpty()) {
                    Utils.showToast(context, "Enter email");
                } else if (!Utils.isValidEmail(email)) {
                    Utils.showToast(context, "Enter valid email");
                } else if (address.isEmpty()) {
                    Utils.showToast(context, "Enter address");
                } else if (desc.isEmpty()) {
                    Utils.showToast(context, "Enter descriptions");
                } else {
                    if (global.isNetworkAvailable()) {
                        new ApplyForVendor().execute();
                    } else {
                        retryInternet("apply_for_vendor");
                    }
                }
            }
        });
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
                if (extraValue.equalsIgnoreCase("apply_for_vendor")) {
                    new ApplyForVendor().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ApplyForVendor extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Apply_For_Vendor;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("job_id", jobList.getJobID());
                    restClient.AddParam("apply_job_title", jobList.getTitle());
                    restClient.AddParam("email", email);
                    restClient.AddParam("name", name);
                    restClient.AddParam("phone", phone);
                    restClient.AddParam("address", address);
                    restClient.AddParam("message", desc);
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
            Utils.showToast(context, resMessage);
            if (resCode.equalsIgnoreCase("0")) {
                onBackPressed();
            }
        }
    }

    private void initComp() {
        tvDate = findViewById(R.id.etaddedOn);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.etOfferDescription);
        ivImage = findViewById(R.id.imgdetailoffer);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etDesc = findViewById(R.id.etDesc);
        btnSubmit = findViewById(R.id.btnSubmit);
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
