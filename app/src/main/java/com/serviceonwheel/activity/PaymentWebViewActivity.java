package com.serviceonwheel.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.serviceonwheel.R;

public class PaymentWebViewActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ProgressBar PBar;
    TextView txt_view;
    private WebView webView;
    private String status = "";
    private String url = "";
    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

        context = this;

        if (getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString("url");
        }

        txt_view = findViewById(R.id.tV1);
        PBar = findViewById(R.id.pB1);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait..");

        webView = findViewById(R.id.webView1);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && PBar.getVisibility() == ProgressBar.GONE) {
                    PBar.setVisibility(ProgressBar.VISIBLE);
                    txt_view.setVisibility(View.VISIBLE);

                }
                PBar.setProgress(progress);
                if (progress == 100) {

                    PBar.setVisibility(ProgressBar.GONE);
                    txt_view.setVisibility(View.GONE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onLoadResource(WebView view, String url) {
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (url.contains("status=success")) {
                        Uri uri = Uri.parse(url);
                        String title = uri.getQueryParameter("title");
                        String short_desc = uri.getQueryParameter("short_desc");
                        Bundle b = new Bundle();
                        status = "captured";
                        b.putString("status", "success");
                        b.putString("title", title);
                        b.putString("short_desc", short_desc);
                        Intent i = getIntent();
                        i.putExtras(b);
                        setResult(Activity.RESULT_OK, i);
                        finish();
                        webView.clearCache(true);
                    } else if (url.equals("https://www.serviceonwheel.com")) {
                        Bundle b = new Bundle();
                        status = "failed";
                        b.putString("title", "Fail");
                        b.putString("short_desc", "Payment Fail!");
                        b.putString("status", "failed");
                        Intent i = getIntent();
                        i.putExtras(b);
                        setResult(Activity.RESULT_OK, i);
                        finish();
                        webView.clearCache(true);
                    } else if (url.contains("status=fail")) {
                        Uri uri = Uri.parse(url);
                        String title = uri.getQueryParameter("title");
                        String short_desc = uri.getQueryParameter("short_desc");
                        Bundle b = new Bundle();
                        status = "failed";
                        b.putString("status", "failed");
                        b.putString("title", title);
                        b.putString("short_desc", short_desc);
                        Intent i = getIntent();
                        i.putExtras(b);
                        setResult(Activity.RESULT_OK, i);
                        finish();
                        webView.clearCache(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        webView.loadUrl(url);

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack() && status.equals("")) {
            webView.goBack();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Cancel Transaction");
            builder.setMessage("Pressing back would cancel your current transaction. Proceed to cancel?");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    webView.clearCache(true);
                    Bundle b = new Bundle();
                    status = "failed";
                    b.putString("title", "Fail");
                    b.putString("short_desc", "Payment Fail!");
                    b.putString("status", "failed");
                    Intent i = getIntent();
                    i.putExtras(b);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        }
    }
}
