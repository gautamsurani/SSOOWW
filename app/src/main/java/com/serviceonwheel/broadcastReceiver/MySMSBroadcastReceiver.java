package com.serviceonwheel.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    private MySMSBroadcastReceiver.OTPListener otpReceiver;

    public final void injectOTPListener(MySMSBroadcastReceiver.OTPListener receiver) {
        this.otpReceiver = receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            assert extras != null;
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            assert status != null;
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    try {
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                        Pattern pattern = Pattern.compile("\\d{4}");
                        Matcher matcher = pattern.matcher(message);

                        if (matcher.find()) {
                            otpReceiver.onOTPReceived(matcher.group(0));
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;
            }
        }
    }

    public interface OTPListener {
        void onOTPReceived(String var1);

        void onOTPTimeOut();
    }
}