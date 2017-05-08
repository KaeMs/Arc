package com.med.fast.management.allergy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.allergy.allergyinterface.AllergyManagementShowIntf;
import com.med.fast.management.allergy.api.AllergyManagementListShowAPI;
import com.med.fast.management.allergy.api.AllergyManagementListShowAPIFunc;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/23/2017. FM
 */

public class AllergyManagementFragment extends FastBaseFragment implements AllergyManagementShowIntf, StartActivityForResultInAdapterIntf {
    @BindView(R.id.management_mainfragment_search_edittxt)
    CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    ImageView searchBtn;
    @BindView(R.id.management_mainfragment_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.management_mainfragment_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.management_mainfragment_progress)
    ProgressBar progressBar;
    @BindView(R.id.management_mainfragment_nocontent_tv)
    CustomFontTextView noContentTV;
    private AllergyManagementAdapter allergyManagementAdapter;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = APIConstants.DEFAULT;
    private String currentSort = APIConstants.DEFAULT;
    private String currentType = APIConstants.DEFAULT;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).changeTitle("ALLERGY MANAGEMENT");

        userId = SharedPreferenceUtilities.getUserId(getActivity());
        allergyManagementAdapter = new AllergyManagementAdapter(getActivity(), this, false);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(allergyManagementAdapter);

        noContentTV.setText(getString(R.string.no_allergy_record));
        progressBar.setVisibility(View.VISIBLE);
        refreshView(false);

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

    public void refreshView(boolean showProgress){
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.ALLERGY_EDIT){
            if (resultCode == Activity.RESULT_OK){
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
        if (this.isVisible()){
            progressBar.setVisibility(View.GONE);
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

                    if (lastItemCounter > 0 &&
                            lastItemCounter >= APIConstants.ALLERGY_INF_SCROLL){
                        allergyManagementAdapter.addSingle(null);
                    }
                    if (lastItemCounter == 0 &&
                            allergyManagementAdapter.getItemCount() == 0){
                        noContentTV.setVisibility(View.VISIBLE);
                    } else {
                        noContentTV.setVisibility(View.GONE);
                    }
                } else {
                    allergyManagementAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if(responseAPI.status_code == 504) {
                allergyManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if(responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((FastBaseActivity)getActivity()).forceLogout();
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
