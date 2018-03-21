package com.med.fast.management.medicine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseFragment;
import com.med.fast.FastBaseManagementFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.ItemAddedEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.medicine.api.MedicineManagementListShowAPI;
import com.med.fast.management.medicine.api.MedicineManagementListShowAPIFunc;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class MedicineManagementFragment extends FastBaseManagementFragment implements MedicineManagementShowIntf, StartActivityForResultInAdapterIntf {
    private MedicineManagementAdapter medicineManagementAdapter;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = APIConstants.DEFAULT;
    private String currentSort = APIConstants.DEFAULT;
    private String userId;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.medicine_management_caps));
        userId = SharedPreferenceUtilities.getUserId(getActivity());

        medicineManagementAdapter = new MedicineManagementAdapter(getActivity(), this, false);

        recyclerView.setAdapter(medicineManagementAdapter);

        noContentTV.setText(getString(R.string.no_medication_record));
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
                    if (lastItemCounter >= APIConstants.MEDICINE_INF_SCROLL) {
                        MedicineManagementListShowAPI medicineManagementListShowAPI = new MedicineManagementListShowAPI();
                        medicineManagementListShowAPI.data.query.user_id = userId;
                        medicineManagementListShowAPI.data.query.keyword = currentKeyword;
                        medicineManagementListShowAPI.data.query.sort = currentSort;
                        medicineManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        medicineManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        MedicineManagementListShowAPIFunc medicineManagementListShowAPIFunc = new MedicineManagementListShowAPIFunc(getActivity(), MedicineManagementFragment.this);
                        medicineManagementListShowAPIFunc.execute(medicineManagementListShowAPI);
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
        MedicineManagementListShowAPI medicineManagementListShowAPI = new MedicineManagementListShowAPI();
        medicineManagementListShowAPI.data.query.user_id = userId;
        medicineManagementListShowAPI.data.query.keyword = currentKeyword;
        medicineManagementListShowAPI.data.query.sort = currentSort;
        medicineManagementListShowAPI.data.query.counter = "0";
        medicineManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        MedicineManagementListShowAPIFunc medicineManagementListShowAPIFunc = new MedicineManagementListShowAPIFunc(getActivity(), MedicineManagementFragment.this);
        medicineManagementListShowAPIFunc.execute(medicineManagementListShowAPI);
        swipeRefreshLayout.setRefreshing(setRefreshing);
        if (setRefreshing){
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        isLoading = true;
    }

    @Subscribe
    public void handleLoadMoreEvent (LoadMoreEvent loadMoreEvent){
        if (this.isVisible()){
            MedicineManagementListShowAPI medicineManagementListShowAPI = new MedicineManagementListShowAPI();
            medicineManagementListShowAPI.data.query.user_id = userId;
            medicineManagementListShowAPI.data.query.keyword = currentKeyword;
            medicineManagementListShowAPI.data.query.sort = currentSort;
            medicineManagementListShowAPI.data.query.counter = String.valueOf(counter);
            medicineManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

            MedicineManagementListShowAPIFunc medicineManagementListShowAPIFunc = new MedicineManagementListShowAPIFunc(getActivity(), MedicineManagementFragment.this);
            medicineManagementListShowAPIFunc.execute(medicineManagementListShowAPI);
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
        if (requestCode == RequestCodeList.MEDICINE_EDIT){
            if (resultCode == Activity.RESULT_OK){
                Gson gson = new Gson();
                MedicineManagementModel model =
                        gson.fromJson(data.getStringExtra(ConstantsManagement.MEDICINE_MODEL_EXTRA), MedicineManagementModel.class);
                medicineManagementAdapter.updateItem(model);
            }
        }
    }

    @Override
    public void addItem() {
        medicineManagementAdapter.submitItem();
    }

    @Override
    public void onFinishMedicineManagementShow(ResponseAPI responseAPI) {
        if (this.isVisible()){
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if(responseAPI.status_code == 200) {
                Gson gson = new Gson();
                MedicineManagementListShowAPI output = gson.fromJson(responseAPI.status_response, MedicineManagementListShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    // If refresh, clear adapter and reset the counter
                    medicineManagementAdapter.setFailLoad(false);
                    if (output.data.query.flag.equals(Constants.FLAG_REFRESH)){
                        medicineManagementAdapter.clearList();
                        counter = 0;
                    }
                    medicineManagementAdapter.addList(output.data.results.medicine_list);
                    lastItemCounter = output.data.results.medicine_list.size();
                    counter += output.data.results.medicine_list.size();

                    if (lastItemCounter > 0 &&
                            lastItemCounter >= APIConstants.MEDICINE_INF_SCROLL){
                        medicineManagementAdapter.addSingle(null);
                    }
                    if (lastItemCounter == 0 &&
                            medicineManagementAdapter.getItemCount() == 0){
                        noContentTV.setVisibility(View.VISIBLE);
                    } else {
                        noContentTV.setVisibility(View.GONE);
                    }
                } else {
                    medicineManagementAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if(responseAPI.status_code == 504) {
                medicineManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if(responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((FastBaseActivity)getActivity()).forceLogout();
            } else {
                medicineManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }
}
