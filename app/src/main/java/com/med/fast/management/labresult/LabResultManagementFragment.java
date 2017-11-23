package com.med.fast.management.labresult;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.med.fast.Constants;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseFragment;
import com.med.fast.FastBaseManagementFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.UriUtils;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.labresult.api.LabResultManagementListShowAPI;
import com.med.fast.management.labresult.api.LabResultManagementListShowAPIFunc;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementShowIntf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class LabResultManagementFragment extends FastBaseManagementFragment implements LabResultManagementShowIntf, StartActivityForResultInAdapterIntf {
    private LabResultManagementAdapter labResultManagementAdapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = APIConstants.DEFAULT;
    private String currentSort = APIConstants.DEFAULT;
    private String userId;
    private boolean returningWithEdit = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.lab_result_management_caps));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        setHasOptionsMenu(true);

        userId = SharedPreferenceUtilities.getUserId(getActivity());
        labResultManagementAdapter = new LabResultManagementAdapter(getActivity(), this);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(labResultManagementAdapter);

        noContentTV.setText(getString(R.string.no_labresult_record));
        progressBar.setVisibility(View.VISIBLE);
        refreshView(false);
        /*Parcelable savedRecyclerLayoutState = getArguments().getParcelable(Constants.MANAGER_STATE);
        if(savedRecyclerLayoutState == null){
            refreshView(false);
        } else {
            linearLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
            refreshView(false);
        }*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView(true);
            }
        });

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
                    if (lastItemCounter >= APIConstants.LAB_INF_SCROLL) {
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

    @Override
    public void onPause() {
        super.onPause();
        getArguments().putParcelable(Constants.MANAGER_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void scrollToTop()
    {
        this.recyclerView.smoothScrollBy(0, -1000);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(0);
            }
        }, Constants.scrollTopTime);
    }

    @Override
    public void refreshView(boolean setRefreshing){
        LabResultManagementListShowAPI labResultManagementListShowAPI = new LabResultManagementListShowAPI();
        labResultManagementListShowAPI.data.query.user_id = userId;
        labResultManagementListShowAPI.data.query.keyword = currentKeyword;
        labResultManagementListShowAPI.data.query.sort = currentSort;
        labResultManagementListShowAPI.data.query.counter = "0";
        labResultManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        LabResultManagementListShowAPIFunc labResultManagementListShowAPIFunc = new LabResultManagementListShowAPIFunc(getActivity());
        labResultManagementListShowAPIFunc.setDelegate(LabResultManagementFragment.this);
        labResultManagementListShowAPIFunc.execute(labResultManagementListShowAPI);
        swipeRefreshLayout.setRefreshing(setRefreshing);
        if (setRefreshing){
            progressBar.setVisibility(View.GONE);
        } else {
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
        if (requestCode == RequestCodeList.LABRESULT_CREATE){
            if (resultCode == Activity.RESULT_OK){
                Gson gson = new Gson();
                LabResultManagementModel model =
                        gson.fromJson(data.getStringExtra(ConstantsManagement.LABRESULT_MODEL_EXTRA), LabResultManagementModel.class);
                labResultManagementAdapter.addSingle(model, 0);
                noContentTV.setVisibility(View.GONE);
            }
        } else if (requestCode == RequestCodeList.LABRESULT_EDIT){
            if (resultCode == Activity.RESULT_OK){
//                Gson gson = new Gson();
                /*Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Uri.class, new UriUtils.UriDeserializer())
                        .create();
                LabResultManagementModel model =
                        gson.fromJson(data.getStringExtra(ConstantsManagement.LABRESULT_MODEL_EXTRA), LabResultManagementModel.class);
                labResultManagementAdapter.updateItem(model);*/
                returningWithEdit = true;
                refreshView(true);
            }
        }
    }

    @Override
    public void addItem() {
        labResultManagementAdapter.submitItem();
    }

    @Override
    public void onFinishLabResultManagementListShow(ResponseAPI responseAPI) {
        if (this.isVisible()){
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if(responseAPI.status_code == 200) {
                Gson gson = new Gson();
                LabResultManagementListShowAPI output = gson.fromJson(responseAPI.status_response, LabResultManagementListShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    // If refresh, clear adapter and reset the counter
                    labResultManagementAdapter.setFailLoad(false);
                    if (output.data.query.flag.equals(Constants.FLAG_REFRESH)){
                        labResultManagementAdapter.clearList();
                        counter = 0;
                    }

                    if (returningWithEdit){
                        Parcelable savedRecyclerLayoutState = getArguments().getParcelable(Constants.MANAGER_STATE);
                        if (savedRecyclerLayoutState != null){
                            linearLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
                        }
                        returningWithEdit = false;
                    }
                    labResultManagementAdapter.addList(output.data.results.lab_result_list);
                    lastItemCounter = output.data.results.lab_result_list.size();
                    counter += output.data.results.lab_result_list.size();

                    if (lastItemCounter > 0 &&
                            lastItemCounter >= APIConstants.LAB_INF_SCROLL){
                        labResultManagementAdapter.addSingle(null);
                    }
                    if (lastItemCounter == 0 &&
                            labResultManagementAdapter.getItemCount() == 0){
                        noContentTV.setVisibility(View.VISIBLE);
                    } else {
                        noContentTV.setVisibility(View.GONE);
                    }
                } else {
                    labResultManagementAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if(responseAPI.status_code == 504) {
                labResultManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if(responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((FastBaseActivity)getActivity()).forceLogout();
            } else {
                labResultManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
