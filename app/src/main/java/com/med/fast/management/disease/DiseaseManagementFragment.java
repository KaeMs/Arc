package com.med.fast.management.disease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseManagementFragment;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.ItemAddedEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.management.disease.api.DiseaseManagementListShowAPI;
import com.med.fast.management.disease.api.DiseaseManagementListShowAPIFunc;
import com.med.fast.management.disease.diseaseinterface.DiseaseManagementShowIntf;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class DiseaseManagementFragment extends FastBaseManagementFragment implements DiseaseManagementShowIntf, StartActivityForResultInAdapterIntf {
    private DiseaseManagementAdapter diseaseManagementAdapter;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = APIConstants.DEFAULT;
    private String currentSort = APIConstants.DEFAULT;
    private String userId;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.disease_management_caps));

        diseaseManagementAdapter = new DiseaseManagementAdapter(getActivity(), this, false);
        userId = SharedPreferenceUtilities.getUserId(getActivity());

        recyclerView.setAdapter(diseaseManagementAdapter);

        noContentTV.setText(getString(R.string.no_disease_record));
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
//                if(swipeRefreshLayout != null)
                swipeRefreshLayout.setEnabled(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);

                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                int visibleThreshold = 1;

                // When threshold is reached, API call is made to get new items
                // for infinite scroll
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (lastItemCounter >= APIConstants.DISEASE_INF_SCROLL) {
                        DiseaseManagementListShowAPI diseaseManagementListShowAPI = new DiseaseManagementListShowAPI();
                        diseaseManagementListShowAPI.data.query.user_id = userId;
                        diseaseManagementListShowAPI.data.query.keyword = currentKeyword;
                        diseaseManagementListShowAPI.data.query.sort = currentSort;
                        diseaseManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        diseaseManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        DiseaseManagementListShowAPIFunc diseaseManagementListShowAPIFunc = new DiseaseManagementListShowAPIFunc(getActivity());
                        diseaseManagementListShowAPIFunc.setDelegate(DiseaseManagementFragment.this);
                        diseaseManagementListShowAPIFunc.execute(diseaseManagementListShowAPI);
                        isLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public void scrollToTop() {
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
    public void refreshView(boolean setRefreshing) {
        DiseaseManagementListShowAPI diseaseManagementListShowAPI = new DiseaseManagementListShowAPI();
        diseaseManagementListShowAPI.data.query.user_id = userId;
        diseaseManagementListShowAPI.data.query.keyword = currentKeyword;
        diseaseManagementListShowAPI.data.query.sort = currentSort;
        diseaseManagementListShowAPI.data.query.counter = "0";
        diseaseManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        DiseaseManagementListShowAPIFunc diseaseManagementListShowAPIFunc = new DiseaseManagementListShowAPIFunc(getActivity());
        diseaseManagementListShowAPIFunc.setDelegate(DiseaseManagementFragment.this);
        diseaseManagementListShowAPIFunc.execute(diseaseManagementListShowAPI);
        swipeRefreshLayout.setRefreshing(setRefreshing);
        if (setRefreshing) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        isLoading = true;
    }

    @Subscribe
    public void handleLoadMoreEvent(LoadMoreEvent loadMoreEvent) {
        if (this.isVisible()) {
            DiseaseManagementListShowAPI diseaseManagementListShowAPI = new DiseaseManagementListShowAPI();
            diseaseManagementListShowAPI.data.query.user_id = userId;
            diseaseManagementListShowAPI.data.query.keyword = currentKeyword;
            diseaseManagementListShowAPI.data.query.sort = currentSort;
            diseaseManagementListShowAPI.data.query.counter = String.valueOf(counter);
            diseaseManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

            DiseaseManagementListShowAPIFunc diseaseManagementListShowAPIFunc = new DiseaseManagementListShowAPIFunc(getActivity());
            diseaseManagementListShowAPIFunc.setDelegate(DiseaseManagementFragment.this);
            diseaseManagementListShowAPIFunc.execute(diseaseManagementListShowAPI);
            isLoading = true;
        }
    }

    @Subscribe
    public void onItemAdded(ItemAddedEvent itemAddedEvent) {
        noContentTV.setVisibility(View.GONE);
        scrollToTop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.DISEASE_CREATE) {
            if (resultCode == Activity.RESULT_OK) {
                refreshView(true);
                scrollToTop();
            }
        } else if (requestCode == RequestCodeList.DISEASE_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                Gson gson = new Gson();
                DiseaseManagementModel model =
                        gson.fromJson(data.getStringExtra(ConstantsManagement.DISEASE_MODEL_EXTRA), DiseaseManagementModel.class);
                diseaseManagementAdapter.updateItem(model);
            }
        }
    }

    @Override
    public void addItem() {
        diseaseManagementAdapter.submitItem();
    }

    @Override
    public void onFinishDiseaseManagementShow(ResponseAPI responseAPI) {
        if (this.isVisible()) {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if (responseAPI.status_code == 200) {
                Gson gson = new Gson();
                DiseaseManagementListShowAPI output = gson.fromJson(responseAPI.status_response, DiseaseManagementListShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    // If refresh, clear adapter and reset the counter
                    diseaseManagementAdapter.setFailLoad(false);
                    if (output.data.query.flag.equals(Constants.FLAG_REFRESH)) {
                        diseaseManagementAdapter.clearList();
                        counter = 0;
                    }
                    diseaseManagementAdapter.addList(output.data.results.disease_list);
                    lastItemCounter = output.data.results.disease_list.size();
                    counter += output.data.results.disease_list.size();

                    if (lastItemCounter > 0 &&
                            lastItemCounter >= APIConstants.DISEASE_INF_SCROLL) {
                        diseaseManagementAdapter.addSingle(null);
                    }
                    if (lastItemCounter == 0 &&
                            diseaseManagementAdapter.getItemCount() == 0) {
                        noContentTV.setVisibility(View.VISIBLE);
                    } else {
                        noContentTV.setVisibility(View.GONE);
                    }
                } else {
                    diseaseManagementAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (responseAPI.status_code == 504) {
                diseaseManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if (responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((FastBaseActivity) getActivity()).forceLogout();
            } else {
                diseaseManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }
}
