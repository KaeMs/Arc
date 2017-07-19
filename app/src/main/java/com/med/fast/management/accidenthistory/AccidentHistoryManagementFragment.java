package com.med.fast.management.accidenthistory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.med.fast.customevents.ItemAddedEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.accidenthistory.accidentinterface.AccidentHistoryShowIntf;
import com.med.fast.management.accidenthistory.api.AccidentHistoryListShowAPI;
import com.med.fast.management.accidenthistory.api.AccidentHistoryListShowAPIFunc;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class AccidentHistoryManagementFragment extends FastBaseFragment implements AccidentHistoryShowIntf, StartActivityForResultInAdapterIntf {
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

    private AccidentHistoryManagementAdapter accidentHistoryManagementAdapter;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = APIConstants.DEFAULT;
    private String currentSort = APIConstants.DEFAULT;
    private String userId;

    public AccidentHistoryManagementFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).changeTitle("ACCIDENT MANAGEMENT");
//        setHasOptionsMenu(true);

        userId = SharedPreferenceUtilities.getUserId(getActivity());
        accidentHistoryManagementAdapter = new AccidentHistoryManagementAdapter(getActivity(), this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(accidentHistoryManagementAdapter);

        noContentTV.setText(getString(R.string.no_accident_record));
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
                    if (lastItemCounter >= APIConstants.ACCIDENT_INF_SCROLL) {
                        AccidentHistoryListShowAPI accidentHistoryListShowAPI = new AccidentHistoryListShowAPI();
                        accidentHistoryListShowAPI.data.query.user_id = userId;
                        accidentHistoryListShowAPI.data.query.keyword = currentKeyword;
                        accidentHistoryListShowAPI.data.query.sort = currentSort;
                        accidentHistoryListShowAPI.data.query.counter = String.valueOf(counter);
                        accidentHistoryListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        AccidentHistoryListShowAPIFunc accidentHistoryListShowAPIFunc = new AccidentHistoryListShowAPIFunc(getActivity());
                        accidentHistoryListShowAPIFunc.setDelegate(AccidentHistoryManagementFragment.this);
                        accidentHistoryListShowAPIFunc.execute(accidentHistoryListShowAPI);
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

    @Override
    public void onPause() {
        super.onPause();
        getArguments().putParcelable(Constants.MANAGER_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
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
        AccidentHistoryListShowAPI accidentHistoryListShowAPI = new AccidentHistoryListShowAPI();
        accidentHistoryListShowAPI.data.query.user_id = userId;
        accidentHistoryListShowAPI.data.query.keyword = currentKeyword;
        accidentHistoryListShowAPI.data.query.sort = currentSort;
        accidentHistoryListShowAPI.data.query.counter = "0";
        accidentHistoryListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        AccidentHistoryListShowAPIFunc accidentHistoryListShowAPIFunc = new AccidentHistoryListShowAPIFunc(getActivity());
        accidentHistoryListShowAPIFunc.setDelegate(AccidentHistoryManagementFragment.this);
        accidentHistoryListShowAPIFunc.execute(accidentHistoryListShowAPI);
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
            AccidentHistoryListShowAPI accidentHistoryListShowAPI = new AccidentHistoryListShowAPI();
            accidentHistoryListShowAPI.data.query.user_id = userId;
            accidentHistoryListShowAPI.data.query.keyword = currentKeyword;
            accidentHistoryListShowAPI.data.query.sort = currentSort;
            accidentHistoryListShowAPI.data.query.counter = String.valueOf(counter);
            accidentHistoryListShowAPI.data.query.flag = Constants.FLAG_LOAD;

            AccidentHistoryListShowAPIFunc accidentHistoryListShowAPIFunc = new AccidentHistoryListShowAPIFunc(getActivity());
            accidentHistoryListShowAPIFunc.setDelegate(AccidentHistoryManagementFragment.this);
            accidentHistoryListShowAPIFunc.execute(accidentHistoryListShowAPI);
            isLoading = true;
        }
    }

    @Subscribe
    void onItemAdded(ItemAddedEvent itemAddedEvent) {
        noContentTV.setVisibility(View.GONE);
        scrollToTop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeList.ACCIDENT_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                Gson gson = new Gson();
                AccidentHistoryManagementModel accidentHistoryManagementModel =
                        gson.fromJson(data.getStringExtra(ConstantsManagement.ACCIDENT_MODEL_EXTRA), AccidentHistoryManagementModel.class);
                accidentHistoryManagementAdapter.updateItem(accidentHistoryManagementModel);
            }
        }
    }

    @Override
    public void addItem() {
        accidentHistoryManagementAdapter.submitItem();
    }

/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_layout_add_btn);
        ImageView addBtn = (ImageView) MenuItemCompat.getActionView(searchItem);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.management_accident_popup);
                dialog.setCanceledOnTouchOutside(false);

                final CustomFontEditText accidentDetails = (CustomFontEditText) dialog.findViewById(R.id.accident_popup_accident_details);
                final CustomFontEditText injuryNature = (CustomFontEditText) dialog.findViewById(R.id.accident_popup_injury_nature);
                final CustomFontEditText injuryLocation = (CustomFontEditText) dialog.findViewById(R.id.accident_popup_injury_location);
                final CustomFontTextView accidentDateTV = (CustomFontTextView) dialog.findViewById(R.id.accident_popup_accident_date_tv);
                final Spinner accidentDateSpinner = (Spinner) dialog.findViewById(R.id.accident_popup_accident_date_spinner);
                String[] approximates = getResources().getStringArray(R.array.accident_approximate_values);
                final ArrayAdapter<String> accidentSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, approximates);
                accidentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                accidentDateSpinner.setAdapter(accidentSpinnerAdapter);

                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                accidentDateTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), null, year, month, day);
                        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                        datePickerDialog.getDatePicker().updateDate(year, month, day);
                        datePickerDialog.show();
                        datePickerDialog.setCanceledOnTouchOutside(true);

                        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Formatting date from MM to MMM
                                        SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.getDefault());
                                        Date newDate = null;
                                        try {
                                            newDate = format.parse(String.valueOf(month) + " " + String.valueOf(day) + " " + String.valueOf(year));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        format = new SimpleDateFormat(Constants.dateFormatSpace, Locale.getDefault());
                                        String date = format.format(newDate);
                                        accidentDateTV.setText(date);
                                    }
                                });

                        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                                            // Do Stuff
                                            datePickerDialog.dismiss();
                                        }
                                    }
                                });
                    }
                });

                CustomFontButton backBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_back_btn);
                CustomFontButton createBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_create_btn);

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(getActivity());
                mAwesomeValidation.addValidation(accidentDetails, RegexTemplate.NOT_EMPTY, getString(R.string.full_accident_details_required));

                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAwesomeValidation.clear();
                        if (mAwesomeValidation.validate()){
                            AccidentHistoryManagementModel accidentHistoryManagementModel = new AccidentHistoryManagementModel();
                            accidentHistoryManagementModel.setDetail(accidentDetails.getText().toString());
                            accidentHistoryManagementModel.setInjury_nature(injuryNature.getText().toString());
                            accidentHistoryManagementModel.setInjury_location(injuryLocation.getText().toString());
                            if (!accidentDateTV.getText().toString().equals("")){
                                accidentHistoryManagementModel.setInjury_date(accidentDateTV.getText().toString());
                            } else if (accidentDateSpinner.getSelectedItemPosition() > 0){
                                accidentHistoryManagementModel.setInjury_date(accidentSpinnerAdapter.getItem(accidentDateSpinner.getSelectedItemPosition()));
                            }
                            accidentHistoryManagementModel.setProgress_status("0");

                            accidentHistoryManagementAdapter.addSingle(accidentHistoryManagementModel);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }*/

    @Override
    public void onFinishAccidentHistoryShow(ResponseAPI responseAPI) {
        if (this.isVisible()) {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if (responseAPI.status_code == 200) {
                Gson gson = new Gson();
                AccidentHistoryListShowAPI output = gson.fromJson(responseAPI.status_response, AccidentHistoryListShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    // If refresh, clear adapter and reset the counter
                    accidentHistoryManagementAdapter.setFailLoad(false);
                    if (output.data.query.flag.equals(Constants.FLAG_REFRESH)) {
                        accidentHistoryManagementAdapter.clearList();
                        counter = 0;
                    }
                    accidentHistoryManagementAdapter.addList(output.data.results.accident_list);
                    lastItemCounter = output.data.results.accident_list.size();
                    counter += output.data.results.accident_list.size();

                    if (lastItemCounter > 0 &&
                            lastItemCounter >= APIConstants.ACCIDENT_INF_SCROLL) {
                        accidentHistoryManagementAdapter.addSingle(null);
                    }
                    if (lastItemCounter == 0 &&
                            accidentHistoryManagementAdapter.getItemCount() == 0) {
                        noContentTV.setVisibility(View.VISIBLE);
                    } else {
                        noContentTV.setVisibility(View.GONE);
                    }
                } else {
                    accidentHistoryManagementAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (responseAPI.status_code == 504) {
                accidentHistoryManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if (responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                forceLogout();
            } else {
                accidentHistoryManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }
}
