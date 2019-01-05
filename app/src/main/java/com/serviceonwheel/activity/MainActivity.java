package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.serviceonwheel.R;
import com.serviceonwheel.adapter.ServiceAdapter;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.ServiceList;
import com.serviceonwheel.preference.AppPersistence;
import com.serviceonwheel.preference.AppPreference;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SliderLayout mDemoSlider;

    Activity context;

    ArrayList<String> bannerImage = new ArrayList<>();

    ArrayList<String> bannerId = new ArrayList<>();

    RecyclerView rvService;

    List<ServiceList> serviceLists = new ArrayList<>();

    List<ServiceList> searchServiceLists = new ArrayList<>();

    ServiceAdapter serviceAdapter;

    TextView tvSelectCity, tvUserName, tvMobile, tvNoFound;

    ImageView profile;

    CircleImageView ivUserImage;

    String location_name, location_id, userId, userName;

    RelativeLayout rlLoginView, rlUserDetail;

    LinearLayout llLogout;

    String resMessage = "", resCode = "", deviceId = "", token = "", version = "",
            android_version_msg = "", android_version = "", support_phone_no = "",
            vendor_name = "", vendor_image = "", short_msg = "", orderID = "", vendorID = "";

    PackageInfo pInfo;

    EditText etSearch;

    Dialog dialog;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        context = this;

        setNavBar();

        initComponent();

        deviceId = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        token = FirebaseInstanceId.getInstance().getToken();

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        serviceAdapter = new ServiceAdapter(context);
        serviceAdapter.setList(serviceLists);
        rvService.setLayoutManager(new GridLayoutManager(this, 3));
        rvService.setAdapter(serviceAdapter);

        serviceAdapter.setOnItemClickListener(new ServiceAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int witch) {
                ServiceList serviceList = serviceLists.get(position);
                Intent i = new Intent(context, ServiceDetailActivity.class);
                i.putExtra("service", serviceList);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchServiceLists.clear();
                searchServiceLists = new ArrayList<>();
                try {
                    for (ServiceList c : serviceLists) {
                        if (c.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                            searchServiceLists.add(c);
                        }
                    }
                    serviceAdapter = new ServiceAdapter(context);
                    serviceAdapter.setList(searchServiceLists);
                    rvService.setLayoutManager(new GridLayoutManager(context, 3));
                    rvService.setAdapter(serviceAdapter);
                    serviceAdapter.setOnItemClickListener(new ServiceAdapter.OnClickListener() {
                        @Override
                        public void onClick(int position, int witch) {
                            ServiceList serviceList = searchServiceLists.get(position);
                            Intent i = new Intent(context, ServiceDetailActivity.class);
                            i.putExtra("service", serviceList);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });

                    if (searchServiceLists.size() == 0) {
                        tvNoFound.setVisibility(View.VISIBLE);
                        rvService.setVisibility(View.GONE);
                    } else {
                        rvService.setVisibility(View.VISIBLE);
                        tvNoFound.setVisibility(View.GONE);
                    }
                } catch (NullPointerException ne) {
                    ne.getMessage();
                }
            }
        });

        tvSelectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SelectLocation.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.getUserId(context) != null) {
                    Intent i = new Intent(context, MyAccountActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Utils.loginDialog(context, "You need to be signed in to go my account.");
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        tvMobile.setText(Utils.getMobileNo(context));
        tvUserName.setText(Utils.getUserName(context));
        Glide.with(context)
                .load(Utils.getUserImage(context))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.user_defult)
                        .fitCenter())
                .into(ivUserImage);
        location_name = Utils.getLocationName(context);
        location_id = Utils.getLocationId(context);
        userId = Utils.getUserId(context);
        userName = Utils.getUserName(context);
        if (userName == null) {
            rlLoginView.setVisibility(View.VISIBLE);
            rlUserDetail.setVisibility(View.GONE);
            llLogout.setVisibility(View.GONE);
        } else {
            rlUserDetail.setVisibility(View.VISIBLE);
            rlLoginView.setVisibility(View.GONE);
            llLogout.setVisibility(View.VISIBLE);
        }

        if (location_id == null) {
            tvSelectCity.setText("Select City...");
            startActivity(new Intent(context, SelectLocation.class));
        } else {
            tvSelectCity.setText(location_name);
            if (global.isNetworkAvailable()) {
                new Dashboard().execute();
            } else {
                retryInternet("dashboard");
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Dashboard extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            serviceLists.clear();
            bannerId.clear();
            bannerImage.clear();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Dashboard;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", Utils.getUserId(context));
                    restClient.AddParam("token", token);
                    restClient.AddParam("deviceID", deviceId);
                    restClient.AddParam("versionID", version);
                    restClient.AddParam("cityID", Utils.getLocationId(context));
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
                        android_version = jsonObjectList.getString("android_version");
                        android_version_msg = jsonObjectList.getString("android_version_msg");
                        support_phone_no = jsonObjectList.getString("support_phone_no");

                        vendor_name = jsonObjectList.getString("vendor_name");
                        vendor_image = jsonObjectList.getString("vendor_image");
                        short_msg = jsonObjectList.getString("short_msg");
                        orderID = jsonObjectList.getString("orderID");
                        vendorID = jsonObjectList.getString("vendorID");

                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("service_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        ServiceList serviceList = new ServiceList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        serviceList.setId(jsonObjectList.getString("serviceID"));
                                        serviceList.setName(jsonObjectList.getString("name"));
                                        serviceList.setImage(jsonObjectList.getString("image"));
                                        serviceLists.add(serviceList);
                                    }
                                }
                            }

                            JSONArray jsonArray1 = jsonObjectList.getJSONArray("banner_list");
                            {
                                if (jsonArray1 != null && jsonArray1.length() != 0) {
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray1.getJSONObject(i);
                                        bannerId.add(jsonObjectList.getString("bannerID"));
                                        bannerImage.add(jsonObjectList.getString("image"));
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
                AppPreference.setPreference(context, AppPersistence.keys.support_phone, support_phone_no);
                serviceAdapter.notifyDataSetChanged();
                setBanner(bannerImage);
                boolean new_version_available = compare(version, android_version);
                if (new_version_available) {
                    if (!context.isFinishing()) {
                        showUpdateDialog(android_version_msg);
                    }
                }
                if (!orderID.isEmpty()) {
                    if (Utils.getLastOrderRateData(context) != null) {
                        if (!Utils.getLastOrderRateData(context).equals(Utils.getTodayDate())) {
                            openRatePopup();
                        }
                    } else {
                        openRatePopup();
                    }
                }
            } else if (resCode.equalsIgnoreCase("2")) {
                Utils.Logout(context);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    String note = "";
    String rate = "";

    private void openRatePopup() {
        AppPreference.setPreference(context, AppPersistence.keys.ORDER_LAST_RATE_DATE, Utils.getTodayDate());
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.setContentView(R.layout.popup_rate_vendor);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final RatingBar rbRate = dialog.findViewById(R.id.rbRate);
        final TextView tvRateLabel = dialog.findViewById(R.id.tvRateLabel);
        final TextView tvVendorName = dialog.findViewById(R.id.tvVendorName);
        final TextView tvSDesc = dialog.findViewById(R.id.tvSDesc);
        final CircleImageView ivVendorImage = dialog.findViewById(R.id.ivVendorImage);

        Glide.with(context)
                .load(vendor_image)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.user_defult)
                        .fitCenter())
                .into(ivVendorImage);

        tvVendorName.setText(vendor_name);
        tvSDesc.setText(short_msg);

        rbRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (ratingBar.getRating() < 2) {
                    tvRateLabel.setText("Bad");
                } else if (ratingBar.getRating() < 3) {
                    tvRateLabel.setText("Normal");
                } else if (ratingBar.getRating() < 4) {
                    tvRateLabel.setText("Good");
                } else if (ratingBar.getRating() < 5) {
                    tvRateLabel.setText("Excellent");
                }
            }
        });
        rbRate.setRating(5);

        TextView tvSubmit = dialog.findViewById(R.id.tvSubmit);
        final EditText etNote = dialog.findViewById(R.id.etNote);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (global.isNetworkAvailable()) {
                    note = etNote.getText().toString();
                    rate = String.valueOf(rbRate.getRating());
                    if (note.isEmpty()) {
                        Toast.makeText(context, "Please write a review!", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        new SubmitRate().execute();
                    }
                } else {
                    retryInternet("rate");
                }
            }
        });

        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class SubmitRate extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_Submit_Rate;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("orderID", orderID);
                    restClient.AddParam("userID", userId);
                    restClient.AddParam("vendorID", vendorID);
                    restClient.AddParam("ratt", rate);
                    restClient.AddParam("ratt_desc", note);
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
            if (resCode.endsWith("0")) {
                AppPreference.removePreference(context, AppPersistence.keys.ORDER_LAST_RATE_DATE);
            }
        }
    }

    protected void showUpdateDialog(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Update Available")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        context.finishAffinity();
                        String appPackageName = getPackageName();
                        Uri uri = Uri.parse("market://details?id=" + appPackageName);
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        context.finishAffinity();
                    }
                })
                .create();
        dialog.show();
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
                if (extraValue.equalsIgnoreCase("dashboard")) {
                    new Dashboard().execute();
                } else if (extraValue.equalsIgnoreCase("rate")) {
                    new SubmitRate().execute();
                }
            }
        }
    }

    public void setBanner(ArrayList banner_content) {
        for (int i = 0; i < banner_content.size(); i++) {
            DefaultSliderView textSliderView = new DefaultSliderView(context);
            textSliderView.image(banner_content.get(i).toString())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {

                        }
                    });
            mDemoSlider.addSlider(textSliderView);
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(4000);
        }
    }

    private void initComponent() {
        mDemoSlider = findViewById(R.id.slider);
        rvService = findViewById(R.id.rvService);
        tvSelectCity = findViewById(R.id.tvSelectloc);
        profile = findViewById(R.id.profile);
        etSearch = findViewById(R.id.etSearch);
    }

    private void setNavBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        View headerView = getLayoutInflater().inflate(R.layout.nav_header_main, navigationView, false);
        navigationView.addHeaderView(headerView);

        llLogout = headerView.findViewById(R.id.llLogout);
        LinearLayout llContactUs = headerView.findViewById(R.id.llContactUs);
        LinearLayout llProfile = headerView.findViewById(R.id.llProfile);
        LinearLayout llHelp = headerView.findViewById(R.id.llhelp);
        LinearLayout llRateUs = headerView.findViewById(R.id.llrateus);
        LinearLayout llAboutUs = headerView.findViewById(R.id.llAboutus);
        LinearLayout llShare = headerView.findViewById(R.id.llShareus);
        LinearLayout llNotification = headerView.findViewById(R.id.llNotification);
        LinearLayout llBooking = headerView.findViewById(R.id.llBooking);
        LinearLayout llApplyAsVendor = headerView.findViewById(R.id.llApplyAsVendor);
        LinearLayout llCoupon = headerView.findViewById(R.id.llCoupon);

        tvUserName = headerView.findViewById(R.id.tvUserName);
        tvMobile = headerView.findViewById(R.id.tvMobile);
        ivUserImage = headerView.findViewById(R.id.ivUserImage);
        rlLoginView = headerView.findViewById(R.id.rlLoginView);
        rlUserDetail = headerView.findViewById(R.id.rlUserDetail);

        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (Utils.getUserId(context) != null) {
                    Intent codeOpen = new Intent(context, MyAccountActivity.class);
                    startActivity(codeOpen);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Utils.loginDialog(context, "You need to be signed in for this action.");
                }
            }
        });

        rlLoginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                Utils.loginDialog(context, "You need to be signed in for this action.");
            }
        });

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to Sign Out ?")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Utils.Logout(context);
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        llBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (Utils.getUserId(context) != null) {
                    Intent codeOpen = new Intent(context, OrderListActivity.class);
                    startActivity(codeOpen);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Utils.loginDialog(context, "You need to be signed in for this action.");
                }
            }
        });

        llApplyAsVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                if (Utils.getUserId(context) != null) {
                    Intent codeOpen = new Intent(context, ApplyAsVendorActivity.class);
                    startActivity(codeOpen);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Utils.loginDialog(context, "You need to be signed in for this action.");
                }
            }
        });

        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (Utils.getUserId(context) != null) {
                    Intent codeOpen = new Intent(context, ReferActivity.class);
                    startActivity(codeOpen);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Utils.loginDialog(context, "You need to be signed in for this action.");
                }
            }
        });

        llAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent codeOpen = new Intent(context, AboutUsActivity.class);
                startActivity(codeOpen);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent codeOpen = new Intent(context, HelpActivity.class);
                startActivity(codeOpen);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent codeOpen = new Intent(context, ContactUsActivity.class);
                startActivity(codeOpen);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        rlUserDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (Utils.getUserId(context) != null) {
                    Intent codeOpen = new Intent(context, MyAccountActivity.class);
                    startActivity(codeOpen);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Utils.loginDialog(context, "You need to be signed in for this action.");
                }
            }
        });

        llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                Intent codeOpen = new Intent(context, NotificationActivity.class);
                startActivity(codeOpen);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                if (Utils.getUserId(context) != null) {
                    Intent codeOpen = new Intent(context, CouponActivity.class);
                    startActivity(codeOpen);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Utils.loginDialog(context, "You need to be signed in for this action.");
                }
            }
        });
    }

    private static boolean compare(String v1, String v2) {
        String s1 = normalisedVersion(v1);
        String s2 = normalisedVersion(v2);
        int cmp = s1.compareTo(s2);
        String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        System.out.printf("result: " + "'%s' %s '%s'%n", v1, cmpStr, v2);
        if (cmpStr.contains("<")) {
            return true;
        }
        if (cmpStr.contains(">") || cmpStr.contains("==")) {
            return false;
        }
        return false;
    }

    public static String normalisedVersion(String version) {
        return normalisedVersion(version, 4);
    }

    public static String normalisedVersion(String version, int maxWidth) {
        String[] split = Pattern.compile(".", Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Utils.showToast(context, getResources().getString(R.string.press_again_exit_app));
            exitTime = System.currentTimeMillis();
        } else {
            finishAffinity();
        }
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }
}