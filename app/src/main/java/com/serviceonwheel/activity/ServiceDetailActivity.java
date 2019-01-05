package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.adapter.ServicePageAdapter;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.fragment.ServiceLevelFourFragment;
import com.serviceonwheel.fragment.ServiceLevelOneFragment;
import com.serviceonwheel.fragment.ServiceLevelThreeFragment;
import com.serviceonwheel.fragment.ServiceLevelTwoFragment;
import com.serviceonwheel.listner.GlobalListener;
import com.serviceonwheel.model.LevelOneService;
import com.serviceonwheel.model.LevelThreeService;
import com.serviceonwheel.model.LevelTwoService;
import com.serviceonwheel.model.ServiceList;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class ServiceDetailActivity extends BaseActivity implements GlobalListener {

    Activity context;

    TextView backBTN, nextBTN, submit, tvDesc;

    ProgressBar progressCount;

    private ViewPager viewPager;

    RelativeLayout mainRv;

    int currentPage = -1;

    int level_one_position = -1;

    int level_two_position = -1;

    int level_three_position = -1;

    ServiceLevelOneFragment serviceLevelOneFragment;

    ServiceList serviceList;

    String resMessage = "", resCode = "";

    List<LevelOneService> levelOneServices = new ArrayList<>();

    String selected_price = "", selected_display_price = "", selected_service_id = "";

    public static String description = "", notes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        context = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            serviceList = (ServiceList) getIntent().getSerializableExtra("service");
        }

        initToolbar();

        initComp();

        setupPager(viewPager);

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (servicePageAdapter.getCount() > currentPage) {
                    viewPager.setCurrentItem(currentPage + 1, true);
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (String.valueOf(servicePageAdapter.getPageTitle(position)).equalsIgnoreCase("last")) {
                    submit.setVisibility(View.VISIBLE);
                    nextBTN.setVisibility(View.GONE);
                } else {
                    submit.setVisibility(View.GONE);
                    nextBTN.setVisibility(View.VISIBLE);
                }
                currentPage = position;
                if (position > 0) {
                    backBTN.setVisibility(View.VISIBLE);
                } else {
                    backBTN.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddressActivity.class);
                intent.putExtra("Button", "Yes");
                intent.putExtra("selected_display_price", selected_display_price);
                intent.putExtra("selected_price", selected_price);
                intent.putExtra("selected_service_id", selected_service_id);
                startActivity(intent);
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        if (global.isNetworkAvailable()) {
            new ServiceDetail().execute();
        } else {
            retryInternet("service_detail");
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ServiceDetail extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Service_Detail
                    + "&app_type=Android"
                    + "&cityID=" + Utils.getLocationId(context)
                    + "&userID=" + Utils.getUserId(context)
                    + "&serviceID=" + serviceList.getId();

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    /*restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("serviceID", serviceList.getId());
                    restClient.AddParam("cityID", Utils.getLocationId(context));*/
                    restClient.Execute(RequestMethod.GET);
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
                        description = jsonObjectList.getString("description");
                        notes = jsonObjectList.getString("notes");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("first_service_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        LevelOneService levelOneService = new LevelOneService();

                                        List<LevelTwoService> levelTwoServices = new ArrayList<>();

                                        JSONArray jsonArray1 = jsonObjectList.getJSONArray("second_service_list");
                                        {
                                            if (jsonArray1 != null && jsonArray1.length() != 0) {
                                                for (int j = 0; j < jsonArray1.length(); j++) {
                                                    JSONObject jsonObjectList1 = jsonArray1.getJSONObject(j);
                                                    LevelTwoService levelTwoService = new LevelTwoService();

                                                    List<LevelThreeService> levelThreeServices = new ArrayList<>();

                                                    JSONArray jsonArray2 = jsonObjectList1.getJSONArray("third_service_list");
                                                    {
                                                        if (jsonArray2 != null && jsonArray2.length() != 0) {
                                                            for (int k = 0; k < jsonArray2.length(); k++) {
                                                                JSONObject jsonObjectList2 = jsonArray2.getJSONObject(k);
                                                                LevelThreeService levelThreeService = new LevelThreeService();
                                                                levelThreeService.setQ_title(jsonObjectList2.getString("q_title"));
                                                                levelThreeService.setServiceID(jsonObjectList2.getString("serviceID"));
                                                                levelThreeService.setName(jsonObjectList2.getString("name"));

                                                                levelThreeService.setDescription(jsonObjectList2.getString("description"));
                                                                levelThreeService.setNotes(jsonObjectList2.getString("notes"));
                                                                levelThreeService.setService_price(jsonObjectList2.getString("service_price"));
                                                                levelThreeService.setDisplay_service_price(jsonObjectList2.getString("display_service_price"));

                                                                levelThreeServices.add(levelThreeService);
                                                            }
                                                        }
                                                    }

                                                    levelTwoService.setQ_title(jsonObjectList1.getString("q_title"));
                                                    levelTwoService.setServiceID(jsonObjectList1.getString("serviceID"));
                                                    levelTwoService.setName(jsonObjectList1.getString("name"));
                                                    levelTwoService.setLevelThreeServices(levelThreeServices);

                                                    levelTwoService.setDescription(jsonObjectList1.getString("description"));
                                                    levelTwoService.setNotes(jsonObjectList1.getString("notes"));
                                                    levelTwoService.setService_price(jsonObjectList1.getString("service_price"));
                                                    levelTwoService.setDisplay_service_price(jsonObjectList1.getString("display_service_price"));

                                                    levelTwoServices.add(levelTwoService);
                                                }
                                            }
                                        }

                                        levelOneService.setQ_title(jsonObjectList.getString("q_title"));
                                        levelOneService.setServiceID(jsonObjectList.getString("serviceID"));
                                        levelOneService.setName(jsonObjectList.getString("name"));
                                        levelOneService.setDescription(jsonObjectList.getString("description"));
                                        levelOneService.setNotes(jsonObjectList.getString("notes"));
                                        levelOneService.setService_price(jsonObjectList.getString("service_price"));
                                        levelOneService.setDisplay_service_price(jsonObjectList.getString("display_service_price"));
                                        levelOneService.setLevelTwoServices(levelTwoServices);
                                        levelOneServices.add(levelOneService);
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
            } else {
                tvDesc.setText(description);
                if (levelOneServices.size() != 0) {
                    if (serviceLevelOneFragment == null) {
                        serviceLevelOneFragment = new ServiceLevelOneFragment();
                        servicePageAdapter.addFragment(serviceLevelOneFragment, "");
                        serviceLevelOneFragment.setData(levelOneServices);
                    }
                }
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
                if (extraValue.equalsIgnoreCase("service_detail")) {
                    new ServiceDetail().execute();
                }
            }
        }
    }

    ServicePageAdapter servicePageAdapter;

    private void setupPager(ViewPager viewPager) {
        servicePageAdapter = new ServicePageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(servicePageAdapter);
        viewPager.setOffscreenPageLimit(4);
    }

    @Override
    public void onRadioClick(int position, int pageNumber) {
        if (pageNumber == 1) {

            if (position == level_one_position) {
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(1, true);
                    }
                }, 500);
            } else {

                int viewPagerTotal = servicePageAdapter.getCount();

                if (viewPagerTotal > 1) {
                    int a = 0;
                    for (int i = 1; i < viewPagerTotal; i++) {
                        servicePageAdapter.removeFragment(i - a);
                        a++;
                    }
                }

                level_one_position = position;
                level_two_position = -1;
                level_three_position = -1;

                selected_price = levelOneServices.get(level_one_position).getService_price();
                selected_display_price = levelOneServices.get(level_one_position).getDisplay_service_price();
                selected_service_id = levelOneServices.get(level_one_position).getServiceID();

                if (levelOneServices.get(level_one_position).getLevelTwoServices().size() == 0) {

                    viewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ServiceLevelFourFragment serviceLevelFourFragment = new ServiceLevelFourFragment();
                            servicePageAdapter.addFragment(serviceLevelFourFragment, "last");
                            viewPager.setCurrentItem(1, true);
                        }
                    }, 500);

                } else {

                    viewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ServiceLevelTwoFragment serviceLevelTwoFragment = new ServiceLevelTwoFragment();
                            servicePageAdapter.addFragment(serviceLevelTwoFragment, "");
                            serviceLevelTwoFragment.setData(levelOneServices.get(level_one_position).getLevelTwoServices());
                            viewPager.setCurrentItem(1, true);
                        }
                    }, 500);
                }
            }
        } else if (pageNumber == 2) {

            if (position == level_two_position) {
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(2, true);
                    }
                }, 500);
            } else {

                int viewPagerTotal = servicePageAdapter.getCount();

                if (viewPagerTotal > 2) {
                    int a = 0;
                    for (int i = 2; i < viewPagerTotal; i++) {
                        servicePageAdapter.removeFragment(i - a);
                        a++;
                    }
                }

                level_two_position = position;
                level_three_position = -1;

                selected_price = levelOneServices.get(level_one_position)
                        .getLevelTwoServices().get(level_two_position).getService_price();

                selected_display_price = levelOneServices.get(level_one_position)
                        .getLevelTwoServices().get(level_two_position).getDisplay_service_price();

                selected_service_id = levelOneServices.get(level_one_position)
                        .getLevelTwoServices().get(level_two_position).getServiceID();

                if (levelOneServices.get(level_one_position)
                        .getLevelTwoServices().get(level_two_position)
                        .getLevelThreeServices().size() == 0) {

                    viewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ServiceLevelFourFragment serviceLevelFourFragment = new ServiceLevelFourFragment();
                            servicePageAdapter.addFragment(serviceLevelFourFragment, "last");
                            viewPager.setCurrentItem(2, true);
                        }
                    }, 500);

                } else {
                    viewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ServiceLevelThreeFragment serviceLevelThreeFragment = new ServiceLevelThreeFragment();
                            servicePageAdapter.addFragment(serviceLevelThreeFragment, "");
                            serviceLevelThreeFragment.setData(levelOneServices.get(level_one_position)
                                    .getLevelTwoServices().get(level_two_position)
                                    .getLevelThreeServices());
                            viewPager.setCurrentItem(2, true);
                        }
                    }, 500);

                }
            }

        } else if (pageNumber == 3) {

            if (position == level_three_position) {
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(3, true);
                    }
                }, 500);
            } else {

                int viewPagerTotal = servicePageAdapter.getCount();

                if (viewPagerTotal > 3) {
                    int a = 0;
                    for (int i = 3; i < viewPagerTotal; i++) {
                        servicePageAdapter.removeFragment(i - a);
                        a++;
                    }
                }

                level_three_position = position;

                selected_price = levelOneServices.get(level_one_position)
                        .getLevelTwoServices().get(level_two_position)
                        .getLevelThreeServices().get(level_three_position).getService_price();

                selected_display_price = levelOneServices.get(level_one_position)
                        .getLevelTwoServices().get(level_two_position)
                        .getLevelThreeServices().get(level_three_position).getDisplay_service_price();

                selected_service_id = levelOneServices.get(level_one_position)
                        .getLevelTwoServices().get(level_two_position)
                        .getLevelThreeServices().get(level_three_position).getServiceID();

                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ServiceLevelFourFragment serviceLevelFourFragment = new ServiceLevelFourFragment();
                        servicePageAdapter.addFragment(serviceLevelFourFragment, "last");
                        viewPager.setCurrentItem(3, true);
                    }
                }, 500);

            }
        }
    }

    private void initComp() {
        viewPager = findViewById(R.id.pagerService);
        progressCount = findViewById(R.id.progressCount);
        progressCount.getProgressDrawable().setColorFilter(Color.parseColor("#0086b4"), PorterDuff.Mode.SRC_IN);
        mainRv = findViewById(R.id.mainrel);
        backBTN = findViewById(R.id.backBTN);
        nextBTN = findViewById(R.id.nextBTN);
        submit = findViewById(R.id.submit);
        tvDesc = findViewById(R.id.tvDesc);
    }

    @SuppressLint("SetTextI18n")
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
        toolbarTitle.setText(serviceList.getName());
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
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}