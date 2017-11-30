package com.med.fast.management.visit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.med.fast.Constants;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseFragment;
import com.med.fast.FastBaseManagementFragment;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.UriUtils;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.ItemAddedEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.MainActivity;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.allergy.api.AllergyManagementListShowAPI;
import com.med.fast.management.labresult.LabResultManagementFragment;
import com.med.fast.management.labresult.api.LabResultManagementListShowAPI;
import com.med.fast.management.labresult.api.LabResultManagementListShowAPIFunc;
import com.med.fast.management.visit.api.VisitManagementListShowAPI;
import com.med.fast.management.visit.api.VisitManagementListShowAPIFunc;
import com.med.fast.management.visit.visitinterface.VisitShowIntf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitFragment extends FastBaseManagementFragment implements StartActivityForResultInAdapterIntf, VisitShowIntf {

    private LinearLayoutManager linearLayoutManager;
    private VisitAdapter visitAdapter;
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
        setTitle(getString(R.string.visit_management_caps));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        setHasOptionsMenu(true);
        userId = SharedPreferenceUtilities.getUserId(getActivity());
        visitAdapter = new VisitAdapter(getActivity(), VisitFragment.this, displayMetrics.widthPixels);

        recyclerView.setAdapter(visitAdapter);

        noContentTV.setText(getString(R.string.no_visit_record));

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
                swipeRefreshLayout.setEnabled(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);

                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                int visibleThreshold = 1;

                // When threshold is reached, API call is made to get new items
                // for infinite scroll
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (lastItemCounter > APIConstants.VISIT_INF_SCROLL) {
                        VisitManagementListShowAPI visitManagementListShowAPI = new VisitManagementListShowAPI();
                        visitManagementListShowAPI.data.query.user_id = userId;
                        visitManagementListShowAPI.data.query.keyword = currentKeyword;
                        visitManagementListShowAPI.data.query.sort = currentSort;
                        visitManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        visitManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        VisitManagementListShowAPIFunc visitManagementListShowAPIFunc = new VisitManagementListShowAPIFunc(getActivity());
                        visitManagementListShowAPIFunc.setDelegate(VisitFragment.this);
                        visitManagementListShowAPIFunc.execute(visitManagementListShowAPI);
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
//        visitAdapter.forceEventbusUnregistration();
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
        VisitManagementListShowAPI visitManagementListShowAPI = new VisitManagementListShowAPI();
        visitManagementListShowAPI.data.query.user_id = userId;
        visitManagementListShowAPI.data.query.keyword = currentKeyword;
        visitManagementListShowAPI.data.query.sort = currentSort;
        visitManagementListShowAPI.data.query.counter = "0";
        visitManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        VisitManagementListShowAPIFunc visitManagementListShowAPIFunc = new VisitManagementListShowAPIFunc(getActivity());
        visitManagementListShowAPIFunc.setDelegate(VisitFragment.this);
        visitManagementListShowAPIFunc.execute(visitManagementListShowAPI);
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
            VisitManagementListShowAPI visitManagementListShowAPI = new VisitManagementListShowAPI();
            visitManagementListShowAPI.data.query.user_id = userId;
            visitManagementListShowAPI.data.query.keyword = currentKeyword;
            visitManagementListShowAPI.data.query.sort = currentSort;
            visitManagementListShowAPI.data.query.counter = String.valueOf(counter);
            visitManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

            VisitManagementListShowAPIFunc visitManagementListShowAPIFunc = new VisitManagementListShowAPIFunc(getActivity());
            visitManagementListShowAPIFunc.setDelegate(VisitFragment.this);
            visitManagementListShowAPIFunc.execute(visitManagementListShowAPI);
            isLoading = true;
        }
    }

    @Subscribe
    public void onItemAdded(ItemAddedEvent itemAddedEvent) {
        noContentTV.setVisibility(View.GONE);
        scrollToTop();
    }

    @Override
    public void onStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.VISIT_CREATE){
            if (resultCode == Activity.RESULT_OK){
                /*Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Uri.class, new UriUtils.UriDeserializer())
                    .create();
                String visitModelStr = data.getStringExtra(ConstantsManagement.VISIT_MODEL_EXTRA);
                VisitModel model =
                        gson.fromJson(visitModelStr, VisitModel.class);
                visitAdapter.addSingle(model, 0);
                noContentTV.setVisibility(View.GONE);*/
                refreshView(true);
                scrollToTop();
            }
        } else if (requestCode == RequestCodeList.VISIT_EDIT){
            if (resultCode == Activity.RESULT_OK){
                /*Gson gson = new Gson();
                VisitModel model =
                        gson.fromJson(data.getStringExtra(ConstantsManagement.VISIT_MODEL_EXTRA), VisitModel.class);
                visitAdapter.updateItem(model);*/
                returningWithEdit = true;
                refreshView(true);
            }
        }
    }

    @Override
    public void addItem() {
        visitAdapter.submitItem();
    }

    @Override
    public void onFinishVisitShow(ResponseAPI responseAPI) {
        if (this.isVisible()){
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if(responseAPI.status_code == 200) {
                Gson gson = new Gson();
                VisitManagementListShowAPI output = gson.fromJson(responseAPI.status_response, VisitManagementListShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    visitAdapter.setFailLoad(false);
                    // If refresh, clear adapter and reset the counter
                    if (output.data.query.flag.equals(Constants.FLAG_REFRESH)){
                        visitAdapter.clearList();
                        counter = 0;
                    }

                    if (returningWithEdit){
                        Parcelable savedRecyclerLayoutState = getArguments().getParcelable(Constants.MANAGER_STATE);
                        if (savedRecyclerLayoutState != null){
                            linearLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
                        }
                        returningWithEdit = false;
                    }

                    visitAdapter.addList(output.data.results.visit_list);
                    lastItemCounter = output.data.results.visit_list.size();
                    counter += output.data.results.visit_list.size();

                    if (lastItemCounter > 0 &&
                            lastItemCounter >= APIConstants.VISIT_INF_SCROLL){
                        visitAdapter.addSingle(null);
                    }
                    if (lastItemCounter == 0 &&
                            visitAdapter.getItemCount() == 0){
                        noContentTV.setVisibility(View.VISIBLE);
                    } else {
                        noContentTV.setVisibility(View.GONE);
                    }
                } else {
                    visitAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if(responseAPI.status_code == 504) {
                visitAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if(responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((FastBaseActivity)getActivity()).forceLogout();
            } else {
                visitAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
