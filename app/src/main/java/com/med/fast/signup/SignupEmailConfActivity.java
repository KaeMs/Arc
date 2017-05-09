package com.med.fast.signup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.FastBaseActivity;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.ResponseAPI;
import com.med.fast.signup.api.SignupEmailConfirmationAPI;
import com.med.fast.signup.api.SignupEmailConfirmationAPIFunc;

/**
 * Created by Kevin Murvie on 5/3/2017. FM
 */

public class SignupEmailConfActivity extends FastBaseActivity implements EmailConfirmationIntf {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri confirmationLink = intent.getData();
        String userToken = confirmationLink.getQueryParameter("Token");

        //Log.d("token", userToken);
        //Log.d("email", userEmail);
        //Log.d("phoneCode", userEmailCode);
        SignupEmailConfirmationAPI signupEmailConfirmationAPI = new SignupEmailConfirmationAPI();
        signupEmailConfirmationAPI.data.query.token = userToken;

        SignupEmailConfirmationAPIFunc signupEmailConfirmationAPIFunc = new SignupEmailConfirmationAPIFunc(this, this);
        signupEmailConfirmationAPIFunc.execute(signupEmailConfirmationAPI);
    }

    @Override
    public void onFinishConfirmEmail(ResponseAPI responseAPI) {

        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SignupEmailConfirmationAPI output = gson.fromJson(responseAPI.status_response,
                    SignupEmailConfirmationAPI.class);
            switch (output.data.results.result_status) {
                case "200":
                    SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.USER_ID, output.data.results.user_id);
                    Intent intent = new Intent(this, InitialDataAllergyActivity.class);
                    startActivity(intent);
                    break;
                case "314":
                    break;
            }
        }
    }
}
