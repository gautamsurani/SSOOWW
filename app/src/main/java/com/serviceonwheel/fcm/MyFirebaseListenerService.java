package com.serviceonwheel.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.serviceonwheel.R;
import com.serviceonwheel.model.NotificationList;
import com.serviceonwheel.utils.AppConstant;
import com.serviceonwheel.utils.RequestMethod;
import com.serviceonwheel.utils.RestClient;
import com.serviceonwheel.utils.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public class MyFirebaseListenerService extends FirebaseMessagingService {

    NotificationManager notificationManager;

    public static int count = 0;

    String strUserId = "", strUserPhone = "";

    String version = "", resMessage = "", resCode = "";

    String token = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        strUserId = Utils.getUserId(getApplicationContext());

        strUserPhone = Utils.getMobileNo(getApplicationContext());

        PackageInfo pInfo = null;
        try {
            pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert pInfo != null;
        version = pInfo.versionName;

        Map<String, String> data = remoteMessage.getData();
        ShowAllDashboardModuleNotification(data);
    }

    @Override
    public void onNewToken(String token) {

        strUserId = Utils.getUserId(getApplicationContext());

        strUserPhone = Utils.getMobileNo(getApplicationContext());

        PackageInfo pInfo = null;
        try {
            pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert pInfo != null;
        version = pInfo.versionName;
        if (strUserId != null) {
            sendRegistrationToServer(token);
        }
    }

    private void sendRegistrationToServer(String token) {
        this.token = token;
        new sendFCMToken().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class sendFCMToken extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String strURL = AppConstant.API_Help + "/index.php?view=token_refresh";
            String strURLTrim = strURL.replaceAll(" ", "%20");
            try {
                RestClient restClient = new RestClient(strURLTrim);
                restClient.AddParam("userID", strUserId);
                restClient.AddParam("userPhone", strUserPhone);
                restClient.AddParam("Token", token);
                restClient.AddParam("appV", version);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String VerifyUser = restClient.getResponse();
                if (VerifyUser != null && VerifyUser.length() != 0) {
                    jsonObjectList = new JSONObject(VerifyUser);
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
            super.onPostExecute(s);
        }
    }

    private void ShowAllDashboardModuleNotification(Map<String, String> data) {
        String type = data.get("type");
        String title = data.get("title");
        String message = data.get("message");
        String imgUrl = data.get("imgUrl");
        String offer_ID = data.get("offer_ID");
        String offer_added_on = data.get("offer_added_on");
        String shre_msg = data.get("shre_msg");

        NotificationList notificationList = new NotificationList();
        notificationList.setTitle(title);
        notificationList.setMessage(message);
        notificationList.setImage(imgUrl);
        notificationList.setOffer_ID(offer_ID);
        notificationList.setAdded_on(offer_added_on);
        notificationList.setShre_msg(shre_msg);

        assert type != null;
        if (type.equalsIgnoreCase("Notification")) {
            final Intent offerIntent = new Intent();
            offerIntent.putExtra("notification", notificationList);
            offerIntent.putExtra("from", "fcm");
            if (Utils.getUserId(getApplicationContext()) != null) {
                offerIntent.setClassName(getApplicationContext(), "com.serviceonwheel.activity.NotificationDetailActivity");
            } else {
                offerIntent.setClassName(getApplicationContext(), "com.serviceonwheel.activity.LoginActivity");
            }
            assert imgUrl != null;
            if (!imgUrl.equals("")) {
                ShowCommonNotificationWithImage(imgUrl, offerIntent, title, message);
            } else {
                sendNotificationBlank(offerIntent, title, message);
            }
        }
    }

    String CHANNEL_ID = "my_channel_service_on_wheel";

    private void ShowCommonNotificationWithImage(String img, Intent gotoIntent, String StrTitle, String SirDesc) {
        Bitmap remote_picture;
        try {
            NotificationCompat.BigPictureStyle notStyle = new NotificationCompat.BigPictureStyle();
            notStyle.setSummaryText(SirDesc);
            remote_picture = getBitmapFromURL(img);

            assert remote_picture != null;
            int imageWidth = remote_picture.getWidth();
            int imageHeight = remote_picture.getHeight();

            DisplayMetrics metrics = this.getResources().getDisplayMetrics();

            int newWidth = metrics.widthPixels;
            float scaleFactor = (float) newWidth / (float) imageWidth;
            int newHeight = (int) (imageHeight * scaleFactor);

            remote_picture = Bitmap.createScaledBitmap(remote_picture, newWidth, newHeight, true);
            notStyle.bigPicture(remote_picture);
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.app_name);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            PendingIntent contentIntent;
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            contentIntent = PendingIntent.getActivity(getApplicationContext(),
                    (int) (Math.random() * 100), gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            Notification notification = mBuilder.setSmallIcon(R.drawable.ic_fcm)
                    .setWhen(0)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setAutoCancel(true)
                    .setContentTitle(StrTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(SirDesc))
                    .setContentIntent(contentIntent)
                    .setVibrate(new long[]{100, 250})
                    .setSound(defaultSoundUri)
                    .setChannelId(CHANNEL_ID)
                    .setContentText(SirDesc).setStyle(notStyle).build();
            count++;
            notificationManager.notify(count, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    private void sendNotificationBlank(Intent gotoIntent, String title, String message) {
        int icon = R.drawable.ic_fcm;
        try {
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
            notificationBuilder.setSmallIcon(icon);
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setContentText(message);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.setChannelId(CHANNEL_ID);
            notificationBuilder.setContentIntent(contentIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            count++;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.app_name);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(mChannel);
            }
            assert notificationManager != null;
            notificationManager.notify(count, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}