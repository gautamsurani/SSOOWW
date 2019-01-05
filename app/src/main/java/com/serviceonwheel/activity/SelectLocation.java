package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.adapter.CitySelectAdapter;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.CityListModel;
import com.serviceonwheel.preference.AppPersistence;
import com.serviceonwheel.preference.AppPreference;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class SelectLocation extends BaseActivity {

    Context context;

    RecyclerView rvSelectCity;

    CitySelectAdapter citySelectAdapter;

    ArrayList<CityListModel> cityListModels = new ArrayList<>();

    ArrayList<CityListModel> cityListResult = new ArrayList<>();

    EditText etSelectedCity;

    LinearLayout lyt_other_area;

    String resMessage = "", resCode = "";

    ProgressBar pbProgress;

    RelativeLayout rlRecyclerView;

    TextView tvSkip, tvOtherCity1, tvOtherCity2;

    String default_city, default_city_name;

    @Override
    protected void onResume() {
        super.onResume();
        if (global.isNetworkAvailable()) {
            new GetCity().execute();
        } else {
            retryInternet("get_city");
        }
    }

    private void setCity() {
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
                                AppPreference.setPreference(context, AppPersistence.keys.Location_Id,
                                        cityListResult.get(position).getCityId());
                                AppPreference.setPreference(context, AppPersistence.keys.Location_Name,
                                        cityListResult.get(position).getName());
                                onBackPressed();
                            }
                        }
                    });
                    if (cityListResult.size() == 0) {
                        lyt_other_area.setVisibility(View.VISIBLE);
                        rlRecyclerView.setVisibility(View.GONE);
                    } else {
                        rlRecyclerView.setVisibility(View.VISIBLE);
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
                    AppPreference.setPreference(context, AppPersistence.keys.Location_Id,
                            cityListModels.get(position).getCityId());
                    AppPreference.setPreference(context, AppPersistence.keys.Location_Name,
                            cityListModels.get(position).getName());
                    onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        context = this;

        initComp();

        RecyclerView.LayoutManager mLayoutManagerMain = new LinearLayoutManager(context);
        rvSelectCity.setLayoutManager(mLayoutManagerMain);
        rvSelectCity.setHasFixedSize(true);
        citySelectAdapter = new CitySelectAdapter(context, cityListModels);
        rvSelectCity.setAdapter(citySelectAdapter);

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvOtherCity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent codeOpen = new Intent(context, HelpActivity.class);
                startActivity(codeOpen);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tvOtherCity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent codeOpen = new Intent(context, HelpActivity.class);
                startActivity(codeOpen);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetCity extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbProgress.setVisibility(View.VISIBLE);
            cityListModels.clear();
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
                        default_city = jsonObjectList.getString("default_city");
                        default_city_name = jsonObjectList.getString("default_city_name");
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
            pbProgress.setVisibility(View.GONE);
            if (!resCode.equalsIgnoreCase("0")) {
                tvSkip.setVisibility(View.GONE);
                Utils.showToast(context, resMessage);
            } else {
                tvSkip.setVisibility(View.VISIBLE);
                citySelectAdapter.notifyDataSetChanged();
                if (cityListModels.size() == 0) {
                    lyt_other_area.setVisibility(View.VISIBLE);
                    rlRecyclerView.setVisibility(View.GONE);
                } else {
                    rlRecyclerView.setVisibility(View.VISIBLE);
                    lyt_other_area.setVisibility(View.GONE);
                }
                setCity();
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
                if (extraValue.equalsIgnoreCase("get_city")) {
                    new GetCity().execute();
                }
            }
        }
    }

    private void initComp() {
        rvSelectCity = findViewById(R.id.rvSelectCity);
        etSelectedCity = findViewById(R.id.etSelectedcity);
        lyt_other_area = findViewById(R.id.lyt_other_area);
        pbProgress = findViewById(R.id.pbProgress);
        rlRecyclerView = findViewById(R.id.rlRecyclerView);
        tvSkip = findViewById(R.id.tvSkip);
        tvOtherCity1 = findViewById(R.id.tvOtherCity1);
        tvOtherCity2 = findViewById(R.id.tvOtherCity2);
    }

    @Override
    public void onBackPressed() {
        if (Utils.getLocationId(context) == null) {
            AppPreference.setPreference(context, AppPersistence.keys.Location_Id,
                    default_city);
            AppPreference.setPreference(context, AppPersistence.keys.Location_Name,
                    default_city_name);
        }
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}