package com.serviceonwheel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.serviceonwheel.R;
import com.serviceonwheel.base.BaseActivity;
import com.serviceonwheel.base.NoNetworkActivity;
import com.serviceonwheel.model.WebLink;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;

import static com.serviceonwheel.utils.AppConstant.NO_NETWORK_REQUEST_CODE;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    ListView list;
    ImageView about_us_icon;
    HtmlTextView about_us_content;
    String resMessage = "", resCode = "";
    ImageView facebook_share, twitter_share, google_plus_share, instagram_share;
    RelativeLayout retry;
    String facebook_link = "";
    String google_link = "";
    String twitter_link = "";
    String inst_link = "";
    Activity context;
    List<WebLink> webLinks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        context = this;
        list = findViewById(R.id.aboutus_list);
        about_us_content = findViewById(R.id.aboutus_content);
        facebook_share = findViewById(R.id.about_us_facebookshare);
        twitter_share = findViewById(R.id.about_us_twittershare);
        google_plus_share = findViewById(R.id.about_us_googleplusshare);
        instagram_share = findViewById(R.id.about_us_instagramshare);
        about_us_icon = findViewById(R.id.aboutus_icon);
        retry = findViewById(R.id.retry_layout);
        facebook_share.setOnClickListener(this);
        twitter_share.setOnClickListener(this);
        google_plus_share.setOnClickListener(this);
        instagram_share.setOnClickListener(this);
        initToolbar();
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String version = String.format(getString(R.string.version_code), versionName);
            TextView versionView = findViewById(R.id.aboutus_version);
            versionView.setText(version);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (global.isNetworkAvailable()) {
            new AboutUsDetail().execute();
        } else {
            retryInternet("about");
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
                if (extraValue.equalsIgnoreCase("about")) {
                    new AboutUsDetail().execute();
                }
            }
        }
    }

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
        toolbarTitle.setText(getResources().getString(R.string.toolbar_aboutus));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onClick(View v) {
        if (v == facebook_share) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook_link));
                startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (v == twitter_share) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_link));
                startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (v == google_plus_share) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(google_link));
                startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (v == instagram_share) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(inst_link));
                startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AboutUsDetail extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_About;

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
                        facebook_link = jsonObjectList.getString("facebook_link");
                        google_link = jsonObjectList.getString("google_link");
                        twitter_link = jsonObjectList.getString("twitter_link");
                        inst_link = jsonObjectList.getString("insta_link");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    about_us_content.setHtml(jsonObjectList.getString("about1"));
                                    Glide.with(context)
                                            .load(jsonObjectList.getString("logo"))
                                            .apply(new RequestOptions()
                                                    .fitCenter())
                                            .into(about_us_icon);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("links");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        WebLink webLink = new WebLink();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        webLink.setTitle(jsonObjectList.getString("title"));
                                        webLink.setLink(jsonObjectList.getString("link"));
                                        webLinks.add(webLink);
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

                if (facebook_link.isEmpty()) {
                    facebook_share.setVisibility(View.GONE);
                } else {
                    facebook_share.setVisibility(View.VISIBLE);
                }

                if (google_link.isEmpty()) {
                    google_plus_share.setVisibility(View.GONE);
                } else {
                    google_plus_share.setVisibility(View.VISIBLE);
                }

                if (twitter_link.isEmpty()) {
                    twitter_share.setVisibility(View.GONE);
                } else {
                    twitter_share.setVisibility(View.VISIBLE);
                }

                if (inst_link.isEmpty()) {
                    instagram_share.setVisibility(View.GONE);
                } else {
                    instagram_share.setVisibility(View.VISIBLE);
                }

                CustomAdapter customAdapter = new CustomAdapter(AboutUsActivity.this, webLinks);
                list.setAdapter(customAdapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            String url = webLinks.get(position).getLink();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        } else if (position == 1) {
                            String url = webLinks.get(position).getLink();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        } else if (position == 2) {
                            String url = webLinks.get(position).getLink();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        } else {
                            String url = webLinks.get(position).getLink();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    }
                });
                setListViewHeightBasedOnChildren(list);
            }
        }
    }

    public class CustomAdapter extends ArrayAdapter<WebLink> {
        private final Activity context;
        private final List<WebLink> webLinks;

        CustomAdapter(Activity context, List<WebLink> webLinks) {
            super(context, R.layout.account_list_textview, webLinks);
            this.context = context;
            this.webLinks = webLinks;
        }

        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            @SuppressLint({"ViewHolder", "InflateParams"}) View rowView = inflater.inflate(R.layout.account_list_textview, null, true);
            TextView txtTitle = rowView.findViewById(R.id.myprofiletext);
            ImageView imageView = rowView.findViewById(R.id.iconimg);
            imageView.setVisibility(View.GONE);
            txtTitle.setText(webLinks.get(position).getTitle());
            return rowView;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
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
