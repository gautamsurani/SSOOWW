package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class ChangePasswordActivity extends BaseActivity {

    Context context;

    TextView tvSubmit;

    EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;

    String current_password = "", new_password = "";

    RelativeLayout relativeMain;

    Toolbar toolbar;

    String userId = "", resMessage = "", resCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        context = this;

        initToolbar();

        initComp();

        userId = Utils.getUserId(context);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePassword()) {
                    return;
                }
                if (!validateConfirmPassword()) {
                    return;
                }
                if (!validateNewConfirmPassword()) {
                    return;
                }

                if (!etNewPassword.getText().toString().equals(etConfirmNewPassword.getText().toString())) {
                    Utils.ShowSnakBar("Password not match", relativeMain, ChangePasswordActivity.this);
                } else {
                    Utils.hideKeyboard(ChangePasswordActivity.this);
                    current_password = etCurrentPassword.getText().toString();
                    new_password = etNewPassword.getText().toString();
                    if (global.isNetworkAvailable()) {
                        new ChangePassword().execute();
                    } else {
                        retryInternet("change_password");
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
                if (extraValue.equalsIgnoreCase("change_password")) {
                    new ChangePassword().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ChangePassword extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Change_Password;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", userId);
                    restClient.AddParam("oldpass", current_password);
                    restClient.AddParam("newpass", new_password);
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String Register = restClient.getResponse();
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
                onBackPressed();
            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    public void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView toolbar_title = toolbar.findViewById(R.id.tvTitle);
        toolbar_title.setText(getResources().getString(R.string.toolbar_change_password));
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

    private void initComp() {
        relativeMain = findViewById(R.id.relativeMain);
        tvSubmit = findViewById(R.id.tvChnaePassword);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);

    }

    private boolean validatePassword() {

        if (etCurrentPassword.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Enter current password", relativeMain, ChangePasswordActivity.this);

            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword() {

        if (etNewPassword.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Enter new password", relativeMain, ChangePasswordActivity.this);

            return false;
        }
        return true;
    }

    private boolean validateNewConfirmPassword() {

        if (etConfirmNewPassword.getText().toString().trim().isEmpty()) {
            Utils.ShowSnakBar("Confirm new password", relativeMain, ChangePasswordActivity.this);

            return false;
        }
        return true;
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
        finish();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}
