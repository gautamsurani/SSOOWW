package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.serviceonwheel.R;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.broadcastReceiver.MySMSBroadcastReceiver;
import com.serviceonwheel.preference.AppPersistence;
import com.serviceonwheel.preference.AppPreference;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class OTPActivity extends BaseActivity {

    EditText et1, et2, et3, et4;

    TextView tvVerify, tvNumber, tvResend;

    Activity context;

    String otp = "", phone = "", userID = "", resMessage = "", resCode = "",
            name = "", email = "", screen = "", image = "";

    String user_otp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        context = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            otp = bundle.getString("otp", "");
            phone = bundle.getString("phone", "");
            userID = bundle.getString("userID", "");
        }

        initComp();

        tvNumber.setText(phone);

        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() < 1) {
                    et1.setBackground(getResources().getDrawable(R.drawable.empty_otp_edit_text));
                } else {
                    et1.setBackground(getResources().getDrawable(R.drawable.un_empty_otp_edit_text));
                    et1.clearFocus();
                    et2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() < 1) {
                    et2.setBackground(getResources().getDrawable(R.drawable.empty_otp_edit_text));
                } else {
                    et2.setBackground(getResources().getDrawable(R.drawable.un_empty_otp_edit_text));
                    et2.clearFocus();
                    et3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() < 1) {
                    et3.setBackground(getResources().getDrawable(R.drawable.empty_otp_edit_text));
                } else {
                    et3.setBackground(getResources().getDrawable(R.drawable.un_empty_otp_edit_text));
                    et3.clearFocus();
                    et4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() < 1) {
                    et4.setBackground(getResources().getDrawable(R.drawable.empty_otp_edit_text));
                } else {
                    et4.setBackground(getResources().getDrawable(R.drawable.un_empty_otp_edit_text));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = et1.getText().toString();
                String b = et2.getText().toString();
                String c = et3.getText().toString();
                String d = et4.getText().toString();
                user_otp = a + b + c + d;
                if (user_otp.isEmpty()) {
                    Utils.showToast(context, "Please enter OTP!");
                } else if (user_otp.length() < 4) {
                    Utils.showToast(context, "Please enter valid OTP!");
                } else if (!otp.equals(user_otp)) {
                    Utils.showToast(context, "Wrong OTP, Please re-enter!");
                } else {
                    if (global.isNetworkAvailable()) {
                        new OTPVerify().execute();
                    } else {
                        retryInternet("otp_verify");
                    }
                }
            }
        });

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (global.isNetworkAvailable()) {
                    new OTPResend().execute();
                } else {
                    retryInternet("otp_resend");
                }
            }
        });

        startSMSRetriever();
    }

    private void startSMSRetriever() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task retriever = client.startSmsRetriever();
        retriever.addOnSuccessListener(new OnSuccessListener() {
            public void onSuccess(Object var1) {
                MySMSBroadcastReceiver.OTPListener otpListener = new MySMSBroadcastReceiver.OTPListener() {
                    public void onOTPReceived(String otp) {
                        et1.setText(otp.substring(0,1));
                        et2.setText(otp.substring(1,2));
                        et3.setText(otp.substring(2,3));
                        et4.setText(otp.substring(3,4));
                        Utils.hideKeyboard(context);
                    }

                    public void onOTPTimeOut() {
                    }
                };
                getSmsBroadcastReceiver().injectOTPListener(otpListener);
                registerReceiver(getSmsBroadcastReceiver(), new IntentFilter("com.google.android.gms.auth.api.phone.SMS_RETRIEVED"));
            }
        });

        retriever.addOnFailureListener(new OnFailureListener() {
            public final void onFailure(@NonNull Exception it) {
                Toast.makeText(OTPActivity.this, "Problem to start listener", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MySMSBroadcastReceiver mySMSBroadcastReceiver;

    public synchronized MySMSBroadcastReceiver getSmsBroadcastReceiver() {
        if (mySMSBroadcastReceiver == null) {
            mySMSBroadcastReceiver = new MySMSBroadcastReceiver();
        }
        return mySMSBroadcastReceiver;
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
                if (extraValue.equalsIgnoreCase("otp_verify")) {
                    new OTPVerify().execute();
                } else if (extraValue.equalsIgnoreCase("otp_resend")) {
                    new OTPResend().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class OTPResend extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_OTP_Resend;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", userID);
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
                        otp = jsonObjectList.getString("otp");
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
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class OTPVerify extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_OTP_Verify;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("user_otp", user_otp);
                    restClient.AddParam("userID", userID);
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
                        otp = jsonObjectList.getString("otp");
                        screen = jsonObjectList.getString("screen");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("user_detail");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        userID = jsonObjectList.getString("userID");
                                        name = jsonObjectList.getString("name");
                                        phone = jsonObjectList.getString("phone");
                                        email = jsonObjectList.getString("email");
                                        image = jsonObjectList.getString("image");
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
                Utils.showToast(context, resMessage);
                if (!screen.equalsIgnoreCase("otp")) {
                    AppPreference.setPreference(context, AppPersistence.keys.USER_ID, userID);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);
                    if (LoginActivity.from.equalsIgnoreCase("address")) {
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        Intent id = new Intent(context, MainActivity.class);
                        startActivity(id);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                }
            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    private void initComp() {
        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        tvNumber = findViewById(R.id.tvNumber);
        tvVerify = findViewById(R.id.tvVerify);
        tvResend = findViewById(R.id.tvResend);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
