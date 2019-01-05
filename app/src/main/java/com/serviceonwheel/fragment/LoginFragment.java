package com.serviceonwheel.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.activity.LoginActivity;
import com.serviceonwheel.activity.MainActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.activity.OTPActivity;
import com.serviceonwheel.base.BaseFragment;
import com.serviceonwheel.preference.AppPersistence;
import com.serviceonwheel.preference.AppPreference;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.Global;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment {

    TextView tvLogin;

    View view;

    Activity context;

    EditText etMobile;

    String number = "", resMessage = "", resCode = "", otp = "", screen = "", userID = "", name = "",
            phone = "", email = "", image = "";

    Global global;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);

        context = getActivity();

        global = new Global(context);

        initComp();

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = etMobile.getText().toString();
                if (number.isEmpty()) {
                    Utils.showToast(context, "Enter mobile number!");
                } else if (number.length() < 10) {
                    Utils.showToast(context, "Enter valid mobile number!");
                } else {
                    if (global.isNetworkAvailable()) {
                        new Login().execute();
                    } else {
                        retryInternet("login");
                    }
                }
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class Login extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Sign_In;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("user_phone", number);
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
                if (screen.equalsIgnoreCase("otp")) {

                    if (LoginActivity.from.equalsIgnoreCase("address")) {
                        Intent intent = new Intent(context, OTPActivity.class);
                        intent.putExtra("otp", otp);
                        intent.putExtra("phone", phone);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        context.finish();
                    } else {
                        Intent intent = new Intent(context, OTPActivity.class);
                        intent.putExtra("otp", otp);
                        intent.putExtra("phone", phone);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                } else {
                    AppPreference.setPreference(context, AppPersistence.keys.USER_ID, userID);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                    AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);

                    if (LoginActivity.from.equalsIgnoreCase("address")) {
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        context.finish();
                    } else {
                        Intent id = new Intent(context, MainActivity.class);
                        startActivity(id);
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        context.finish();
                    }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("login")) {
                    new Login().execute();
                }
            }
        }
    }

    private void initComp() {
        tvLogin = view.findViewById(R.id.tvLogin);
        etMobile = view.findViewById(R.id.etMobile);
    }
}