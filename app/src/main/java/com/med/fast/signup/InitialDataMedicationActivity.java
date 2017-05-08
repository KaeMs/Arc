package com.med.fast.signup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.FastBaseActivity;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.medicine.MedicineManagementAdapter;
import com.med.fast.management.medicine.MedicineManagementModel;
import com.med.fast.management.medicine.api.MedicineManagementListShowAPI;
import com.med.fast.management.medicine.api.MedicineManagementListShowAPIFunc;
import com.med.fast.management.medicine.medicineinterface.MedicineShowIntf;
import com.med.fast.signup.api.SkipInitialAPI;
import com.med.fast.signup.api.SkipInitialAPIFunc;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/11/2017. Fast
 */

public class InitialDataMedicationActivity extends FastBaseActivity implements MedicineShowIntf, SkipInitialIntf {

    // Toolbar
    @BindView(R.id.toolbartitledivider_title)
    CustomFontTextView toolbarTitle;

    // Step Indicator
    @BindView(R.id.initialdata_step_imgview)
    ImageView step;

    // RecyclerView
    @BindView(R.id.initialdata_swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.initialdata_recycler)
    RecyclerView recyclerView;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = "default";
    private String currentSort = "default";
    private String userId;

    // Btns
    @BindView(R.id.initialdata_add_btn)
    CustomFontButton addBtn;
    @BindView(R.id.initialdata_skip_btn)
    CustomFontTextView skipBtn;
    @BindView(R.id.initialdata_next_btn)
    CustomFontTextView nextBtn;

    private MedicineManagementAdapter medicineManagementAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialdata_mainlayout);

        toolbarTitle.setText(getString(R.string.step_3_medication));
        userId = SharedPreferenceUtilities.getUserId(this);

        medicineManagementAdapter = new MedicineManagementAdapter(this, true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(medicineManagementAdapter);

        refreshView(true);
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                swipeRefreshLayout.setEnabled(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);

                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                int visibleThreshold = 1;

                // When threshold is reached, API call is made to get new items
                // for infinite scroll
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (lastItemCounter > 10) {
                        MedicineManagementListShowAPI medicineManagementListShowAPI = new MedicineManagementListShowAPI();
                        medicineManagementListShowAPI.data.query.user_id = userId;
                        medicineManagementListShowAPI.data.query.keyword = currentKeyword;
                        medicineManagementListShowAPI.data.query.sort = currentSort;
                        medicineManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        medicineManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        *//*AllergyManagementListShowAPIFunc allergyManagementListShowAPIFunc = new AllergyManagementListShowAPIFunc(InitialDataAllergyActivity.this);
                        allergyManagementListShowAPIFunc.setDelegate(InitialDataAllergyActivity.this);
                        allergyManagementListShowAPIFunc.execute(allergyManagementListShowAPI);*//*
                        MedicineInitListShowAPIFunc medicineInitListShowAPIFunc = new MedicineInitListShowAPIFunc(InitialDataMedicationActivity.this, InitialDataMedicationActivity.this);
                        medicineInitListShowAPIFunc.execute(medicineManagementListShowAPI);
                        isLoading = true;
                    }
                }
            }
        });*/
        
        addBtn.setText(getString(R.string.add_medication));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicineManagementAdapter.submitItem();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipInitialAPI skipInitialAPI = new SkipInitialAPI();
                skipInitialAPI.data.query.user_id = userId;

                SkipInitialAPIFunc skipInitialAPIFunc = new SkipInitialAPIFunc(InitialDataMedicationActivity.this, InitialDataMedicationActivity.this);
                skipInitialAPIFunc.execute(skipInitialAPI);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialDataMedicationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void refreshView(boolean setRefreshing){
        MedicineManagementListShowAPI medicineManagementListShowAPI = new MedicineManagementListShowAPI();
        medicineManagementListShowAPI.data.query.user_id = userId;
        medicineManagementListShowAPI.data.query.keyword = currentKeyword;
        medicineManagementListShowAPI.data.query.sort = currentSort;
        medicineManagementListShowAPI.data.query.counter = "0";
        medicineManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        MedicineInitListShowAPIFunc medicineInitListShowAPIFunc = new MedicineInitListShowAPIFunc(InitialDataMedicationActivity.this, InitialDataMedicationActivity.this);
        medicineInitListShowAPIFunc.execute(medicineManagementListShowAPI);
        if (setRefreshing){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
        isLoading = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void handleLoadMoreEvent (LoadMoreEvent loadMoreEvent){
        MedicineManagementListShowAPI medicineManagementListShowAPI = new MedicineManagementListShowAPI();
        medicineManagementListShowAPI.data.query.user_id = userId;
        medicineManagementListShowAPI.data.query.keyword = currentKeyword;
        medicineManagementListShowAPI.data.query.sort = currentSort;
        medicineManagementListShowAPI.data.query.counter = String.valueOf(counter);
        medicineManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

        MedicineInitListShowAPIFunc medicineInitListShowAPIFunc = new MedicineInitListShowAPIFunc(InitialDataMedicationActivity.this, InitialDataMedicationActivity.this);
        medicineInitListShowAPIFunc.execute(medicineManagementListShowAPI);
        isLoading = true;
    }
    
    @Override
    public void onFinishMedicineShow(ResponseAPI responseAPI) {
        swipeRefreshLayout.setRefreshing(false);
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            MedicineManagementListShowAPI output = gson.fromJson(responseAPI.status_response, MedicineManagementListShowAPI.class);
            if (output.data.status.code.equals("200")) {
                medicineManagementAdapter.setFailLoad(false);
                // If refresh, clear adapter and reset the counter
                if (output.data.query.flag.equals(Constants.FLAG_REFRESH)){
                    medicineManagementAdapter.clearList();
                    counter = 0;
                }
                medicineManagementAdapter.addList(output.data.results.medicine_list);
                lastItemCounter = output.data.results.medicine_list.size();
                counter += output.data.results.medicine_list.size();

                if (lastItemCounter > 0){
                    medicineManagementAdapter.addSingle(null);
                }
            } else {
                medicineManagementAdapter.setFailLoad(true);
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            medicineManagementAdapter.setFailLoad(true);
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            forceLogout();
        } else {
            medicineManagementAdapter.setFailLoad(true);
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishSkip(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SkipInitialAPI output = gson.fromJson(responseAPI.status_response, SkipInitialAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent(InitialDataMedicationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            forceLogout();
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }
}
