package com.serviceonwheel.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.serviceonwheel.R;

import java.util.Map;

public class AppPersistence {
    public enum keys {
        USER_NAME, USER_ID, USER_NUMBER, USER_EMAIL, USER_ADDRESS, CITY, USER_IMAGE, DOB,
        MRG_Anniversary, COMPANY, CartCount, SELECTED_LAT, SELECTED_LNG, SELECTED_ADDRESS,
        support_phone, WhatsappNumber, Location_Name, Location_Id, ORDER_LAST_RATE_DATE
    }

    private static AppPersistence mAppPersistance;
    private SharedPreferences sharedPreferences;

    public static AppPersistence start(Context context) {
        if (mAppPersistance == null) {
            mAppPersistance = new AppPersistence(context);
        }
        return mAppPersistance;
    }

    private AppPersistence(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.prefrence_file_name),
                Context.MODE_PRIVATE);
    }

    public Object get(Enum key) {
        Map<String, ?> all = sharedPreferences.getAll();
        return all.get(key.toString());
    }

    void save(Enum key, Object val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (val instanceof Integer) {
            editor.putInt(key.toString(), (Integer) val);
        } else if (val instanceof String) {
            editor.putString(key.toString(), String.valueOf(val));
        } else if (val instanceof Float) {
            editor.putFloat(key.toString(), (Float) val);
        } else if (val instanceof Long) {
            editor.putLong(key.toString(), (Long) val);
        } else if (val instanceof Boolean) {
            editor.putBoolean(key.toString(), (Boolean) val);
        }
        editor.apply();
    }

    void remove(Enum key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key.toString());
        editor.apply();
    }

    public void removeAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
