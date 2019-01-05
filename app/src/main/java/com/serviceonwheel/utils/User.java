package com.serviceonwheel.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.serviceonwheel.base.BaseClass;
import com.serviceonwheel.listner.UserDetail;
import com.serviceonwheel.preference.AppPersistence;
import com.serviceonwheel.preference.AppPreference;

import org.json.JSONArray;
import org.json.JSONObject;


/*
 * Created by Welcome on 15-03-2018.
 */

public class User extends BaseClass {
    private UserDetail userDetail;

    private Activity context;

    private String userId, phone, name, email, image, bdate, whatsapp_no;

    private String resCode = "";

    public void setOnReceiveListener(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public void getUserInfo() {
        new GetUserData().execute();
    }

    @SuppressLint("CommitPrefEdits")
    public User(Activity context) {
        this.context = context;
        userId = Utils.getUserId(context);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetUserData extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_Get_User_Data;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.AddParam("app_type", "Android");
                    restClient.AddParam("userID", userId);
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("Register", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("user_data");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        userId = jsonObjectList.getString("userID");
                                        name = jsonObjectList.getString("name");
                                        phone = jsonObjectList.getString("phone");
                                        email = jsonObjectList.getString("email");
                                        image = jsonObjectList.getString("image");
                                        bdate = jsonObjectList.getString("bdate");
                                        whatsapp_no = jsonObjectList.getString("whatsapp_no");
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
            dismissProgressDialog(context);
            if (resCode.equalsIgnoreCase("0")) {
                AppPreference.setPreference(context, AppPersistence.keys.USER_ID, userId);
                AppPreference.setPreference(context, AppPersistence.keys.USER_NAME, name);
                AppPreference.setPreference(context, AppPersistence.keys.USER_EMAIL, email);
                AppPreference.setPreference(context, AppPersistence.keys.USER_NUMBER, phone);
                AppPreference.setPreference(context, AppPersistence.keys.USER_IMAGE, image);
                AppPreference.setPreference(context, AppPersistence.keys.DOB, bdate);
                AppPreference.setPreference(context, AppPersistence.keys.WhatsappNumber, whatsapp_no);
                userDetail.onReceived();
            }
        }
    }

}