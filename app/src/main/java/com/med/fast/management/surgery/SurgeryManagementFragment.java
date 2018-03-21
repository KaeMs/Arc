package com.med.fast.management.surgery;

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
import com.med.fast.management.surgery.api.SurgeryManagementListShowAPI;
import com.med.fast.management.surgery.api.SurgeryManagementListShowAPIFunc;
import com.med.fast.management.surgery.surgeryinterface.SurgeryManagementShowIntf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class SurgeryManagementFragment extends FastBaseManagementFragment implements SurgeryManagementShowIntf, StartActivityForResultInAdapterIntf {
    private SurgeryManagementAdapter surgeryManagementAdapter;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = APIConstants.DEFAULT;
    private String currentSort = APIConstants.DEFAULT;
    private String userId;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.surgery_management_caps));

        surgeryManagementAdapter = new SurgeryManagementAdapter(getActivity(), this);
        userId = SharedPreferenceUtilities.getUserId(getActivity());

        recyclerView.setAdapter(surgeryManagementAdapter);

        noContentTV.setText(getString(R.string.no_surgery_record));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView(true);
            }
        });

        refreshView(false);
        /*Parcelable savedRecyclerLayoutState = getArguments().getParcelable(Constants.MANAGER_STATE);
        if(savedRecyclerLayoutState == null){
            refreshView(false);
        } else {
            linearLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
            refreshView(false);
        }*/

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
                    if (lastItemCounter > APIConstants.SURGERY_INF_SCROLL) {
                        SurgeryManagementListShowAPI surgeryManagementListShowAPI = new SurgeryManagementListShowAPI();
                        surgeryManagementListShowAPI.data.query.user_id = userId;
                        surgeryManagementListShowAPI.data.query.keyword = currentKeyword;
                        surgeryManagementListShowAPI.data.query.sort = currentSort;
                        surgeryManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        surgeryManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        SurgeryManagementListShowAPIFunc surgeryManagementListShowAPIFunc = new SurgeryManagementListShowAPIFunc(getActivity());
                        surgeryManagementListShowAPIFunc.setDelegate(SurgeryManagementFragment.this);
                        surgeryManagementListShowAPIFunc.execute(surgeryManagementListShowAPI);
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
        SurgeryManagementListShowAPI surgeryManagementListShowAPI = new SurgeryManagementListShowAPI();
        surgeryManagementListShowAPI.data.query.user_id = userId;
        surgeryManagementListShowAPI.data.query.keyword = currentKeyword;
        surgeryManagementListShowAPI.data.query.sort = currentSort;
        surgeryManagementListShowAPI.data.query.counter = "0";
        surgeryManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        SurgeryManagementListShowAPIFunc surgeryManagementListShowAPIFunc = new SurgeryManagementListShowAPIFunc(getActivity());
        surgeryManagementListShowAPIFunc.setDelegate(SurgeryManagementFragment.this);
        surgeryManagementListShowAPIFunc.execute(surgeryManagementListShowAPI);
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
            SurgeryManagementListShowAPI surgeryManagementListShowAPI = new SurgeryManagementListShowAPI();
            surgeryManagementListShowAPI.data.query.user_id = userId;
            surgeryManagementListShowAPI.data.query.keyword = currentKeyword;
            surgeryManagementListShowAPI.data.query.sort = currentSort;
            surgeryManagementListShowAPI.data.query.counter = String.valueOf(counter);
            surgeryManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

            SurgeryManagementListShowAPIFunc surgeryManagementListShowAPIFunc = new SurgeryManagementListShowAPIFunc(getActivity());
            surgeryManagementListShowAPIFunc.setDelegate(SurgeryManagementFragment.this);
            surgeryManagementListShowAPIFunc.execute(surgeryManagementListShowAPI);
            isLoading = true;
        }
    }

    @Subscribe
    public void onItemAdded(ItemAddedEvent itemAddedEvent) {
        if (surgeryManagementAdapter.getItemCount() > 0) {
            noContentTV.setVisibility(View.GONE);
            scrollToTop();
        } else {
            noContentTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.SURGERY_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                Gson gson = new Gson();
                SurgeryManagementModel model =
                        gson.fromJson(data.getStringExtra(ConstantsManagement.SURGERY_MODEL_EXTRA), SurgeryManagementModel.class);
                surgeryManagementAdapter.updateItem(model);
            }
        }
    }

    @Override
    public void addItem() {
        surgeryManagementAdapter.submitItem();
    }

    @Override
    public void onFinishSurgeryManagementShow(ResponseAPI responseAPI) {
        if (this.isVisible()) {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if (responseAPI.status_code == 200) {
                Gson gson = new Gson();
                SurgeryManagementListShowAPI output = gson.fromJson(responseAPI.status_response, SurgeryManagementListShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    // If refresh, clear adapter and reset the counter
                    surgeryManagementAdapter.setFailLoad(false);
                    if (output.data.query.flag.equals(Constants.FLAG_REFRESH)) {
                        surgeryManagementAdapter.clearList();
                        counter = 0;
                    }
                    surgeryManagementAdapter.addList(output.data.results.surgery_list);
                    lastItemCounter = output.data.results.surgery_list.size();
                    counter += output.data.results.surgery_list.size();

                    if (lastItemCounter > 0 &&
                            lastItemCounter >= APIConstants.SURGERY_INF_SCROLL) {
                        surgeryManagementAdapter.addSingle(null);
                    }
                    if (lastItemCounter == 0 &&
                            surgeryManagementAdapter.getItemCount() == 0) {
                        noContentTV.setVisibility(View.VISIBLE);
                    } else {
                        noContentTV.setVisibility(View.GONE);
                    }
                } else {
                    surgeryManagementAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (responseAPI.status_code == 504) {
                surgeryManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if (responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((FastBaseActivity) getActivity()).forceLogout();
            } else {
                surgeryManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }
}