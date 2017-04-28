package com.med.fast.management.labresult.api;

import android.app.Activity;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementEditSubmitIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevindreyar on 28-Apr-17.
 */

public class LabResultManagementEditSubmitAPIFunc extends AsyncTask<LabResultManagementEditSubmitAPI, Integer, ResponseAPI> {
    private LabResultManagementEditSubmitIntf delegate;
    private Activity activity;

    public LabResultManagementEditSubmitAPIFunc(Activity activity) {
        this.activity = activity;
    }

    public void setDelegate(LabResultManagementEditSubmitIntf delegate) {
        this.delegate = delegate;
    }
    @Override
    protected ResponseAPI doInBackground(LabResultManagementEditSubmitAPI... params) {
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
                    .add("user_id", params[0].data.query.user_id)
                    .add("test_name", params[0].data.query.test_name)
                    .add("lab_result_id", params[0].data.query.lab_result_id)
                    .add("desc_result", params[0].data.query.desc_result)
                    .add("img_path", params[0].data.query.img_path)
                    .add("date", params[0].data.query.date)
                    .add("place", params[0].data.query.place)
                    .add("list_img_uploadeds", params[0].data.query.list_img_uploadeds)
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
        delegate.onFinishLabResultManagementEditSubmit(responseAPI);
    }
}
