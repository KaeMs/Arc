package com.med.fast.management.labresult.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementEditIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevindreyar on 28-Apr-17. FM
 */

public class LabResultManagementEditSubmitAPIFunc extends AsyncTask<LabResultManagementEditSubmitAPI, Integer, ResponseAPI> {
    private LabResultManagementEditIntf delegate;
    private Context context;
    private ProgressDialog progressDialog;

    public LabResultManagementEditSubmitAPIFunc(Context context, LabResultManagementEditIntf delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Submitting your Lab Result..");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected ResponseAPI doInBackground(LabResultManagementEditSubmitAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + APIConstants.LABRESULT_EDIT_SUBMIT;

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

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", params[0].data.query.user_id)
                    .addFormDataPart("test_name", params[0].data.query.test_name)
                    .addFormDataPart("lab_result_id", params[0].data.query.lab_result_id)
                    .addFormDataPart("desc_result", params[0].data.query.desc_result)
                    .addFormDataPart("date", params[0].data.query.date)
                    .addFormDataPart("place", params[0].data.query.place)
                    .addFormDataPart("img_obj_json", params[0].data.query.img_obj_json)
                    .addFormDataPart("added_img_obj_json", params[0].data.query.added_img_obj_json);

            // Upload multiple files
            for(int i = 0; i < params[0].data.query.image_files.size(); ++i){
                String fileType = "image/" + "jpg";
                String fileName = "visit_image_" + i + "_" + params[0].data.query.user_id;
                builder.addFormDataPart("visit_image_file", fileName, RequestBody.create(MediaType.parse(fileType), params[0].data.query.image_files.get(i)));
            }

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(builder.build())
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
        progressDialog.dismiss();
    }
}
