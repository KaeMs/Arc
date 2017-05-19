package com.med.fast.management.visit.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.visit.visitinterface.VisitCreateIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevindreyar on 28-Apr-17. FM
 */

public class VisitManagementCreateSubmitAPIFunc extends AsyncTask<VisitManagementCreateSubmitAPI, Integer, ResponseAPI> {
    private VisitCreateIntf delegate;
    private Context context;
    private String tag;
    private ProgressDialog progressDialog;

    public VisitManagementCreateSubmitAPIFunc(Context context, VisitCreateIntf delegate, String tag) {
        this.context = context;
        this.delegate = delegate;
        this.tag = tag;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Submitting your visit..");
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_pink));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected ResponseAPI doInBackground(VisitManagementCreateSubmitAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + APIConstants.VISIT_CREATE_SUBMIT;

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

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", params[0].data.query.user_id)
                    .addFormDataPart("doctor", params[0].data.query.doctor)
                    .addFormDataPart("hospital", params[0].data.query.hospital)
                    .addFormDataPart("diagnose", params[0].data.query.diagnose)
                    .addFormDataPart("disease_id_list", params[0].data.query.disease_id_list)
                    .addFormDataPart("is_image_uploaded", params[0].data.query.is_image_uploaded);

            // Upload multiple files
            for(int i = 0; i < params[0].data.query.image_list.size(); ++i){
                String fileType = "image/" + "jpg";
                String fileName = "visit_image_" + i + "_" + params[0].data.query.user_id;
                builder.addFormDataPart("visit_image_file", fileName, RequestBody.create(MediaType.parse(fileType), params[0].data.query.image_list.get(i)));
            }

            RequestBody formBody = builder.build();

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
        delegate.onFinishVisitCreate(responseAPI, tag);
        progressDialog.dismiss();
    }
}
