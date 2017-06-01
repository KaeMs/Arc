package com.med.fast.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;

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
 * Created by Kevin Murvie on 5/02/2017. FM
 */

public class LoginAPIFunc extends AsyncTask<LoginAPI, Integer, ResponseAPI> {
    private LoginIntf delegate;
    private Context context;
    private ProgressDialog progressDialog;

    public LoginAPIFunc(Context context, LoginIntf delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Signing you in");
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_pink));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected ResponseAPI doInBackground(LoginAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {

            // Get token first
            String urlToken = APIConstants.API_URL + APIConstants.TOKEN;

            OkHttpClient clientToken = new OkHttpClient.Builder()
                    .connectTimeout(APIConstants.connectTimeout, TimeUnit.SECONDS)
                    .writeTimeout(APIConstants.writeTimeout, TimeUnit.SECONDS)
                    .readTimeout(APIConstants.readTimeout, TimeUnit.SECONDS)
                    .build();

            RequestBody formBodyToken = new FormBody.Builder()
                    .add("username", params[0].data.query.email)
                    .add("password", params[0].data.query.password)
                    .add("grant_type", "password")
                    .build();

            Request requestToken = new Request.Builder()
                    .url(urlToken)
                    .post(formBodyToken)
                    .build();

            Response responseToken = clientToken.newCall(requestToken).execute();

            if (responseToken.code() == 200) {
                Gson gson = new GsonBuilder().create();
                String tmp = responseToken.body().string();
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(tmp).getAsJsonObject();

                // Get new token
                String accessToken = obj.get("access_token").toString();
                SharedPreferenceUtilities.setUserInformation(context, TokenUtils.TOKEN, accessToken.replace("\"", ""));

                // Get new expired date
                int secExpired = obj.get("expires_in").getAsInt();
                int days = secExpired / (60 * 60 * 24);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, (days - 1));
                String expiredDate = sdf.format(calendar.getTime());

                SharedPreferenceUtilities.setUserInformation(context, TokenUtils.TOKEN_EXPIRY, expiredDate);
            } else {
                responseAPI.status_code = responseToken.code();
                responseAPI.status_response = responseToken.message();

                return responseAPI;
            }

            responseToken.body().close();

            String url = APIConstants.API_URL + APIConstants.LOGIN;

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(APIConstants.connectTimeout, TimeUnit.SECONDS)
                    .writeTimeout(APIConstants.writeTimeout, TimeUnit.SECONDS)
                    .readTimeout(APIConstants.readTimeout, TimeUnit.SECONDS)
                    .build();

            // Get token id
            if (TokenUtils.checkTokenExpiry(context)) {
                if (!TokenUtils.refresh(context)) {
                    responseAPI.status_code = 505;
                    responseAPI.status_response = "Error";

                    return responseAPI;
                }
            }
            String token = SharedPreferenceUtilities.getFromSessionSP(context, TokenUtils.TOKEN);

            RequestBody formBody = new FormBody.Builder()
                    .add("email", params[0].data.query.email)
                    .add("password", params[0].data.query.password)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();

            responseAPI.status_code = response.code();
            if (response.code() == 200) {
                responseAPI.status_response = response.body().string();
            } else {
                responseAPI.status_response = response.message();
            }
            response.body().close();
        } catch (Exception ex) {
            responseAPI.status_code = 504;
            responseAPI.status_response = APIConstants.CONNECTION_ERROR;
        }
        return responseAPI;
    }

    @Override
    protected void onPostExecute(ResponseAPI responseAPI) {
        super.onPostExecute(responseAPI);
        progressDialog.dismiss();
        delegate.onFinishLogin(responseAPI);
    }

}