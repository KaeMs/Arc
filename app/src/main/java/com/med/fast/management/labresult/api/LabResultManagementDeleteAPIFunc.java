package com.med.fast.management.labresult.api;

import android.content.Context;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementDeleteIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevindreyar on 28-Apr-17. FM
 */

public class LabResultManagementDeleteAPIFunc extends AsyncTask<LabResultManagementDeleteSubmitAPI, Integer, ResponseAPI> {
    private LabResultManagementDeleteIntf delegate;
    private Context context;
    private String tag;

    public LabResultManagementDeleteAPIFunc(Context context, LabResultManagementDeleteIntf intf, String tag) {
        this.context = context;
        this.delegate = intf;
        this.tag = tag;
    }

    @Override
    protected void onPostExecute(ResponseAPI responseAPI) {
        super.onPostExecute(responseAPI);
        delegate.onFinishLabResultManagementDelete(responseAPI, tag);
    }

    @Override
    protected ResponseAPI doInBackground(LabResultManagementDeleteSubmitAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + APIConstants.LABRESULT_DELETE_SUBMIT;

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
                    .add("user_id", params[0].data.query.user_id)
                    .add("lab_result_id", params[0].data.query.lab_result_id)
                    .add("img_path", params[0].data.query.img_path)
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
}
