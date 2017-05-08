package com.med.fast.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.FastBaseActivity;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.allergy.AllergyManagementAdapter;
import com.med.fast.management.allergy.allergyinterface.AllergyManagementShowIntf;
import com.med.fast.management.allergy.api.AllergyManagementListShowAPI;
import com.med.fast.signup.api.SkipInitialAPI;
import com.med.fast.signup.api.SkipInitialAPIFunc;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/11/2017. Fast
 */

public class InitialDataAllergyActivity extends FastBaseActivity implements AllergyManagementShowIntf, SkipInitialIntf {

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

    private AllergyManagementAdapter allergyManagementAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialdata_mainlayout);

        toolbarTitle.setText(getString(R.string.step_1_allergy));
        userId = SharedPreferenceUtilities.getUserId(this);

        allergyManagementAdapter = new AllergyManagementAdapter(this, true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView(true);
            }
        });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(allergyManagementAdapter);

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
                        AllergyManagementListShowAPI allergyManagementListShowAPI = new AllergyManagementListShowAPI();
                        allergyManagementListShowAPI.data.query.user_id = userId;
                        allergyManagementListShowAPI.data.query.keyword = currentKeyword;
                        allergyManagementListShowAPI.data.query.sort = currentSort;
                        allergyManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        allergyManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        *//*AllergyManagementListShowAPIFunc allergyManagementListShowAPIFunc = new AllergyManagementListShowAPIFunc(InitialDataAllergyActivity.this);
                        allergyManagementListShowAPIFunc.setDelegate(InitialDataAllergyActivity.this);
                        allergyManagementListShowAPIFunc.execute(allergyManagementListShowAPI);*//*
                        AllergyInitListShowAPIFunc allergyInitListShowAPIFunc = new AllergyInitListShowAPIFunc(InitialDataAllergyActivity.this, InitialDataAllergyActivity.this);
                        allergyInitListShowAPIFunc.execute(allergyManagementListShowAPI);
                        isLoading = true;
                    }
                }
            }
        });*/

        addBtn.setText(getString(R.string.add_allergy));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allergyManagementAdapter.submitItem();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipInitialAPI skipInitialAPI = new SkipInitialAPI();
                skipInitialAPI.data.query.user_id = userId;

                SkipInitialAPIFunc skipInitialAPIFunc = new SkipInitialAPIFunc(InitialDataAllergyActivity.this, InitialDataAllergyActivity.this);
                skipInitialAPIFunc.execute(skipInitialAPI);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialDataAllergyActivity.this, InitialDataDiseaseActivity.class);
                startActivity(intent);
            }
        });
    }

    public void refreshView(boolean setRefreshing){
        AllergyManagementListShowAPI allergyManagementListShowAPI = new AllergyManagementListShowAPI();
        allergyManagementListShowAPI.data.query.user_id = userId;
        allergyManagementListShowAPI.data.query.keyword = currentKeyword;
        allergyManagementListShowAPI.data.query.sort = currentSort;
        allergyManagementListShowAPI.data.query.counter = "0";
        allergyManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        /*AllergyManagementListShowAPIFunc allergyManagementListShowAPIFunc = new AllergyManagementListShowAPIFunc(this);
        allergyManagementListShowAPIFunc.setDelegate(this);
        allergyManagementListShowAPIFunc.execute(allergyManagementListShowAPI);*/
        AllergyInitListShowAPIFunc allergyInitListShowAPIFunc = new AllergyInitListShowAPIFunc(InitialDataAllergyActivity.this, InitialDataAllergyActivity.this);
        allergyInitListShowAPIFunc.execute(allergyManagementListShowAPI);
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
        AllergyManagementListShowAPI allergyManagementListShowAPI = new AllergyManagementListShowAPI();
        allergyManagementListShowAPI.data.query.user_id = userId;
        allergyManagementListShowAPI.data.query.keyword = currentKeyword;
        allergyManagementListShowAPI.data.query.sort = currentSort;
        allergyManagementListShowAPI.data.query.counter = String.valueOf(counter);
        allergyManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

        /*AllergyManagementListShowAPIFunc allergyManagementListShowAPIFunc = new AllergyManagementListShowAPIFunc(InitialDataAllergyActivity.this);
        allergyManagementListShowAPIFunc.setDelegate(InitialDataAllergyActivity.this);
        allergyManagementListShowAPIFunc.execute(allergyManagementListShowAPI);*/
        AllergyInitListShowAPIFunc allergyInitListShowAPIFunc = new AllergyInitListShowAPIFunc(InitialDataAllergyActivity.this, InitialDataAllergyActivity.this);
        allergyInitListShowAPIFunc.execute(allergyManagementListShowAPI);
        isLoading = true;
    }

    @Override
    public void onFinishAllergyManagementShow(ResponseAPI responseAPI) {
        swipeRefreshLayout.setRefreshing(false);
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AllergyManagementListShowAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementListShowAPI.class);
            if (output.data.status.code.equals("200")) {
                allergyManagementAdapter.setFailLoad(false);
                // If refresh, clear adapter and reset the counter
                if (output.data.query.flag.equals(Constants.FLAG_REFRESH)){
                    allergyManagementAdapter.clearList();
                    counter = 0;
                }
                allergyManagementAdapter.addList(output.data.results.allergy_list);
                lastItemCounter = output.data.results.allergy_list.size();
                counter += output.data.results.allergy_list.size();

                if (lastItemCounter > 0){
                    allergyManagementAdapter.addSingle(null);
                }
            } else {
                allergyManagementAdapter.setFailLoad(true);
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            allergyManagementAdapter.setFailLoad(true);
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            forceLogout();
        } else {
            allergyManagementAdapter.setFailLoad(true);
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishSkip(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SkipInitialAPI output = gson.fromJson(responseAPI.status_response, SkipInitialAPI.class);
            if (output.data.status.code.equals("200")) {
                Intent intent = new Intent(InitialDataAllergyActivity.this, MainActivity.class);
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
