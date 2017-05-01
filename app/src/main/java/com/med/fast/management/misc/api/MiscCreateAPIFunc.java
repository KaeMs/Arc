package com.med.fast.management.misc.api;

import android.content.Context;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.misc.miscinterface.MiscShowCreateIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public class MiscCreateAPIFunc extends AsyncTask<MiscCreateAPI, Integer, ResponseAPI> {
    private MiscShowCreateIntf delegate;
    private Context context;

    public MiscCreateAPIFunc(Context context, MiscShowCreateIntf delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected ResponseAPI doInBackground(MiscCreateAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + APIConstants.MISC_SUBMIT;

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
            String token = SharedPreferenceUtilities.getUserInformation(context, TokenUtils.TOKEN);

            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", params[0].data.query.user_id)
                    .add("voluptuary_habits", params[0].data.query.voluptuary_habits)
                    .add("pregnancy", params[0].data.query.pregnancy)
                    .add("pregnancy_weeks", params[0].data.query.pregnancy_weeks)
                    .add("had_miscarriage", params[0].data.query.had_miscarriage)
                    .add("last_time_miscarriage", params[0].data.query.last_time_miscarriage)
                    .add("cycle_alteration", params[0].data.query.cycle_alteration)
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
    protected void onPostExecute(ResponseAPI responseAPI) {
        super.onPostExecute(responseAPI);
        delegate.onFinishMiscCreateSubmit(responseAPI);
    }
}
