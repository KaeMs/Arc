package com.med.fast.signup.api;

import android.app.Activity;
import android.os.AsyncTask;

import com.med.fast.api.APIConstants;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.TokenUtils;
import com.med.fast.api.ResponseAPI;
import com.med.fast.signup.RegisterSubmitAPIIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kevin Murvie on 4/20/2017. FM
 */

public class RegisterSubmitAPIFunc extends AsyncTask<RegisterSubmitAPI, Integer, ResponseAPI> {
    private RegisterSubmitAPIIntf delegate;
    private Activity activity;

    public RegisterSubmitAPIFunc(Activity activity) {
        this.activity = activity;
    }

    public void setDelegate(RegisterSubmitAPIIntf delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ResponseAPI doInBackground(RegisterSubmitAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + "register/registersubmit";

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(APIConstants.connectTimeout, TimeUnit.SECONDS)
                    .writeTimeout(APIConstants.writeTimeout, TimeUnit.SECONDS)
                    .readTimeout(APIConstants.readTimeout, TimeUnit.SECONDS)
                    .build();

            // Get token id
            if (TokenUtils.checkTokenExpiry(activity)) {
                if (!TokenUtils.refresh(activity)) {
                    responseAPI.status_code = 505;
                    responseAPI.status_response = "Error";

                    return responseAPI;
                }
            }
            String token = SharedPreferenceUtilities.getUserInformation(activity, TokenUtils.TOKEN);

            RequestBody formBody = new FormBody.Builder()
                    .add("first_name", params[0].data.query.first_name)
                    .add("last_name", params[0].data.query.last_name)
                    .add("email", params[0].data.query.email)
                    .add("dob", params[0].data.query.dob)
                    .add("password", params[0].data.query.password)
                    .add("gender", params[0].data.query.gender)
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
            responseAPI.status_response = "Koneksi Bermasalah";
        }

        return responseAPI;
    }

    @Override
    protected void onPostExecute(ResponseAPI registerSubmitAPIResult) {
        super.onPostExecute(registerSubmitAPIResult);
        delegate.onFinishRegisterSubmit(registerSubmitAPIResult);
    }

}