package com.med.fast.management.medicine.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.medicine.medicineinterface.MedicineEditIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevindreyar on 02-May-17. FM
 */

public class MedicineManagementEditSubmitAPIFunc extends AsyncTask<MedicineManagementEditSubmitAPI, Integer, ResponseAPI> {

    private MedicineEditIntf delegate;
    private Context context;
    private ProgressDialog progressDialog;

    public MedicineManagementEditSubmitAPIFunc(Context context) {
        this.context = context;
    }

    public void setDelegate(MedicineEditIntf delegate) {
        this.delegate = delegate;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ResponseAPI doInBackground(MedicineManagementEditSubmitAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + APIConstants.MEDICINE_EDIT_SUBMIT;

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
                    .add("medicine_id", params[0].data.query.medicine_id)
                    .add("user_id", params[0].data.query.user_id)
                    .add("name", params[0].data.query.name)
                    .add("route", params[0].data.query.route)
                    .add("dose", params[0].data.query.dose)
                    .add("form", params[0].data.query.form)
                    .add("frequency", params[0].data.query.frequency)
                    .add("reason", params[0].data.query.reason)
                    .add("status", params[0].data.query.status)
                    .add("additional_instruction", params[0].data.query.additional_instruction)
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
        delegate.onFinishMedicineEditSubmit(responseAPI);
    }
}
