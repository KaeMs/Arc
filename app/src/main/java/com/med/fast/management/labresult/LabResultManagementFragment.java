package com.med.fast.management.labresult;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.management.labresult.api.LabResultManagementListShowAPI;
import com.med.fast.management.labresult.api.LabResultManagementListShowAPIFunc;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementShowIntf;

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
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class LabResultManagementFragment extends FastBaseFragment implements LabResultManagementShowIntf, StartActivityForResultInAdapterIntf {
    @BindView(R.id.management_mainfragment_search_edittxt)
    CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    ImageView searchBtn;
    @BindView(R.id.management_mainfragment_recycler)
    RecyclerView recyclerView;
    private LabResultManagementAdapter labResultManagementAdapter;
    @BindView(R.id.management_mainfragment_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.management_mainfragment_progress)
    ProgressBar progressBar;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = "";
    private String currentSort = "";
    private String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).changeTitle("LAB RESULT MANAGEMENT");
        setHasOptionsMenu(true);

        userId = SharedPreferenceUtilities.getUserId(getActivity());
        labResultManagementAdapter = new LabResultManagementAdapter(getActivity(), this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        refreshView(false);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        LabResultManagementListShowAPI labResultManagementListShowAPI = new LabResultManagementListShowAPI();
                        labResultManagementListShowAPI.data.query.user_id = userId;
                        labResultManagementListShowAPI.data.query.keyword = currentKeyword;
                        labResultManagementListShowAPI.data.query.sort = currentSort;
                        labResultManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        labResultManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        LabResultManagementListShowAPIFunc labResultManagementListShowAPIFunc = new LabResultManagementListShowAPIFunc(getActivity());
                        labResultManagementListShowAPIFunc.setDelegate(LabResultManagementFragment.this);
                        labResultManagementListShowAPIFunc.execute(labResultManagementListShowAPI);
                        isLoading = true;
                    }
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentKeyword = searchET.getText().toString();
                refreshView(false);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void refreshView(boolean showProgress){
        LabResultManagementListShowAPI labResultManagementListShowAPI = new LabResultManagementListShowAPI();
        labResultManagementListShowAPI.data.query.user_id = userId;
        labResultManagementListShowAPI.data.query.keyword = currentKeyword;
        labResultManagementListShowAPI.data.query.sort = currentSort;
        labResultManagementListShowAPI.data.query.counter = "0";
        labResultManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        LabResultManagementListShowAPIFunc labResultManagementListShowAPIFunc = new LabResultManagementListShowAPIFunc(getActivity());
        labResultManagementListShowAPIFunc.setDelegate(LabResultManagementFragment.this);
        labResultManagementListShowAPIFunc.execute(labResultManagementListShowAPI);
        if (showProgress){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.VISIBLE);
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
        if (this.isVisible()){
            LabResultManagementListShowAPI labResultManagementListShowAPI = new LabResultManagementListShowAPI();
            labResultManagementListShowAPI.data.query.user_id = userId;
            labResultManagementListShowAPI.data.query.keyword = currentKeyword;
            labResultManagementListShowAPI.data.query.sort = currentSort;
            labResultManagementListShowAPI.data.query.counter = String.valueOf(counter);
            labResultManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

            LabResultManagementListShowAPIFunc labResultManagementListShowAPIFunc = new LabResultManagementListShowAPIFunc(getActivity());
            labResultManagementListShowAPIFunc.setDelegate(LabResultManagementFragment.this);
            labResultManagementListShowAPIFunc.execute(labResultManagementListShowAPI);
            isLoading = true;
        }
    }

    @Override
    public void onStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.DISEASE_EDIT){
            if (resultCode == Activity.RESULT_OK){
                Gson gson = new Gson();
                LabResultManagementModel model =
                        gson.fromJson(data.getStringExtra(ConstantsManagement.LABRESULT_MODEL_EXTRA), LabResultManagementModel.class);
                labResultManagementAdapter.updateItem(model);
            }
        }
    }

    @Override
    public void addItem() {
        labResultManagementAdapter.submitItem();
    }

    @Override
    public void onFinishLabResultManagementListShow(ResponseAPI responseAPI) {

    }
}
