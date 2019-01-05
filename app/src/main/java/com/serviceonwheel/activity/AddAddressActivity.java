package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.adapter.CitySelectAdapter;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.AddressList;
import com.serviceonwheel.model.CityListModel;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class AddAddressActivity extends BaseActivity {

    Activity context;

    LinearLayout llBottom;

    TextView tvSpinnerCity, tvAddAddress;

    Dialog dialog;

    RecyclerView rvSelectCity;

    CitySelectAdapter citySelectAdapter;

    ArrayList<CityListModel> cityListModels = new ArrayList<>();

    ArrayList<CityListModel> cityListResult = new ArrayList<>();

    String resMessage = "", resCode = "", selectedCityId = "", addressId = "", userId = "";

    EditText etName, etNumber, etAddress1, etAddress2, etPinCode;

    RadioGroup rgAddressLocation;

    RadioButton radioButton, radioHome, radioOffice;

    String address_type = "", name = "", phone_number = "", address1 = "", address2 = "", pincode = "";

    AddressList addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        context = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            addressList = (AddressList) getIntent().getSerializableExtra("address");
        }

        initComp();

        if (addressList != null) {
            setData();
        }

        initToolbar();

        userId = Utils.getUserId(context);

        if (global.isNetworkAvailable()) {
            new GetCity().execute();
        } else {
            retryInternet("get_city");
        }

        llBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = rgAddressLocation.getCheckedRadioButtonId();
                radioButton = findViewById(selectedId);
                address_type = radioButton.getText().toString();
                name = etName.getText().toString();
                phone_number = etNumber.getText().toString();
                address1 = etAddress1.getText().toString();
                address2 = etAddress2.getText().toString();
                pincode = etPinCode.getText().toString();
                if (name.isEmpty()) {
                    Utils.showToast(context, "Enter full name!");
                } else if (phone_number.isEmpty()) {
                    Utils.showToast(context, "Enter phone number!");
                } else if (phone_number.length() < 10) {
                    Utils.showToast(context, "Enter valid phone number!");
                } else if (address1.isEmpty()) {
                    Utils.showToast(context, "Enter address!");
                } else if (selectedCityId.isEmpty()) {
                    Utils.showToast(context, "Select city!");
                } else if (pincode.isEmpty()) {
                    Utils.showToast(context, "Enter pincode!");
                } else if (pincode.length() < 6) {
                    Utils.showToast(context, "Enter valid pincode!");
                } else {
                    if (global.isNetworkAvailable()) {
                        new AddAddress().execute();
                    } else {
                        retryInternet("add_address");
                    }
                }
            }
        });

        tvSpinnerCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCity();
            }
        });
    }

    private void setData() {
        addressId = addressList.getAddressID();
        selectedCityId = addressList.getCityID();
        address_type = addressList.getAddress_type();
        if (address_type.equals("Home")) {
            radioHome.setChecked(true);
        } else {
            radioOffice.setChecked(true);
        }
        etName.setText(addressList.getName());
        etNumber.setText(addressList.getPhone());
        etAddress1.setText(addressList.getAddress1());
        etAddress2.setText(addressList.getAddress2());
        etPinCode.setText(addressList.getPincode());
        tvSpinnerCity.setText(addressList.getCity());
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
                                    tvSpinnerCity.setText(cityListResult.get(position).getName());
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
                        tvSpinnerCity.setText(cityListModels.get(position).getName());
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

    @SuppressLint("StaticFieldLeak")
    private class AddAddress extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Add_Address;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", userId);
                    restClient.AddParam("cityID", selectedCityId);
                    restClient.AddParam("add_name", name);
                    restClient.AddParam("add_phone", phone_number);
                    restClient.AddParam("add_address_1", address1);
                    restClient.AddParam("add_address_2", address2);
                    restClient.AddParam("add_area", "");
                    restClient.AddParam("add_pincode", pincode);
                    restClient.AddParam("add_address_type", address_type);
                    restClient.AddParam("addressID", addressId);
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
            if (resCode.equalsIgnoreCase("0")) {
                Utils.showToast(context, resMessage);
                onBackPressed();
            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    private void initComp() {
        llBottom = findViewById(R.id.llBottom);
        tvSpinnerCity = findViewById(R.id.tvSpinnerCity);
        etName = findViewById(R.id.etName);
        etNumber = findViewById(R.id.etNumber);
        etAddress1 = findViewById(R.id.etAddress1);
        etAddress2 = findViewById(R.id.etAddress2);
        etPinCode = findViewById(R.id.etPinCode);
        rgAddressLocation = findViewById(R.id.rgAddressLocation);
        radioHome = findViewById(R.id.radioHome);
        radioOffice = findViewById(R.id.radioOffice);
        tvAddAddress = findViewById(R.id.tvAddAddress);
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
                } else if (extraValue.equalsIgnoreCase("add_address")) {
                    new AddAddress().execute();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tvTitle);
        if (addressList != null) {
            toolbarTitle.setText("Edit Address");
            tvAddAddress.setText("Edit Address");
        } else {
            toolbarTitle.setText("Add Address");
            tvAddAddress.setText("Add Address");
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
