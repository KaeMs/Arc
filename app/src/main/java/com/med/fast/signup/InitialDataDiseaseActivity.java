package com.med.fast.signup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.FastBaseActivity;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.Utils;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.allergy.allergyinterface.AllergyManagementShowIntf;
import com.med.fast.management.disease.DiseaseManagementAdapter;
import com.med.fast.management.disease.DiseaseManagementModel;
import com.med.fast.management.disease.api.DiseaseManagementListShowAPI;
import com.med.fast.management.disease.diseaseinterface.DiseaseManagementShowIntf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/11/2017. Fast
 */

public class InitialDataDiseaseActivity extends FastBaseActivity implements DiseaseManagementShowIntf {

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

    private DiseaseManagementAdapter diseaseManagementAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialdata_mainlayout);

        toolbarTitle.setText(getString(R.string.step_2_disease));
        userId = SharedPreferenceUtilities.getUserId(this);

        diseaseManagementAdapter = new DiseaseManagementAdapter(this, true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(diseaseManagementAdapter);

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
                        DiseaseManagementListShowAPI diseaseManagementListShowAPI = new DiseaseManagementListShowAPI();
                        diseaseManagementListShowAPI.data.query.user_id = userId;
                        diseaseManagementListShowAPI.data.query.keyword = currentKeyword;
                        diseaseManagementListShowAPI.data.query.sort = currentSort;
                        diseaseManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        diseaseManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        DiseaseInitListShowAPIFunc diseaseInitListShowAPIFunc = new DiseaseInitListShowAPIFunc(InitialDataDiseaseActivity.this, InitialDataDiseaseActivity.this);
                        diseaseInitListShowAPIFunc.execute(diseaseManagementListShowAPI);
                        isLoading = true;
                    }
                }
            }
        });*/
        
        addBtn.setText(getString(R.string.add_allergy));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diseaseManagementAdapter.submitItem();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialDataDiseaseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialDataDiseaseActivity.this, InitialDataMedicationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void refreshView(boolean setRefreshing){
        DiseaseManagementListShowAPI diseaseManagementListShowAPI = new DiseaseManagementListShowAPI();
        diseaseManagementListShowAPI.data.query.user_id = userId;
        diseaseManagementListShowAPI.data.query.keyword = currentKeyword;
        diseaseManagementListShowAPI.data.query.sort = currentSort;
        diseaseManagementListShowAPI.data.query.counter = "0";
        diseaseManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        DiseaseInitListShowAPIFunc diseaseInitListShowAPIFunc = new DiseaseInitListShowAPIFunc(this, this);
        diseaseInitListShowAPIFunc.execute(diseaseManagementListShowAPI);
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
        DiseaseManagementListShowAPI diseaseManagementListShowAPI = new DiseaseManagementListShowAPI();
        diseaseManagementListShowAPI.data.query.user_id = userId;
        diseaseManagementListShowAPI.data.query.keyword = currentKeyword;
        diseaseManagementListShowAPI.data.query.sort = currentSort;
        diseaseManagementListShowAPI.data.query.counter = String.valueOf(counter);
        diseaseManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

        DiseaseInitListShowAPIFunc diseaseInitListShowAPIFunc = new DiseaseInitListShowAPIFunc(InitialDataDiseaseActivity.this, InitialDataDiseaseActivity.this);
        diseaseInitListShowAPIFunc.execute(diseaseManagementListShowAPI);
        isLoading = true;
    }

    @Override
    public void onFinishDiseaseManagementShow(ResponseAPI responseAPI) {
        swipeRefreshLayout.setRefreshing(false);
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            DiseaseManagementListShowAPI output = gson.fromJson(responseAPI.status_response, DiseaseManagementListShowAPI.class);
            if (output.data.status.code.equals("200")) {
                diseaseManagementAdapter.setFailLoad(false);
                // If refresh, clear adapter and reset the counter
                if (output.data.query.flag.equals(Constants.FLAG_REFRESH)){
                    diseaseManagementAdapter.clearList();
                    counter = 0;
                }
                diseaseManagementAdapter.addList(output.data.results.disease_list);
                lastItemCounter = output.data.results.disease_list.size();
                counter += output.data.results.disease_list.size();

                if (lastItemCounter > 0){
                    diseaseManagementAdapter.addSingle(null);
                }
            } else {
                diseaseManagementAdapter.setFailLoad(true);
                Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            diseaseManagementAdapter.setFailLoad(true);
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            forceLogout();
        } else {
            diseaseManagementAdapter.setFailLoad(true);
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }        
    }
}
