package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.adapter.NotificationAdapter;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.NotificationList;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class NotificationActivity extends BaseActivity {

    Context context;

    RecyclerView rvNotification;

    private NotificationAdapter notificationAdapter;

    ArrayList<NotificationList> notificationLists = new ArrayList<>();

    String resMessage = "", resCode = "";

    int page = 0;

    ProgressBar pbProgress;

    int this_visible_item_count = 0;

    int visibleItemCount, totalItemCount, pastVisibleItems;

    boolean IsLAstLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        context = this;

        initComp();

        initToolbar();

        notificationAdapter = new NotificationAdapter(notificationLists, context);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        rvNotification.setLayoutManager(mLayoutManager);
        rvNotification.setAdapter(notificationAdapter);

        rvNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    this_visible_item_count = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (IsLAstLoading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount &&
                                recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                            IsLAstLoading = false;
                            page++;
                            new GetNotification().execute();
                        }
                    }
                }
            }
        });

        notificationAdapter.setOnItemClickListener(new NotificationAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int witch) {
                NotificationList notificationList = notificationLists.get(position);
                Intent intent = new Intent(context, NotificationDetailActivity.class);
                intent.putExtra("notification", notificationList);
                context.startActivity(intent);
            }
        });

        if (global.isNetworkAvailable()) {
            new GetNotification().execute();
        } else {
            retryInternet("notification");
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetNotification extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (page == 0) {
                startProgressDialog();
            } else {
                pbProgress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Notification;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("cityID", Utils.getLocationId(context));
                    restClient.AddParam("pagecode", String.valueOf(page));
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("notification_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        NotificationList notificationList = new NotificationList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        notificationList.setOffer_ID(jsonObjectList.getString("offer_ID"));
                                        notificationList.setTitle(jsonObjectList.getString("title"));
                                        notificationList.setImage(jsonObjectList.getString("image"));
                                        notificationList.setMessage(jsonObjectList.getString("message"));
                                        notificationList.setAdded_on(jsonObjectList.getString("added_on"));
                                        notificationList.setShre_msg(jsonObjectList.getString("shre_msg"));
                                        notificationLists.add(notificationList);
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
            pbProgress.setVisibility(View.GONE);
            IsLAstLoading = true;
            if (!resCode.equalsIgnoreCase("0")) {
                if (page == 0) {
                    Utils.showToast(context, resMessage);
                }
            } else {
                notificationAdapter.notifyDataSetChanged();
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
                if (extraValue.equalsIgnoreCase("notification")) {
                    new GetNotification().execute();
                }
            }
        }
    }

    private void initComp() {
        rvNotification = findViewById(R.id.rvOffer);
        pbProgress = findViewById(R.id.pbProgress);
    }

    public void initToolbar() {
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
        toolbarTitle.setText(getResources().getString(R.string.toolbar_notification));
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
        Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}
