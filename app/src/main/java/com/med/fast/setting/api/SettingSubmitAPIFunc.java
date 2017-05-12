package com.med.fast.setting.api;

import android.app.Activity;
import android.os.AsyncTask;

import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.api.TokenUtils;
import com.med.fast.setting.SettingAPIIntf;
import com.med.fast.setting.SettingSubmitAPIIntf;
import com.med.fast.signup.RegisterSubmitAPIIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kevindreyar on 01-May-17.
 */

public class SettingSubmitAPIFunc extends AsyncTask<SettingSubmitAPI, Integer, ResponseAPI> {
    private SettingAPIIntf delegate;
    private Activity activity;

    public SettingSubmitAPIFunc(Activity activity) {
        this.activity = activity;
    }

    public void setDelegate(SettingAPIIntf delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ResponseAPI doInBackground(SettingSubmitAPI... params) {

        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + "setting/settingsubmit";

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

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("first_name", params[0].data.query.first_name)
                    .addFormDataPart("user_id", params[0].data.query.user_id)
                    .addFormDataPart("last_name", params[0].data.query.last_name)
                    .addFormDataPart("profil_image_path", params[0].data.query.profil_image_path)
                    .addFormDataPart("date_of_birth", params[0].data.query.date_of_birth)
                    .addFormDataPart("gender", params[0].data.query.gender);

            // Upload photo file
            if (params[0].data.query.profile_image_file != null){
                String fileType = "image/" + "jpg";
                String fileName = "profile_image_" + params[0].data.query.user_id;
                builder.addFormDataPart("profile_image_file", fileName, RequestBody.create(MediaType.parse(fileType), params[0].data.query.profile_image_file));
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
        delegate.onFinishSettingSubmit(responseAPI);
    }
}
