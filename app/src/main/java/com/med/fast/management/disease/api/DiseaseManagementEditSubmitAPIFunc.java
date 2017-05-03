package com.med.fast.management.disease.api;

import android.app.Activity;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.disease.diseaseinterface.DiseaseManagementEditIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevindreyar on 28-Apr-17. FM
 */

public class DiseaseManagementEditSubmitAPIFunc extends AsyncTask<DiseaseManagementEditSubmitAPI, Integer, ResponseAPI> {
    private DiseaseManagementEditIntf delegate;
    private Activity activity;

    public DiseaseManagementEditSubmitAPIFunc(Activity activity) {
        this.activity = activity;
    }

    public void setDelegate(DiseaseManagementEditIntf delegate) {
        this.delegate = delegate;
    }
    @Override
    protected ResponseAPI doInBackground(DiseaseManagementEditSubmitAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + APIConstants.DISEASE_EDIT_SUBMIT;

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
                    .add("disease_id", params[0].data.query.disease_id)
                    .add("disease_name", params[0].data.query.disease_name)
                    .add("user_id", params[0].data.query.user_id)
                    .add("is_hereditary", params[0].data.query.is_hereditary)
                    .add("is_ongoing", params[0].data.query.is_ongoing)
                    .add("history_date_text", params[0].data.query.history_date_text)
                    .add("date", params[0].data.query.date)
                    .add("hereditary_carrier", params[0].data.query.hereditary_carrier)
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
        delegate.onFinishDiseaseManagementEditSubmit(responseAPI);
    }
}
