package com.med.fast.management.labresult;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.med.fast.Constants;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.management.accidenthistory.AccidentHistoryManagementFragment;
import com.med.fast.management.accidenthistory.api.AccidentHistoryListShowAPI;
import com.med.fast.management.accidenthistory.api.AccidentHistoryListShowAPIFunc;
import com.med.fast.management.disease.api.DiseaseManagementListShowAPI;
import com.med.fast.management.disease.api.DiseaseManagementListShowAPIFunc;
import com.med.fast.management.labresult.api.LabResultManagementListShowAPI;
import com.med.fast.management.labresult.api.LabResultManagementListShowAPIFunc;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementListShowIntf;

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

public class LabResultManagementFragment extends FastBaseFragment implements LabResultManagementListShowIntf {
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
    private String userId = "18";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).changeTitle("DISEASE MANAGEMENT");
        setHasOptionsMenu(true);

        labResultManagementAdapter = new LabResultManagementAdapter(getActivity());

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
                dialog.setContentView(R.layout.management_labresult_popup);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                final CustomFontEditText testType = (CustomFontEditText) dialog.findViewById(R.id.labresult_popup_test_type);
                final CustomFontEditText testLocation = (CustomFontEditText) dialog.findViewById(R.id.labresult_popup_test_location);
                final CustomFontEditText testDescription = (CustomFontEditText) dialog.findViewById(R.id.labresult_popup_test_description);
                final CustomFontEditText testFinishedDate = (CustomFontEditText) dialog.findViewById(R.id.labresult_popup_test_finished_date);

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(getActivity());
                mAwesomeValidation.addValidation(testType, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_type_question_required));
                mAwesomeValidation.addValidation(testLocation, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_location_question_required));
                mAwesomeValidation.addValidation(testDescription, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_description_question_required));
                mAwesomeValidation.addValidation(testFinishedDate, RegexTemplate.NOT_EMPTY, getString(R.string.lab_test_date_question_required));

                final Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                testFinishedDate.setOnClickListener(new View.OnClickListener() {
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
                                        testFinishedDate.setText(date);
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
                            LabResultManagementModel labResultManagementModel = new LabResultManagementModel();
                            labResultManagementModel.setTest_type(testType.getText().toString());
                            labResultManagementModel.setTest_location(testLocation.getText().toString());
                            labResultManagementModel.setTest_description(testDescription.getText().toString());
                            labResultManagementModel.setTest_date(testFinishedDate.getText().toString());

                            labResultManagementAdapter.addSingle(labResultManagementModel);

                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onFinishLabResultManagementListShow(ResponseAPI responseAPI) {

    }
}
