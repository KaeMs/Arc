package com.med.fast.management.medicine.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.accidenthistory.accidentinterface.AccidentHistoryEditIntf;
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

public class MedicineManagementEditShowAPIFunc extends AsyncTask<MedicineManagementEditShowAPI, Integer, ResponseAPI> {
    private MedicineEditIntf delegate;
    private Context context;
    private ProgressDialog progressDialog;

    public MedicineManagementEditShowAPIFunc(Context context) {
        this.context = context;
    }

    public void setDelegate(MedicineEditIntf delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Getting information from server...");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected ResponseAPI doInBackground(MedicineManagementEditShowAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + APIConstants.MEDICINE_EDIT_SHOW;

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
                    .add("medicine_id", params[0].data.query.medicine_id)
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
        delegate.onFinishMedicineEditShow(responseAPI);
    }
}
