package com.med.fast.management.accidenthistory.api;

import android.content.Context;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.accidenthistory.accidentinterface.AccidentHistoryCreateDeleteIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kevin Murvie on 4/20/2017. FM
 */

public class AccidentHistoryCreateSubmitAPIFunc extends AsyncTask<AccidentHistoryCreateSubmitAPI, Integer, ResponseAPI> {
    private AccidentHistoryCreateDeleteIntf delegate;
    private Context context;
    private String tag;

    public AccidentHistoryCreateSubmitAPIFunc(Context context, AccidentHistoryCreateDeleteIntf delegate, String tag) {
        this.context = context;
        this.delegate = delegate;
        this.tag = tag;
    }

    @Override
    protected ResponseAPI doInBackground(AccidentHistoryCreateSubmitAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + APIConstants.ACCIDENT_CREATE_SUBMIT;

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
                    .add("detail", params[0].data.query.detail)
                    .add("injury_nature", params[0].data.query.injury_nature)
                    .add("injury_location", params[0].data.query.injury_location)
                    .add("injury_date", params[0].data.query.injury_date)
                    .add("injury_date_tmp", params[0].data.query.injury_date_tmp)
                    .add("injury_date_custom", params[0].data.query.injury_date_custom)
                    .add("tag", params[0].data.query.tag)
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
        delegate.onFinishAccidentHistoryCreateSubmit(responseAPI, tag);
    }

}