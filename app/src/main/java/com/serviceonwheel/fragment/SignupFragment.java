package com.serviceonwheel.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.activity.LoginActivity;
import com.serviceonwheel.activity.MainActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.activity.OTPActivity;
import com.serviceonwheel.activity.WebViewActivity;
import com.serviceonwheel.adapter.CitySelectAdapter;
import com.serviceonwheel.base.BaseFragment;
import com.serviceonwheel.model.CityListModel;
import com.serviceonwheel.preference.AppPersistence;
import com.serviceonwheel.preference.AppPreference;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.Global;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends BaseFragment {

    Activity context;

    TextView tvTAndC;

    Global global;

    String resMessage = "", resCode = "", selectedCityId = "";

    ArrayList<CityListModel> cityListModels = new ArrayList<>();

    ArrayList<CityListModel> cityListResult = new ArrayList<>();

    TextView tvCity, tvSignUp, tvReferPopup;

    EditText etRefer, etName, etNumber, etEmail;

    Dialog dialog;

    RecyclerView rvSelectCity;

    CitySelectAdapter citySelectAdapter;

    String name = "", number = "", email = "", refer_code = "",
            otp = "", screen = "", userID = "", phone = "", image = "";

    public SignupFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

        global = new Global(context);

        initComp(view);

        tvTAndC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("title", "Terms & Conditions");
                intent.putExtra("link", "https://www.serviceonwheel.com/page/terms.html");
                startActivity(intent);
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if (global.isNetworkAvailable()) {
            new GetCity().execute();
        } else {
            retryInternet("get_city");
        }

        tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCity();
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                number = etNumber.getText().toString();
                email = etEmail.getText().toString();
                email = email.replaceAll(" ", "");
                refer_code = etRefer.getText().toString();
                if (name.isEmpty()) {
                    Utils.showToast(context, "Enter your name!");
                } else if (number.isEmpty()) {
                    Utils.showToast(context, "Enter mobile number!");
                } else if (number.length() < 10) {
                    Utils.showToast(context, "Enter valid mobile number!");
                } else if (selectedCityId.isEmpty()) {
                    Utils.showToast(context, "Select city!");
                } else {
                    if (!email.isEmpty()) {
                        if (!Utils.isValidEmail(email)) {
                            Utils.showToast(context, "Enter valid email!");
                            return;
                        }
                    }
                    if (global.isNetworkAvailable()) {
                        new Register().execute();
                    } else {
                        retryInternet("register");
                    }
                }
            }
        });

        tvReferPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopup();
            }
        });
    }

    private void openPopup(){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.setContentView(R.layout.popup_refer_info);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView text = dialog.findViewById(R.id.tvOk);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void selectCity() {
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            assert dialog.getWindow() != null;
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            dialog.setContentView(R.layout.row_selectcity);
            rvSelectCity = dialog.findViewById(R.id.rvSelectCity);
            TextView tvHeaderName = dialog.findViewById(R.id.tvHeadername);
            tvHeaderName.setText("Select City");
            final EditText etSelectedCity = dialog.findViewById(R.id.etSelectedcity);
            final LinearLayout lyt_other_area = dialog.findViewById(R.id.lyt_other_area);
            ImageView imgClose = dialog.findViewById(R.id.imgClose);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    dialog.dismiss();
                }
            });
            RecyclerView.LayoutManager mLayoutManagerMain = new LinearLayoutManager(context);
            rvSelectCity.setLayoutManager(mLayoutManagerMain);
            rvSelectCity.setHasFixedSize(true);
            citySelectAdapter = new CitySelectAdapter(context, cityListModels);
            rvSelectCity.setAdapter(citySelectAdapter);

            etSelectedCity.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cityListResult.clear();
                    cityListResult = new ArrayList<>();
                    try {
                        for (CityListModel c : cityListModels) {
                            if (c.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                cityListResult.add(c);
                            }
                        }
                        citySelectAdapter = new CitySelectAdapter(context, cityListResult);
                        rvSelectCity.setAdapter(citySelectAdapter);
                        citySelectAdapter.setOnItemClickListener(new CitySelectAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position, View view, int which) {
                                if (which == 1) {
                                    Utils.hideKeyboard(context);
                                    tvCity.setText(cityListResult.get(position).getName());
                                    selectedCityId = cityListResult.get(position).getCityId();
                                    dialog.dismiss();
                                }
                            }
                        });
                        if (cityListResult.size() == 0) {
                            lyt_other_area.setVisibility(View.VISIBLE);
                            rvSelectCity.setVisibility(View.GONE);
                        } else {
                            rvSelectCity.setVisibility(View.VISIBLE);
                            lyt_other_area.setVisibility(View.GONE);
                        }
                    } catch (NullPointerException ne) {
                        ne.getMessage();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            citySelectAdapter.setOnItemClickListener(new CitySelectAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view, int which) {
                    if (which == 1) {
                        Utils.hideKeyboard(context);
                        tvCity.setText(cityListModels.get(position).getName());
                        selectedCityId = cityListModels.get(position).getCityId();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else {
            dialog.show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Register extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Sign_Up;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("user_name", name);
                    restClient.AddParam("user_email", email);
                    restClient.AddParam("user_phone", number);
                    restClient.AddParam("user_reff_code", refer_code);
                    restClient.AddParam("cityID", selectedCityId);
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

    @SuppressLint("StaticFieldLeak")
    private class GetCity extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_City_List;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("city_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        CityListModel cityListModel = new CityListModel();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        cityListModel.setCityId(jsonObjectList.getString("cityID"));
                                        cityListModel.setName(jsonObjectList.getString("city_name"));
                                        cityListModels.add(cityListModel);
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
            if (!resCode.equalsIgnoreCase("0")) {
                Utils.showToast(context, resMessage);
            }
        }
    }

    private void initComp(View view) {
        tvTAndC = view.findViewById(R.id.tvTAndC);
        tvCity = view.findViewById(R.id.tvCity);
        tvSignUp = view.findViewById(R.id.tvSignUp);
        etRefer = view.findViewById(R.id.etRefer);
        etName = view.findViewById(R.id.etName);
        etNumber = view.findViewById(R.id.etNumber);
        etEmail = view.findViewById(R.id.etEmail);
        tvReferPopup = view.findViewById(R.id.tvReferPopup);
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
                if (extraValue.equalsIgnoreCase("get_city")) {
                    new GetCity().execute();
                } else if (extraValue.equalsIgnoreCase("register")) {
                    new Register().execute();
                }
            }
        }
    }
}