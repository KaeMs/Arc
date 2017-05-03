package com.med.fast.management.surgery;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.surgery.api.SurgeryManagementListShowAPI;
import com.med.fast.management.surgery.api.SurgeryManagementListShowAPIFunc;
import com.med.fast.management.surgery.surgeryinterface.SurgeryManagementShowIntf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class SurgeryManagementFragment extends FastBaseFragment implements SurgeryManagementShowIntf {
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
    private SurgeryManagementAdapter surgeryManagementAdapter;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = "";
    private String currentSort = "";
    private String userId = "18";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).changeTitle("SURGERY MANAGEMENT");
        setHasOptionsMenu(true);

        surgeryManagementAdapter = new SurgeryManagementAdapter(getActivity());

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

    public void refreshView(boolean showProgress){
        SurgeryManagementListShowAPI surgeryManagementListShowAPI = new SurgeryManagementListShowAPI();
        surgeryManagementListShowAPI.data.query.user_id = userId;
        surgeryManagementListShowAPI.data.query.keyword = currentKeyword;
        surgeryManagementListShowAPI.data.query.sort = currentSort;
        surgeryManagementListShowAPI.data.query.counter = "0";
        surgeryManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        SurgeryManagementListShowAPIFunc surgeryManagementListShowAPIFunc = new SurgeryManagementListShowAPIFunc(getActivity());
        surgeryManagementListShowAPIFunc.setDelegate(SurgeryManagementFragment.this);
        surgeryManagementListShowAPIFunc.execute(surgeryManagementListShowAPI);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.DISEASE_EDIT){
            if (resultCode == Activity.RESULT_OK){
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
        if (this.isVisible()){
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if(responseAPI.status_code == 200) {
                Gson gson = new Gson();
                SurgeryManagementListShowAPI output = gson.fromJson(responseAPI.status_response, SurgeryManagementListShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    // If refresh, clear adapter and reset the counter
                    surgeryManagementAdapter.setFailLoad(false);
                    if (output.data.query.flag.equals(Constants.FLAG_REFRESH)){
                        surgeryManagementAdapter.clearList();
                        counter = 0;
                    }
                    surgeryManagementAdapter.addList(output.data.results.surgery_list);
                    lastItemCounter = output.data.results.surgery_list.size();
                    counter += output.data.results.surgery_list.size();

                    if (lastItemCounter > 0){
                        surgeryManagementAdapter.addSingle(null);
                    }
                } else {
                    surgeryManagementAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if(responseAPI.status_code == 504) {
                surgeryManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if(responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((MainActivity)getActivity()).forceLogout();
            } else {
                surgeryManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}