package com.med.fast;

import android.text.Html;

import com.med.fast.api.APIConstants;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
        if (input.equals("") ||
                input.equals("-")){
            return "default";
        } else {
            return Html.fromHtml(input).toString();
        }
    }

    public static String processStringFromAPI(String apiString){
        // If null, directly return -
        if (apiString == null) return "-";
        // If not, return - or string if string is empty or not respectively
        return apiString.equals(APIConstants.DEFAULT) ||
                apiString.equals("") ? "-" : apiString;
    }

    public static boolean processBoolFromAPI(String apiString){
        // If null, directly return -
        if (apiString == null) return false;
        // If not, return - or string if string is empty or not respectively
        return apiString.toLowerCase().equals("true") ||
                apiString.toLowerCase().equals("yes");
    }
}
