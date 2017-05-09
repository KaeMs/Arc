package com.med.fast;

import android.text.Html;

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
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.dateFormatSpace, Locale.getDefault());
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
}
