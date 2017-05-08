package com.med.fast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kevin Murvie on 4/20/2017. FM
 */

@SuppressLint("ApplySharedPref")
public class SharedPreferenceUtilities {

    public static final String SESSION_SP = "SESSION_SP";
    public static final String USER_ID = "USER_ID";
    public static final String USER_FIRST_NAME = "USER_FIRST_NAME";
    public static final String USER_LAST_NAME = "USER_LAST_NAME";
    public static final String USER_GENDER = "USER_GENDER";
    public static final String USER_DOB = "USER_DOB";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_EMAIL_IS_VERIFIED = "USER_EMAIL_IS_VERIFIED";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    
    private SharedPreferenceUtilities() {
        super();
    }

    private static void save(Context context, String prefsName, String prefsKey, String text) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(prefsKey, text); //3

        editor.commit(); //4
    }

    private static String getValue(Context context, String prefsName, String prefsKey) {
        SharedPreferences settings;
        String text;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        text = settings.getString(prefsKey, null);
        return text;
    }

    public static void clearSharedPreference(Context context, String prefsName) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    private static void removeValue(Context context, String prefsName, String prefsKey) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(prefsKey);
        editor.commit();
    }

    public static String getUserInformation(Context context, String whatToGet) {
        return getValue(context, SESSION_SP,
                whatToGet);
    }

    public static String getUserId(Context context){
        return getUserInformation(context, USER_ID);
    }

    public static void setUserInformation(Context context, String prefId, String whatToSave) {
        // If exist then remove the value
        if (getValue(context, SESSION_SP, prefId) != null) {
            removeValue(context, SESSION_SP, prefId);
            save(context, SESSION_SP, prefId, whatToSave);
        } else {
            save(context, SESSION_SP, prefId, whatToSave);
        }
    }
    public static void setUserInformation(Context context, String sharedPref, String prefId, String whatToSave) {
        // If exist then remove the value
        if (getValue(context, sharedPref, prefId) != null) {
            removeValue(context, sharedPref, prefId);
            save(context, sharedPref, prefId, whatToSave);
        } else {
            save(context, sharedPref, prefId, whatToSave);
        }
    }

    public static void removeUserInformation(Context context, String whatToRemove) {
        // If exist then remove the value
        if (getValue(context, SESSION_SP, whatToRemove) != null) {
            removeValue(context, SESSION_SP, whatToRemove);
        }
    }
    public static void removeUserInformation(Context context, String sharedPref, String whatToRemove) {
        // If exist then remove the value
        if (getValue(context, sharedPref, whatToRemove) != null) {
            removeValue(context, sharedPref, whatToRemove);
        }
    }

    public static void removeSharedPreference(Context context, String whichSPToRemove) {
        clearSharedPreference(context, whichSPToRemove);
    }
}
