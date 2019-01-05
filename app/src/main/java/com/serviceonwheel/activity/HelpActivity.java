package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.serviceonwheel.R;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.HelpList;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class HelpActivity extends BaseActivity implements View.OnClickListener {

    Context context;

    String name, email, phone, msg;

    RelativeLayout relativeMain;

    private EditText edtName, edtEmail, edtPhone, edtMsg;

    private Button btnSubmit;

    ExpandableRelativeLayout expandableLayout1, expandableLayout2,
            expandableLayout3, expandableLayout4, expandableLayout5;

    TextView tvRow1, tvRow2, tvRow3, tvRow4, tvRow5;

    String resMessage = "", resCode = "";

    List<HelpList> helpLists = new ArrayList<>();

    Button expandableButton1, expandableButton2, expandableButton3, expandableButton4, expandableButton5;

    LinearLayout LLRow1, LLRow2, LLRow3, LLRow4, LLRow5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        context = this;

        initToolbar();

        initComponent();

        btnSubmit.setOnClickListener(this);

        edtName.setText(Utils.getUserName(context));
        edtPhone.setText(Utils.getMobileNo(context));
        edtEmail.setText(Utils.getEmail(context));

        if (global.isNetworkAvailable()) {
            new GetHelp().execute();
        } else {
            retryInternet("get_help");
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetHelp extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Help_Data;

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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("help_data");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        HelpList helpList = new HelpList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        helpList.setTitle(jsonObjectList.getString("title"));
                                        helpList.setDesc(jsonObjectList.getString("desc"));
                                        helpList.setColor_code(jsonObjectList.getString("color_code"));
                                        helpLists.add(helpList);
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
                for (int i = 0; i < helpLists.size(); i++) {
                    switch (i) {
                        case 0:
                            LLRow1.setVisibility(View.VISIBLE);
                            tvRow1.setText(helpLists.get(i).getDesc());
                            expandableButton1.setText(helpLists.get(i).getTitle());
                            expandableButton1.setBackgroundColor(Color.parseColor(helpLists.get(i).getColor_code()));
                            expandableLayout1.setBackgroundColor(Color.parseColor(helpLists.get(i).getColor_code()));
                            break;
                        case 1:
                            LLRow2.setVisibility(View.VISIBLE);
                            tvRow2.setText(helpLists.get(i).getDesc());
                            expandableButton2.setText(helpLists.get(i).getTitle());
                            expandableButton2.setBackgroundColor(Color.parseColor(helpLists.get(i).getColor_code()));
                            expandableLayout2.setBackgroundColor(Color.parseColor(helpLists.get(i).getColor_code()));
                            break;
                        case 2:
                            LLRow3.setVisibility(View.VISIBLE);
                            tvRow3.setText(helpLists.get(i).getDesc());
                            expandableButton3.setText(helpLists.get(i).getTitle());
                            expandableButton3.setBackgroundColor(Color.parseColor(helpLists.get(i).getColor_code()));
                            expandableLayout3.setBackgroundColor(Color.parseColor(helpLists.get(i).getColor_code()));
                            break;
                        case 3:
                            LLRow4.setVisibility(View.VISIBLE);
                            tvRow4.setText(helpLists.get(i).getDesc());
                            expandableButton4.setText(helpLists.get(i).getTitle());
                            expandableButton4.setBackgroundColor(Color.parseColor(helpLists.get(i).getColor_code()));
                            expandableLayout4.setBackgroundColor(Color.parseColor(helpLists.get(i).getColor_code()));
                            break;
                        case 4:
                            LLRow5.setVisibility(View.VISIBLE);
                            tvRow5.setText(helpLists.get(i).getDesc());
                            expandableButton5.setText(helpLists.get(i).getTitle());
                            expandableButton5.setBackgroundColor(Color.parseColor(helpLists.get(i).getColor_code()));
                            expandableLayout5.setBackgroundColor(Color.parseColor(helpLists.get(i).getColor_code()));
                            break;
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
                if (extraValue.equalsIgnoreCase("get_help")) {
                    new GetHelp().execute();
                } else if (extraValue.equalsIgnoreCase("help")) {
                    new Help().execute();
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Help extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Help;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("name", name);
                    restClient.AddParam("email", email);
                    restClient.AddParam("contact_no", phone);
                    restClient.AddParam("message", msg);
                    restClient.AddParam("userID", Utils.getUserId(context));
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
            if (resCode.equalsIgnoreCase("0")) {
                onBackPressed();
            }
        }
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbar_title = toolbar.findViewById(R.id.tvTitle);
        toolbar_title.setText(getResources().getString(R.string.toolbar_help));
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

    public void initComponent() {
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtMsg = findViewById(R.id.edtMsg);
        btnSubmit = findViewById(R.id.btnSubmitComplaint);
        relativeMain = findViewById(R.id.relativeMain);
        tvRow1 = findViewById(R.id.tvRow1);
        tvRow2 = findViewById(R.id.tvRow2);
        tvRow3 = findViewById(R.id.tvRow3);
        tvRow4 = findViewById(R.id.tvRow4);
        tvRow5 = findViewById(R.id.tvRow5);
        expandableButton1 = findViewById(R.id.expandableButton1);
        expandableButton2 = findViewById(R.id.expandableButton2);
        expandableButton3 = findViewById(R.id.expandableButton3);
        expandableButton4 = findViewById(R.id.expandableButton4);
        expandableButton5 = findViewById(R.id.expandableButton5);
        LLRow1 = findViewById(R.id.LLRow1);
        LLRow2 = findViewById(R.id.LLRow2);
        LLRow3 = findViewById(R.id.LLRow3);
        LLRow4 = findViewById(R.id.LLRow4);
        LLRow5 = findViewById(R.id.LLRow5);
        expandableLayout1 = findViewById(R.id.expandableLayout1);
        expandableLayout2 = findViewById(R.id.expandableLayout2);
        expandableLayout3 = findViewById(R.id.expandableLayout3);
        expandableLayout4 = findViewById(R.id.expandableLayout4);
        expandableLayout5 = findViewById(R.id.expandableLayout5);
    }

    @Override
    public void onClick(View v) {

        name = edtName.getText().toString();
        email = edtEmail.getText().toString();
        email = email.replaceAll(" ", "");
        phone = edtPhone.getText().toString();
        msg = edtMsg.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Utils.ShowSnakBar("Enter Name", relativeMain, HelpActivity.this);
        } else if (TextUtils.isEmpty(email)) {
            Utils.ShowSnakBar("Enter Email", relativeMain, HelpActivity.this);
        } else if (!Utils.isValidEmail(email)) {
            Utils.ShowSnakBar("Invalid Email", relativeMain, HelpActivity.this);
        } else if (TextUtils.isEmpty(phone)) {
            Utils.ShowSnakBar("Enter Phone", relativeMain, HelpActivity.this);
        } else if (phone.length() != 10) {
            Utils.ShowSnakBar("Invalid Phone", relativeMain, HelpActivity.this);
        } else if (TextUtils.isEmpty(msg)) {
            Utils.ShowSnakBar("Enter Message", relativeMain, HelpActivity.this);
        } else {
            if (global.isNetworkAvailable()) {
                new Help().execute();
            } else {
                retryInternet("help");
            }
        }
    }

    public void expandableButton1(View view) {
        expandableLayout1.toggle(); // toggle expand and collapse
    }

    public void expandableButton2(View view) {
        expandableLayout2.toggle(); // toggle expand and collapse
    }

    public void expandableButton3(View view) {
        expandableLayout3.toggle(); // toggle expand and collapse
    }

    public void expandableButton4(View view) {
        expandableLayout4.toggle(); // toggle expand and collapse
    }

    public void expandableButton5(View view) {
        expandableLayout5.toggle(); // toggle expand and collapse
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
        Utils.hideKeyboard(HelpActivity.this);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
