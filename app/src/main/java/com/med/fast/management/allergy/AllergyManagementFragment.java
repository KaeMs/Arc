package com.med.fast.management.allergy;

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
import com.med.fast.management.allergy.allergyinterface.AllergyManagementShowIntf;
import com.med.fast.management.allergy.api.AllergyManagementListShowAPI;
import com.med.fast.management.allergy.api.AllergyManagementListShowAPIFunc;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Kevin Murvie on 4/23/2017. FM
 */

public class AllergyManagementFragment extends FastBaseManagementFragment implements AllergyManagementShowIntf, StartActivityForResultInAdapterIntf {
    private AllergyManagementAdapter allergyManagementAdapter;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = APIConstants.DEFAULT;
    private String currentSort = APIConstants.DEFAULT;
    private String currentType = APIConstants.DEFAULT;
    private String userId;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.allergy_management_caps));

        userId = SharedPreferenceUtilities.getUserId(getActivity());
        allergyManagementAdapter = new AllergyManagementAdapter(getActivity(), this, false);

        recyclerView.setAdapter(allergyManagementAdapter);

        noContentTV.setText(getString(R.string.no_allergy_record));
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
                    if (lastItemCounter >= APIConstants.ALLERGY_INF_SCROLL) {
                        AllergyManagementListShowAPI allergyManagementListShowAPI = new AllergyManagementListShowAPI();
                        allergyManagementListShowAPI.data.query.user_id = userId;
                        allergyManagementListShowAPI.data.query.keyword = currentKeyword;
                        allergyManagementListShowAPI.data.query.sort = currentSort;
                        allergyManagementListShowAPI.data.query.type = currentType;
                        allergyManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        allergyManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        AllergyManagementListShowAPIFunc allergyManagementListShowAPIFunc = new AllergyManagementListShowAPIFunc(getActivity());
                        allergyManagementListShowAPIFunc.setDelegate(AllergyManagementFragment.this);
                        allergyManagementListShowAPIFunc.execute(allergyManagementListShowAPI);
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
        recyclerView.clearOnScrollListeners();
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
        AllergyManagementListShowAPI allergyManagementListShowAPI = new AllergyManagementListShowAPI();
        allergyManagementListShowAPI.data.query.user_id = userId;
        allergyManagementListShowAPI.data.query.keyword = currentKeyword;
        allergyManagementListShowAPI.data.query.sort = currentSort;
        allergyManagementListShowAPI.data.query.type = currentType;
        allergyManagementListShowAPI.data.query.counter = "0";
        allergyManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        AllergyManagementListShowAPIFunc allergyManagementListShowAPIFunc = new AllergyManagementListShowAPIFunc(getActivity());
        allergyManagementListShowAPIFunc.setDelegate(AllergyManagementFragment.this);
        allergyManagementListShowAPIFunc.execute(allergyManagementListShowAPI);
        swipeRefreshLayout.setRefreshing(setRefreshing);
        if (setRefreshing) {
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
    public void handleLoadMoreEvent(LoadMoreEvent loadMoreEvent) {
        if (this.isVisible()) {
            AllergyManagementListShowAPI allergyManagementListShowAPI = new AllergyManagementListShowAPI();
            allergyManagementListShowAPI.data.query.user_id = userId;
            allergyManagementListShowAPI.data.query.keyword = currentKeyword;
            allergyManagementListShowAPI.data.query.sort = currentSort;
            allergyManagementListShowAPI.data.query.type = currentType;
            allergyManagementListShowAPI.data.query.counter = String.valueOf(counter);
            allergyManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

            AllergyManagementListShowAPIFunc allergyManagementListShowAPIFunc = new AllergyManagementListShowAPIFunc(getActivity());
            allergyManagementListShowAPIFunc.setDelegate(AllergyManagementFragment.this);
            allergyManagementListShowAPIFunc.execute(allergyManagementListShowAPI);
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
        if (requestCode == RequestCodeList.ALLERGY_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                Gson gson = new Gson();
                AllergyManagementModel allergyManagementModel =
                        gson.fromJson(data.getStringExtra(ConstantsManagement.ALLERGY_MODEL_EXTRA), AllergyManagementModel.class);
                allergyManagementAdapter.updateItem(allergyManagementModel);
            }
        }
    }

    @Override
    public void addItem() {
        allergyManagementAdapter.submitItem();
    }

    @Override
    public void onFinishAllergyManagementShow(ResponseAPI responseAPI) {
        if (this.isVisible()) {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if (responseAPI.status_code == 200) {
                Gson gson = new Gson();
                AllergyManagementListShowAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementListShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    allergyManagementAdapter.setFailLoad(false);
                    // If refresh, clear adapter and reset the counter
                    if (output.data.query.flag.equals(Constants.FLAG_REFRESH)) {
                        allergyManagementAdapter.clearList();
                        counter = 0;
                    }
                    allergyManagementAdapter.addList(output.data.results.allergy_list);
                    lastItemCounter = output.data.results.allergy_list.size();
                    counter += output.data.results.allergy_list.size();

                    if (lastItemCounter > 0 &&
                            lastItemCounter >= APIConstants.ALLERGY_INF_SCROLL) {
                        allergyManagementAdapter.addSingle(null);
                    }
                    if (lastItemCounter == 0 &&
                            allergyManagementAdapter.getItemCount() == 0) {
                        noContentTV.setVisibility(View.VISIBLE);
                    } else {
                        noContentTV.setVisibility(View.GONE);
                    }
                } else {
                    allergyManagementAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (responseAPI.status_code == 504) {
                allergyManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if (responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((FastBaseActivity) getActivity()).forceLogout();
            } else {
                allergyManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }
}
