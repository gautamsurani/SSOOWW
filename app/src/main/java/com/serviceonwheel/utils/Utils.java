package com.serviceonwheel.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.serviceonwheel.R;
import com.serviceonwheel.activity.LoginActivity;
import com.serviceonwheel.preference.AppPersistence;
import com.serviceonwheel.preference.AppPreference;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utils {
    private Context _context;

    public Utils(Context context) {
        this._context = context;
    }

    public static boolean isValidEmail(String strEmail) {
        boolean b;
        if (strEmail == null) {
            b = false;
        } else {
            b = Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(strEmail).matches();
        }
        return b;
    }

    public static synchronized boolean isNetworkAvailable(Context activity) {
        boolean flag;

        if (checkNetworkAvailable(activity)) {
            flag = true;

        } else {
            flag = false;
            Log.d("", "No network available!");
        }
        return flag;
    }

    public static String getTodayDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c);
    }

    private static boolean checkNetworkAvailable(Context activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        String pxx;
        if (String.valueOf(px).contains(".")) {
            pxx = String.valueOf(px).substring(0, String.valueOf(px).indexOf("."));
        } else {
            pxx = String.valueOf(px);
        }
        return Integer.parseInt(pxx);
    }

    public static void hideKeyboard(Activity noticeBoard) {
        View view = noticeBoard.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) noticeBoard.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity noticeBoard) {
        View view = noticeBoard.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) noticeBoard.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void ShowSnakBar(String s, View linearLayout, Activity login) {
        Snackbar snackbar = Snackbar.make(linearLayout, s, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView tv = sbView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        sbView.setBackgroundColor(ContextCompat.getColor(login, R.color.colorPrimary));
        snackbar.show();
    }

    public static void showToast(Context context, String msg) {
        if (!msg.isEmpty()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "There was some server or network problem try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static void ShowSnakBar1(String s, View linearLayout, final Activity login) {
        Snackbar snackbar = Snackbar
                .make(linearLayout, s, Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

        snackbar.setActionTextColor(Color.RED);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        sbView.setBackgroundColor(ContextCompat.getColor(login, R.color.colorPrimary));
        snackbar.show();
    }


    public static void displayDialog(String title, String msg, final Context context, final boolean isFinish) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(msg);
        alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isFinish)
                    ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        final AlertDialog dialog = alertDialog.create();
        if (!((Activity) context).isFinishing()) {
            if (!dialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    public static String getUserName(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_NAME);
    }

    public static String getLastOrderRateData(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.ORDER_LAST_RATE_DATE);
    }

    public static String getEmail(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_EMAIL);
    }

    public static String getLocationName(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.Location_Name);
    }

    public static String getLocationId(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.Location_Id);
    }

    public static void callIntent(Activity context, String number) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            context.startActivity(intent);
    }

    public static String getSupportPhone(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.support_phone);
    }

    public static String getUserId(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_ID);
    }

    public static String getSelectedLat(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.SELECTED_LAT);
    }

    public static String getSelectedLng(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.SELECTED_LNG);
    }

    public static String getDOB(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.DOB);
    }

    public static String getWhatsappNumber(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.WhatsappNumber);
    }

    public static String getSelectedAddress(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.SELECTED_ADDRESS);
    }


    private static String getCartCount(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.CartCount);
    }

    private static void setCartCount(Context context, String value) {
        AppPreference.setPreference(context, AppPersistence.keys.CartCount, value);
    }

    public static void cartPlusOne(Context context) {
        String value = Utils.getCartCount(context);
        if (value == null) {
            value = "0";
        } else {
            if (value.equals("")) {
                value = "0";
            }
        }
        Utils.setCartCount(context, String.valueOf(Integer.parseInt(value) + 1));
    }

    public static void updateCartCount(Context context, TextView textView) {
        textView.setText(getCartCount(context));
    }

    public static String getCity(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.CITY);
    }

    public static String getCompany(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.COMPANY);
    }

    public static String getUserImage(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_IMAGE);
    }

    public static String getMobileNo(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_NUMBER);
    }

    public static String getAddress(Context context) {
        return AppPreference.getPreference(context, AppPersistence.keys.USER_ADDRESS);
    }

    public static void Logout(Context context) {
        AppPersistence.start(context).removeAll();
    }

    public static String getIndianRupee(String value) {
        Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
        return format.format(new BigDecimal(value));
    }

    public static void loginDialog(final Context context, String Message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.create();
        AlertDialog alertDialog;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.popup_sign_in_up, null);

        TextView tvSignIn, tvSignUp;

        tvSignIn = alertLayout.findViewById(R.id.tvSignIn);
        tvSignUp = alertLayout.findViewById(R.id.tvSignUp);

        alertDialogBuilder.setView(alertLayout);

        alertDialog = alertDialogBuilder.create();

        final AlertDialog finalAlertDialog = alertDialog;
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalAlertDialog.dismiss();
                Intent codeOpen = new Intent(context, LoginActivity.class);
                context.startActivity(codeOpen);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalAlertDialog.dismiss();
                Intent codeOpen = new Intent(context, LoginActivity.class);
                codeOpen.putExtra("page", "signup");
                context.startActivity(codeOpen);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        alertDialog.show();

    }
}
