package com.med.fast.management.disease;

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
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.Utils;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.accidenthistory.api.AccidentHistoryListShowAPI;
import com.med.fast.management.disease.api.DiseaseManagementListShowAPI;
import com.med.fast.management.disease.api.DiseaseManagementListShowAPIFunc;
import com.med.fast.management.disease.diseaseinterface.DiseaseManagementShowIntf;

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

public class DiseaseManagementFragment extends FastBaseFragment implements DiseaseManagementShowIntf {
    @BindView(R.id.management_mainfragment_search_edittxt)
    CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    ImageView searchBtn;
    @BindView(R.id.management_mainfragment_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.management_mainfragment_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.management_mainfragment_progress)
    ProgressBar progressBar;
    private DiseaseManagementAdapter diseaseManagementAdapter;
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

        diseaseManagementAdapter = new DiseaseManagementAdapter(getActivity());

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
    public void refreshView(boolean showProgress){
        DiseaseManagementListShowAPI diseaseManagementListShowAPI = new DiseaseManagementListShowAPI();
        diseaseManagementListShowAPI.data.query.user_id = userId;
        diseaseManagementListShowAPI.data.query.keyword = currentKeyword;
        diseaseManagementListShowAPI.data.query.sort = currentSort;
        diseaseManagementListShowAPI.data.query.counter = "0";
        diseaseManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        DiseaseManagementListShowAPIFunc diseaseManagementListShowAPIFunc = new DiseaseManagementListShowAPIFunc(getActivity());
        diseaseManagementListShowAPIFunc.setDelegate(DiseaseManagementFragment.this);
        diseaseManagementListShowAPIFunc.execute(diseaseManagementListShowAPI);
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
                dialog.setContentView(R.layout.management_disease_popup);
                dialog.setCanceledOnTouchOutside(false);

                final CustomFontEditText diseaseName = (CustomFontEditText) dialog.findViewById(R.id.disease_popup_name);
                final CustomFontRadioButton hereditaryY = (CustomFontRadioButton) dialog.findViewById(R.id.disease_popup_hereditary_y_rb);
                final CustomFontEditText inheritedFrom = (CustomFontEditText) dialog.findViewById(R.id.disease_popup_inherited_from);

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(getActivity());
                mAwesomeValidation.addValidation(diseaseName, RegexTemplate.NOT_EMPTY, getString(R.string.full_accident_details_required));

                // Setting Hereditary RB so when it is at "no", inheritance won't be added
                hereditaryY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        inheritedFrom.setEnabled(isChecked);
                        if (isChecked){
                            mAwesomeValidation.addValidation(inheritedFrom, RegexTemplate.NOT_EMPTY, getString(R.string.disease_hereditary_inherited_from_required));
                        } else {
                            mAwesomeValidation.clear();
                        }
                    }
                });

                final CustomFontRadioButton ongoingY = (CustomFontRadioButton) dialog.findViewById(R.id.disease_popup_currently_having_y_rb);
                final CustomFontTextView historicDate = (CustomFontTextView) dialog.findViewById(R.id.disease_popup_historic_date_tv);
                final Spinner approximateDateSpinner = (Spinner) dialog.findViewById(R.id.disease_popup_date_spinner);

                String[] approximates = getResources().getStringArray(R.array.accident_approximate_values);
                final ArrayAdapter<String> approximateSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, approximates);
                approximateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                approximateDateSpinner.setAdapter(approximateSpinnerAdapter);


                final Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                historicDate.setOnClickListener(new View.OnClickListener() {
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
                                        historicDate.setText(date);
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
                            DiseaseManagementModel diseaseManagementModel = new DiseaseManagementModel();
                            diseaseManagementModel.setDisease_name(diseaseName.getText().toString());
                            if (hereditaryY.isChecked()){
                                diseaseManagementModel.setDisease_hereditary("yes");
                                diseaseManagementModel.setDisease_hereditary_carriers(inheritedFrom.getText().toString());
                            } else {
                                diseaseManagementModel.setDisease_hereditary("no");
                                diseaseManagementModel.setDisease_hereditary_carriers(getString(R.string.sign_dash));
                            }

                            if (ongoingY.isChecked()){
                                diseaseManagementModel.setDisease_ongoing("yes");
                            } else {
                                diseaseManagementModel.setDisease_ongoing("no");
                            }

                            diseaseManagementModel.setDate_last_visit("-");
                            if (!historicDate.getText().toString().equals("")){
                                diseaseManagementModel.setDate_historic(historicDate.getText().toString());
                            } else if (approximateDateSpinner.getSelectedItemPosition() > 0){
                                diseaseManagementModel.setDate_approximate(approximateSpinnerAdapter.getItem(approximateDateSpinner.getSelectedItemPosition()));
                            }
                            diseaseManagementModel.setDate_created(Utils.getCurrentDate());

                            diseaseManagementAdapter.addSingle(diseaseManagementModel);

                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public void onFinishDiseaseManagementShow(ResponseAPI responseAPI) {
        if (this.isVisible()){
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if(responseAPI.status_code == 200) {
                Gson gson = new Gson();
                DiseaseManagementListShowAPI output = gson.fromJson(responseAPI.status_response, DiseaseManagementListShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    // If refresh, clear adapter and reset the counter
                    diseaseManagementAdapter.setFailLoad(false);
                    if (output.data.query.flag.equals(Constants.FLAG_REFRESH)){
                        diseaseManagementAdapter.clearList();
                        counter = 0;
                    }
                    diseaseManagementAdapter.addList(output.data.results.disease_list);
                    lastItemCounter = output.data.results.disease_list.size();
                    counter += output.data.results.disease_list.size();

                    if (lastItemCounter > 0){
                        diseaseManagementAdapter.addSingle(null);
                    }
                } else {
                    diseaseManagementAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if(responseAPI.status_code == 504) {
                diseaseManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if(responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((MainActivity)getActivity()).forceLogout();
            } else {
                diseaseManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
