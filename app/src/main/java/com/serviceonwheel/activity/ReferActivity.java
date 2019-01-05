package com.serviceonwheel.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serviceonwheel.R;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.Global;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class ReferActivity extends BaseActivity {

    Toolbar toolbar;
    ImageView imgReferfriend;
    TextView tvReferAmount, tvReferlink;
    LinearLayout sharewp, sharefb, sharemail, sharmore;
    String strUserId = "";
    LinearLayout relativeMain;
    String strimage = "", strmessage = "", strshare_image = "", strref_key = "", stryou_get = "", stryou_friend_get = "";
    String WhichShareType = "";
    Context context;
    String resMessage = "", resCode = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        context = this;
        global = new Global(this);
        initToolbar();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        strUserId = Utils.getUserId(context);
        initComponent();

        tvReferlink.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() <= tvReferlink.getRight()) {
                        Utils.ShowSnakBar("Refer code Copied", relativeMain, ReferActivity.this);
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("", strmessage);
                        assert clipboard != null;
                        clipboard.setPrimaryClip(clip);
                        return true;
                    }
                }
                return true;
            }
        });

        sharewp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhichShareType = "WhatsApp";
                shareMore();

            }
        });
        sharefb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhichShareType = "facebook";
                shareMore();
            }
        });
        sharemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhichShareType = "gmail";
                shareMore();
            }
        });

        sharmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WhichShareType = "All";
                shareMore();
            }
        });

        if (global.isNetworkAvailable()) {
            new LoadReferDetail().execute();
        } else {
            retryInternet("refer");
        }
    }

    public void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView toolbartitle = toolbar.findViewById(R.id.tvTitle);
        toolbartitle.setText("Refer & Earn");
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
        imgReferfriend = findViewById(R.id.imgReferfriend);
        tvReferAmount = findViewById(R.id.tvReferAmount);
        tvReferlink = findViewById(R.id.tvReferlink);
        sharewp = findViewById(R.id.sharewp);
        sharefb = findViewById(R.id.sharefb);
        sharemail = findViewById(R.id.sharemail);
        sharmore = findViewById(R.id.sharmore);
        relativeMain = findViewById(R.id.relativeMain);
    }

    public void shareMore() {
        String ImgListPath = strimage;
        DownloadSelectedIMF d = new DownloadSelectedIMF();
        d.execute(ImgListPath);
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadReferDetail extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Share_Msg;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", strUserId);
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String register = restClient.getResponse();
                Log.e("API : ", register);

                if (register != null && register.length() != 0) {
                    jsonObjectList = new JSONObject(register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONObject jsonObject = jsonObjectList.getJSONObject("share_data");
                            {
                                strimage = jsonObject.getString("image");
                                strmessage = jsonObject.getString("share_msg");
                                strshare_image = jsonObject.getString("share_image");
                                strref_key = jsonObject.getString("refer_code");
                                stryou_get = jsonObject.getString("title");
                                stryou_friend_get = jsonObject.getString("link");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                Glide.with(context)
                        .load(strimage)
                        .into(imgReferfriend);
                tvReferAmount.setText(stryou_get);
                // tvReferlines.setText(stryou_friend_get);
                tvReferlink.setText(strref_key);
            } else {
                Utils.showToast(context, resMessage);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadSelectedIMF extends AsyncTask<String, String, Void> {

        String ImgPath = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        protected Void doInBackground(String... arg0) {
            String filename = "SpiceChanda.jpg";
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/SpiceChanda");
            wallpaperDirectory.mkdirs();
            ImgPath = wallpaperDirectory.getPath() + "/" + filename;

            String myu_recivedimage = arg0[0];
            int count;
            try {
                URL myurl = new URL(myu_recivedimage);
                URLConnection conection = myurl.openConnection();

                int lenghtOfFile = conection.getContentLength();
                conection.connect();
                InputStream inpit = new BufferedInputStream(myurl.openStream());
                OutputStream output = new FileOutputStream(ImgPath);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = inpit.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                inpit.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dismissProgressDialog();
            try {

                if (WhichShareType.equalsIgnoreCase("WhatsApp")) {
                    String shareBody = strmessage;
                    File file = new File(ImgPath);
                    Uri imageUri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setPackage("com.whatsapp");
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "SpiceChanda");
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Share on.."));

                } else if (WhichShareType.equalsIgnoreCase("gmail")) {
                    try {
                        String shareBody = strmessage;
                        File file = new File(ImgPath);
                        Uri imageUri = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "your_email"));
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Service On Wheel");
                        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        startActivity(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } else if (WhichShareType.equalsIgnoreCase("facebook")) {
                    try {
                        String shareBody = strmessage;
                        File file = new File(ImgPath);
                        Uri imageUri = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "SpiceChanda");
                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(intent, "Share on.."));


                    } catch (Exception ex) {
                        //Log.e("fb", "msg:-" + ex.getMessage());
                    }

                } else {
                    String shareBody = strmessage;
                    File file = new File(ImgPath);
                    Uri imageUri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "SpiceChanda");
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, "Share on.."));
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                if (extraValue.equalsIgnoreCase("refer")) {
                    new LoadReferDetail().execute();
                }
            }
        }
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
        finish();
    }
}
