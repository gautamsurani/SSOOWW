package com.serviceonwheel.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.serviceonwheel.R;

public class SuccessActivity extends AppCompatActivity {

    TextView tvOrder, tvTitle, tvDesc, tvOrderId;

    Context context;

    String title = "", short_desc = "", display_orderID = "", booking_status = "";

    LottieAnimationView lavSuccess, lavFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        context = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title = bundle.getString("title", "");
            short_desc = bundle.getString("short_desc", "");
            display_orderID = bundle.getString("display_orderID", "");
            booking_status = bundle.getString("booking_status", "");
        }

        initComp();

        if (booking_status.equalsIgnoreCase("0")) {
            lavSuccess.setVisibility(View.VISIBLE);
            lavFail.setVisibility(View.GONE);
        } else {
            lavSuccess.setVisibility(View.GONE);
            lavFail.setVisibility(View.VISIBLE);
        }

        tvDesc.setText(short_desc);
        tvTitle.setText(title);
        tvOrderId.setText(display_orderID);

        tvOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initComp() {
        tvOrder = findViewById(R.id.tvOrder);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        tvOrderId = findViewById(R.id.tvOrderId);
        lavSuccess = findViewById(R.id.lavSuccess);
        lavFail = findViewById(R.id.lavFail);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(context, OrderListActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}