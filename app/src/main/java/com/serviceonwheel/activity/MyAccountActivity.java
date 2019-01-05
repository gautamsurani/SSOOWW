package com.serviceonwheel.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.serviceonwheel.R;
import com.serviceonwheel.utils.Utils;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccountActivity extends AppCompatActivity {

    Context context;

    TextView tvUserName, tvMobile;

    LinearLayout liMyProfile, liMyAddress, llMyBooking, llNotification, llContactUs;

    LinearLayout relBasicDetail;

    private CircleImageView ivUserImage;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);
        context = this;
        initComp();
        initToolbar();
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tvTitle);
        ImageView profile = toolbar.findViewById(R.id.imgProfile);
        profile.setVisibility(View.GONE);
        toolbarTitle.setText(getResources().getString(R.string.toolbar_myaccount));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initComp() {
        TextView btnLogout = findViewById(R.id.btnLogout);
        ivUserImage = findViewById(R.id.ivUserImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvMobile = findViewById(R.id.tvMobile);
        liMyProfile = findViewById(R.id.liMyprofile);
        liMyAddress = findViewById(R.id.liMyAddress);
        relBasicDetail = findViewById(R.id.relbasicdetail);
        llMyBooking = findViewById(R.id.llMyBooking);
        llNotification = findViewById(R.id.llNotification);
        llContactUs = findViewById(R.id.llContactUs);

        llContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent codeOpen = new Intent(context, ContactUsActivity.class);
                startActivity(codeOpen);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent codeOpen = new Intent(context, NotificationActivity.class);
                startActivity(codeOpen);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        relBasicDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        liMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        liMyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, AddressActivity.class);
                intent.putExtra("Button", "No");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        llMyBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent codeOpen = new Intent(context, OrderListActivity.class);
                startActivity(codeOpen);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}