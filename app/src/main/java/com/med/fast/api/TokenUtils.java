package com.med.fast.api;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.med.fast.SharedPreferenceUtilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kevin Murvie on 4/20/2017. FM
 */

public class TokenUtils {

    public static String TOKEN = "TOKEN";
    public static String TOKEN_EXPIRY = "TOKEN_EXPIRY";

    public static Boolean checkTokenExpiry(Context context) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();

        //Get Data From Current Time & Expired Time
        String currentDate = format.format(calendar.getTime());
        String token = SharedPreferenceUtilities.getUserInformation(context, TOKEN);
        String expiredDate = SharedPreferenceUtilities.getUserInformation(context, TOKEN_EXPIRY);

        if (token == null || expiredDate == null) {
            return true;
        }

        try {
            Date dateCurrentFormat = format.parse(currentDate);
            Date dateExpiredFormat = format.parse(expiredDate);

            if (dateCurrentFormat.after(dateExpiredFormat) || dateCurrentFormat.equals(dateExpiredFormat)) {
                return true;
            }

        } catch (ParseException e) {
            return true;
        }

        return false;
    }

    private static int counter = 0;

    public static boolean refresh(Context context){
        try
        {
            ResponseAPI responseAPI = new ResponseAPI();

            String url = APIConstants.API_URL + "token";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(APIConstants.connectTimeout, TimeUnit.SECONDS)
                    .writeTimeout(APIConstants.writeTimeout, TimeUnit.SECONDS)
                    .readTimeout(APIConstants.readTimeout, TimeUnit.SECONDS)
                    .build();

            // Get data from sharepref
            String username = SharedPreferenceUtilities.getUserInformation(context, SharedPreferenceUtilities.USER_NAME);
            String password = SharedPreferenceUtilities.getUserInformation(context, SharedPreferenceUtilities.USER_PASSWORD);

            RequestBody formBody = new FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .add("grant_type", "password")
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.code() == 200){
                String tmp = response.body().string();
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(tmp).getAsJsonObject();

                // get new token
                String accessToken = obj.get("access_token").toString();
                SharedPreferenceUtilities.setUserInformation(context, TOKEN, accessToken.replace("\"", ""));

                // Get new expired date
                int secExpired = obj.get("expires_in").getAsInt();
                int days = secExpired / (60 * 60 * 24);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, (days - 1));
                String expiredDate = sdf.format(calendar.getTime());

                SharedPreferenceUtilities.setUserInformation(context, TOKEN_EXPIRY, expiredDate);
            } else {
                responseAPI.status_code = response.code();
                responseAPI.status_response = response.message();
            }
            response.body().close();
        } catch(Exception ex) {
            if(counter == 3){
                counter = 0;
                return false;
            } else {
                counter++;
                refresh(context);
            }
        }
        return true;
    }
}
