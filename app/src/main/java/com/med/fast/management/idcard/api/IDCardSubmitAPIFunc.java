package com.med.fast.management.idcard.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.management.idcard.intf.IDCardShowSubmitIntf;
import com.med.fast.management.visit.api.VisitManagementCreateSubmitAPI;
import com.med.fast.management.visit.visitinterface.VisitCreateIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kevin Murvie on 5/8/2017. FM
 */

public class IDCardSubmitAPIFunc extends AsyncTask<IDCardSubmitAPI, Integer, ResponseAPI> {
    private IDCardShowSubmitIntf delegate;
    private Context context;
    private ProgressDialog progressDialog;

    public IDCardSubmitAPIFunc(Context context, IDCardShowSubmitIntf delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Submitting your edit..");
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_pink));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected ResponseAPI doInBackground(IDCardSubmitAPI... params) {
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
                    .addFormDataPart("user_id", params[0].data.query.user_id);

            // Upload image
            String fileType = "image/" + "jpg";
            String fileName = "idcard_img_" + params[0].data.query.user_id;
            builder.addFormDataPart("idcard_image_file", fileName, RequestBody.create(MediaType.parse(fileType), params[0].data.query.image));

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
        delegate.onFinishIDCardSubmit(responseAPI);
        progressDialog.dismiss();
    }
}
