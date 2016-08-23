package com.widevision.prayergrid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

@SuppressWarnings("deprecation")
public class PreferenceConnector {
    public static final String PREF_NAME = "PRAYERGRID";
    public static final int MODE = Context.MODE_WORLD_WRITEABLE;
    public static final String LOGIN_UserId = "LOGIN_UserId";
    public static final String LOGIN_EMAIL = "LOGIN_EMAIL";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String GENDER = "gender";
    public static final String COUNTRY = "country";
    public static final String STATE = "state";
    public static final String NOTIFY_PRAYS = "notify_prays";
    public static final String NOTIFY_COMMENTS = "notify_comments";
    public static final String PROFILE_PIC = "profile_pic";
    public static final String PASSWORD = "password";
    public static final String CHURCH_NAME = "church_name";
    public static final String CHURCH_Cover = "CHURCH_Cover";
    public static final String IS_LOGIN = "IS_LOGIN";

    //for message notification....
    public static final String MESSAGE_STATE = "message_state";
    public static final String CHATTING_USER_ID = "chating_user_id";


    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).commit();
    }

    public static boolean readBoolean(Context context, String key, boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    public static void writeInteger(Context context, String key, int value) {
        getEditor(context).putInt(key, value).commit();

    }

    public static int readInteger(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();

    }

    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static void writeFloat(Context context, String key, float value) {
        getEditor(context).putFloat(key, value).commit();
    }

    public static float readFloat(Context context, String key, float defValue) {
        return getPreferences(context).getFloat(key, defValue);
    }

    public static void writeLong(Context context, String key, long value) {
        getEditor(context).putLong(key, value).commit();
    }

    public static long readLong(Context context, String key, long defValue) {
        return getPreferences(context).getLong(key, defValue);

    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void remove(Context context, String key) {
        getEditor(context).remove(key);

    }

    public static void clear(Context context) {
        getEditor(context).clear().commit();
        getEditor(context).clear().commit();

    }
}
