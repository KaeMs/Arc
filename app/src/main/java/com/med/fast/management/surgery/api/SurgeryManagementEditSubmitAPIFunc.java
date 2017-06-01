package com.med.fast.management.surgery.api;

import android.content.Context;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.surgery.surgeryinterface.SurgeryManagementEditIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevindreyar on 03-May-17. FM
 */

public class SurgeryManagementEditSubmitAPIFunc extends AsyncTask<SurgeryManagementEditSubmitAPI, Integer, ResponseAPI> {
    private SurgeryManagementEditIntf delegate;
    private Context context;

    public SurgeryManagementEditSubmitAPIFunc(Context context) {
        this.context = context;
    }

    public void setDelegate(SurgeryManagementEditIntf delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ResponseAPI doInBackground(SurgeryManagementEditSubmitAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + "/surgery/surgeryeditsubmit";

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
                    .add("surgery_id", params[0].data.query.surgery_id)
                    .add("user_id", params[0].data.query.user_id)
                    .add("procedure", params[0].data.query.procedure)
                    .add("physician", params[0].data.query.physician)
                    .add("date", params[0].data.query.date)
                    .add("hospital", params[0].data.query.hospital)
                    .add("notes", params[0].data.query.notes)
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
        delegate.onFinishSurgeryManagementEditSubmit(responseAPI);
    }
}
