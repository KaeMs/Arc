package com.med.fast.management.visit.api;

import android.app.Activity;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.labresult.api.LabResultManagementEditSubmitAPI;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementEditIntf;
import com.med.fast.management.visit.visitinterface.VisitEditIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevindreyar on 28-Apr-17. FM
 */

public class VisitManagementEditSubmitAPIFunc extends AsyncTask<VisitManagementEditSubmitAPI, Integer, ResponseAPI> {
    private VisitEditIntf delegate;
    private Activity activity;

    public VisitManagementEditSubmitAPIFunc(Activity activity, VisitEditIntf delegate) {
        this.activity = activity;
        this.delegate = delegate;
    }

    @Override
    protected ResponseAPI doInBackground(VisitManagementEditSubmitAPI... params) {
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
                    .add("visit_id", params[0].data.query.visit_id)
                    .add("doctor", params[0].data.query.doctor)
                    .add("hospital", params[0].data.query.hospital)
                    .add("diagnose", params[0].data.query.diagnose)
                    .add("disease_id_list", params[0].data.query.disease_id_list)
                    .add("is_image_uploaded", params[0].data.query.is_image_uploaded)
                    .add("image_json_str", params[0].data.query.image_json_str)
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
        delegate.onFinishVisitEditSubmit(responseAPI);
    }
}
