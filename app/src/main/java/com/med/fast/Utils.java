package com.med.fast;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.text.TextUtils;

import com.med.fast.api.APIConstants;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class Utils {
    public static Date formatMMMtoDate(String input){

        SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        Date newDate = null;
        try {
            newDate = (Date) format.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDate;
    }

    public static String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.dateFormatComma, Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    public static String processStringForAPI(String input){
        if (TextUtils.isEmpty(input)) return "";
//        if (input == null) return "default";
//        if (input.equals("-")) return "default";
         else return Html.fromHtml(input).toString();
    }

    public static String processStringFromAPI(String apiString){
        // If null, directly return -
        if (apiString == null) return "-";
        // If not, return - or string if string is empty or not respectively
        return apiString.equals(APIConstants.DEFAULT) ||
                apiString.equals("") ? "-" : apiString;
    }

    public static boolean processBoolStringFromAPI(String apiString){
        // If null, directly return -
        if (apiString == null) return false;
        // If not, return - or string if string is empty or not respectively
        return apiString.toLowerCase().equals("true") ||
                apiString.toLowerCase().equals("yes");
    }

    public static String processBoolFromAPI(boolean apiString){
        // If not, return - or string if string is empty or not respectively
        return apiString ? "Yes" : "No";
    }

    public static String processWebViewURL(String url) {
        String regex = "^(https?|ftp)://.*$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher httpMatch = p.matcher(url);
        if (!httpMatch.matches()) {
            url = "https://" + url;
        }
        return url;
    }

    public static boolean checkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();

            return (info != null && info.isConnected());
        }
        return false;
    }
}
