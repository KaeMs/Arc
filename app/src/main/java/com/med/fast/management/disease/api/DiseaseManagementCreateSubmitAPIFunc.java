package com.med.fast.management.disease.api;

import android.content.Context;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.disease.diseaseinterface.DiseaseManagementCreateIntf;
import com.med.fast.management.disease.diseaseinterface.DiseaseManagementDeleteIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevindreyar on 28-Apr-17. FM
 */

public class DiseaseManagementCreateSubmitAPIFunc extends AsyncTask<DiseaseManagementCreateSubmitAPI, Integer, ResponseAPI> {
    private DiseaseManagementCreateIntf delegate;
    private Context context;
    private String tag;
    private boolean initial = false;

    public DiseaseManagementCreateSubmitAPIFunc(Context context, String tag, boolean initial) {
        this.context = context;
        this.tag = tag;
        this.initial = initial;
    }

    public void setDelegate(DiseaseManagementCreateIntf delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ResponseAPI doInBackground(DiseaseManagementCreateSubmitAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url;
            if (initial)url = APIConstants.API_URL + APIConstants.DISEASE_INIT_CREATE_SUBMIT;
            else url = APIConstants.API_URL + APIConstants.DISEASE_CREATE_SUBMIT;

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
                    .add("name", params[0].data.query.name)
                    .add("user_id", params[0].data.query.user_id)
                    .add("is_hereditary", params[0].data.query.is_hereditary)
                    .add("is_ongoing", params[0].data.query.is_ongoing)
                    .add("historic_date", params[0].data.query.historic_date)
                    .add("approximate_date", params[0].data.query.approximate_date)
                    .add("hereditary_carriers", params[0].data.query.hereditary_carriers)
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
        delegate.onFinishDiseaseManagementCreate(responseAPI, tag);
    }
}
