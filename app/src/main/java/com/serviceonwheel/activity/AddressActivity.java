package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.adapter.AddressAdapter;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.AddressList;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class AddressActivity extends BaseActivity {

    Context context;

    RecyclerView rvAddress;

    List<AddressList> addressLists = new ArrayList<>();

    AddressAdapter addressAdapter;

    LinearLayout llBottom;

    TextView tvAddAddress, tvMsg, tvPrice;

    private String btn, userId = "", resMessage = "", resCode = "", addressID = "";

    String selected_display_price = "", selected_price = "", selected_service_id = "";

    int addressPosition;

    AlertDialog alertDialog;

    @Override
    protected void onResume() {
        super.onResume();
        userId = Utils.getUserId(context);
        if (Utils.getUserId(context) == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.create();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.popup_sign_in_up, null);

            TextView tvSignIn, tvSignUp;

            tvSignIn = alertLayout.findViewById(R.id.tvSignIn);
            tvSignUp = alertLayout.findViewById(R.id.tvSignUp);

            alertDialogBuilder.setView(alertLayout);

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss(); // dismiss the dialog
                        onBackPressed();
                    }

                    return true;
                }
            });

            alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);

            final AlertDialog finalAlertDialog = alertDialog;
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAlertDialog.dismiss();
                    Intent codeOpen = new Intent(context, LoginActivity.class);
                    codeOpen.putExtra("from", "address");
                    context.startActivity(codeOpen);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            tvSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAlertDialog.dismiss();
                    Intent codeOpen = new Intent(context, LoginActivity.class);
                    codeOpen.putExtra("page", "signup");
                    codeOpen.putExtra("from", "address");
                    context.startActivity(codeOpen);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            alertDialog.show();
        } else {
            if (global.isNetworkAvailable()) {
                new GetAddressList().execute();
            } else {
                retryInternet("address_list");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        context = this;

        initComp();

        intentData();

        initToolbar();

        addressAdapter = new AddressAdapter(addressLists, context, btn);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvAddress.setLayoutManager(linearLayoutManager);
        rvAddress.setAdapter(addressAdapter);

        addressAdapter.setOnItemClickListener(new AddressAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int witch) {
                AddressList addressList = addressLists.get(position);
                if (witch == 1) {
                    Intent intent = new Intent(context, AddAddressActivity.class);
                    intent.putExtra("address", addressList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (witch == 2) {
                    addressID = addressList.getAddressID();
                    addressPosition = position;
                    if (global.isNetworkAvailable()) {
                        new AddressDelete().execute();
                    } else {
                        retryInternet("address_delete");
                    }
                } else if (witch == 3) {
                    addressID = addressList.getAddressID();
                    addressPosition = position;
                }
            }
        });

        llBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addressID.isEmpty()) {
                    if (global.isNetworkAvailable()) {
                        new CheckAvailability().execute();
                    } else {
                        retryInternet("check_availability");
                    }
                } else {
                    Utils.showToast(context, "Select address!");
                }
            }
        });

        tvAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddAddressActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                if (extraValue.equalsIgnoreCase("address_list")) {
                    new GetAddressList().execute();
                } else if (extraValue.equalsIgnoreCase("address_delete")) {
                    new AddressDelete().execute();
                } else if (extraValue.equalsIgnoreCase("check_availability")) {
                    new CheckAvailability().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckAvailability extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_Check_Availability;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", userId);
                    restClient.AddParam("cityID", Utils.getLocationId(context));
                    restClient.AddParam("addressID", addressID);
                    restClient.AddParam("serviceID", selected_service_id);
                    restClient.AddParam("service_price", selected_service_id);
                    restClient.AddParam("display_service_price", selected_display_price);
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
                Intent intent = new Intent(context, OrderSummeryActivity.class);
                intent.putExtra("serviceID", selected_service_id);
                intent.putExtra("addressID", addressID);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (resCode.equalsIgnoreCase("1")) {
                Utils.showToast(context, resMessage);
            } else if (resCode.equalsIgnoreCase("2")) {
                showPopup(resMessage);
            }
        }
    }

    Dialog dialog;

    private void showPopup(String resMessage) {
        dialog = new Dialog(AddressActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.setContentView(R.layout.popup_address_msg);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView text = dialog.findViewById(R.id.tvOk);
        TextView tvMsg = dialog.findViewById(R.id.tvMsg);
        tvMsg.setText(resMessage);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class AddressDelete extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Address_Delete;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", userId);
                    restClient.AddParam("addressID", addressID);
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
            if (!resCode.equalsIgnoreCase("0")) {
                Utils.showToast(context, resMessage);
            } else {
                addressLists.remove(addressPosition);
                addressAdapter.notifyDataSetChanged();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetAddressList extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            addressLists.clear();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Address_List;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", userId);
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("address_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        AddressList addressList = new AddressList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        addressList.setSr(jsonObjectList.getString("Sr"));
                                        addressList.setAddressID(jsonObjectList.getString("addressID"));
                                        addressList.setCityID(jsonObjectList.getString("cityID"));
                                        addressList.setName(jsonObjectList.getString("name"));
                                        addressList.setPhone(jsonObjectList.getString("phone"));
                                        addressList.setAddress1(jsonObjectList.getString("address1"));
                                        addressList.setAddress2(jsonObjectList.getString("address2"));
                                        addressList.setArea(jsonObjectList.getString("area"));
                                        addressList.setCity(jsonObjectList.getString("city"));
                                        addressList.setPincode(jsonObjectList.getString("pincode"));
                                        addressList.setAddress_type(jsonObjectList.getString("address_type"));
                                        addressLists.add(addressList);
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
                tvMsg.setVisibility(View.VISIBLE);
                tvMsg.setText(resMessage);
            } else {
                tvMsg.setVisibility(View.GONE);
                addressAdapter.notifyDataSetChanged();
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void intentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            btn = bundle.getString("Button", "");
            selected_display_price = bundle.getString("selected_display_price", "");
            selected_price = bundle.getString("selected_price", "");
            selected_service_id = bundle.getString("selected_service_id", "");
        }

        try {
            Double aDouble = Double.parseDouble(selected_display_price);
            tvPrice.setText(getResources().getString(R.string.rs) + " " + selected_display_price);
        } catch (Exception e) {
            tvPrice.setText(selected_display_price);
        }
    }


    private void initComp() {
        rvAddress = findViewById(R.id.rvAddress);
        llBottom = findViewById(R.id.llBottom);
        tvAddAddress = findViewById(R.id.tvAddAddress);
        tvMsg = findViewById(R.id.tvMsg);
        tvPrice = findViewById(R.id.tvPrice);
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tvTitle);
        if (btn.equalsIgnoreCase("no")) {
            toolbarTitle.setText("Address");
            llBottom.setVisibility(View.GONE);
        } else {
            toolbarTitle.setText("Select Address");
            llBottom.setVisibility(View.VISIBLE);
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
