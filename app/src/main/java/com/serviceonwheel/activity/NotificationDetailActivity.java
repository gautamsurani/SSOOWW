package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serviceonwheel.R;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.model.NotificationList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class NotificationDetailActivity extends BaseActivity {

    Activity context;

    NotificationList notificationList;

    public static final String EXTRA_OBJECT = "com.app.sample.chatting";

    CollapsingToolbarLayout collapsingToolbarLayout;

    TextView tvTitle, tvDate, tvDesc;

    ImageView ivImage;

    String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail_);

        context = this;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            notificationList = (NotificationList) getIntent().getSerializableExtra("notification");
            from = extras.getString("from", "");
        }

        initComp();

        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_OBJECT);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar1));
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        tvTitle.setText(notificationList.getTitle());
        tvDate.setText(notificationList.getAdded_on());
        tvDesc.setText(notificationList.getMessage());

        Glide.with(context)
                .load(notificationList.getImage())
                .into(ivImage);

    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar1);
        TextView toolbar_title = toolbar.findViewById(R.id.tvTitle);
        toolbar_title.setText(getResources().getString(R.string.toolbar_myaccount));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComp() {
        tvDate = findViewById(R.id.etaddedOn);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.etOfferDescription);
        ivImage = findViewById(R.id.imgdetailoffer);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            new DownloadSelectedIMF().execute(notificationList.getImage());
        }
        return super.onOptionsItemSelected(item);
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
            String filename = "service_on_wheel.jpg";
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/ServiceOnWheel/");
            wallpaperDirectory.mkdirs();
            ImgPath = wallpaperDirectory.getPath() + filename;
            String mys_receivedImage = arg0[0];
            int count;
            try {
                URL myurl = new URL(mys_receivedImage);
                URLConnection connection = myurl.openConnection();

                int lengthOfFile = connection.getContentLength();
                connection.connect();
                InputStream input = new BufferedInputStream(myurl.openStream());
                OutputStream output = new FileOutputStream(ImgPath);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
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
                File file = new File(ImgPath);
                Uri imageUri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, notificationList.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, notificationList.getShre_msg());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Share via"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (from.equals("fcm")) {
            startActivity(new Intent(context, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
