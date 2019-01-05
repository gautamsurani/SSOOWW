package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.serviceonwheel.R;
import com.serviceonwheel.adapter.JobAdapter;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.JobList;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class ApplyAsVendorActivity extends BaseActivity {

    Context context;

    RecyclerView rvService;

    List<JobList> jobLists = new ArrayList<>();

    JobAdapter jobAdapter;

    String resMessage = "", resCode = "", h_title = "", h_desc = "";

    TextView tvTitle, tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_as_vendor);

        context = this;

        initToolbar();

        initComp();

        jobAdapter = new JobAdapter(context);
        jobAdapter.setList(jobLists);
        rvService.setLayoutManager(new GridLayoutManager(this, 2));
        rvService.setAdapter(jobAdapter);

        jobAdapter.setOnItemClickListener(new JobAdapter.OnClickListener() {
            @Override
            public void onClick(int position, int witch) {
                JobList jobList = jobLists.get(position);
                if (witch == 0) {
                    Intent intent = new Intent(context, ApplyVendorFormActivity.class);
                    intent.putExtra("job", jobList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        if (global.isNetworkAvailable()) {
            new GetServiceList().execute();
        } else {
            retryInternet("get_service");
        }

    }

    public void retryInternet(String extraValue) {
        Intent i = new Intent(context, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetServiceList extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Job_List;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("cityID", Utils.getLocationId(context));
                    restClient.AddParam("pagecode", "0");
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
                        h_title = jsonObjectList.getString("h_title");
                        h_desc = jsonObjectList.getString("h_desc");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("job_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JobList jobList = new JobList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        jobList.setJobID(jsonObjectList.getString("jobID"));
                                        jobList.setImage(jsonObjectList.getString("image"));
                                        jobList.setTitle(jsonObjectList.getString("title"));
                                        jobList.setDescription(jsonObjectList.getString("description"));
                                        jobList.setDate(jsonObjectList.getString("date"));
                                        jobList.setShre_msg(jsonObjectList.getString("shre_msg"));
                                        jobLists.add(jobList);
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
                tvTitle.setText(h_title);
                tvDesc.setText(h_desc);
                jobAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("get_service")) {
                    new GetServiceList().execute();
                }
            }
        }
    }

    private void initComp() {
        rvService = findViewById(R.id.rvService);
        tvTitle = findViewById(R.id.tvTitle1);
        tvDesc = findViewById(R.id.tvDesc);
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tvTitle);
        toolbarTitle.setText("Apply as Executive");
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
