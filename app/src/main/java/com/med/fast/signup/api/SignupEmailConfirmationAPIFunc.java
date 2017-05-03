package com.med.fast.signup.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.med.fast.R;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.signup.EmailConfirmationIntf;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kevin Murvie on 4/20/2017. FM
 */

public class SignupEmailConfirmationAPIFunc extends AsyncTask<SignupEmailConfirmationAPI, Integer, ResponseAPI> {
    private EmailConfirmationIntf delegate;
    private Context context;
    private ProgressDialog progressDialog;

    public SignupEmailConfirmationAPIFunc(Context context, EmailConfirmationIntf intf) {
        this.context = context;
        this.delegate = intf;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Confirming your email.");
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_pink));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected ResponseAPI doInBackground(SignupEmailConfirmationAPI... params) {
        ResponseAPI responseAPI = new ResponseAPI();
        try {
            String url = APIConstants.API_URL + APIConstants.REGISTER_EMAIL_VERIFY;

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(APIConstants.connectTimeout, TimeUnit.SECONDS)
                    .writeTimeout(APIConstants.writeTimeout, TimeUnit.SECONDS)
                    .readTimeout(APIConstants.readTimeout, TimeUnit.SECONDS)
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("token", params[0].data.query.token)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
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
    protected void onPostExecute(ResponseAPI registerSubmitAPIResult) {
        super.onPostExecute(registerSubmitAPIResult);
        delegate.onFinishConfirmEmail(registerSubmitAPIResult);
        progressDialog.dismiss();
    }
}