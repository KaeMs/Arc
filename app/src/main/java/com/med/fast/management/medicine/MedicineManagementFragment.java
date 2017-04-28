package com.med.fast.management.medicine;

import android.app.Dialog;
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
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.management.medicine.api.MedicineManagementListShowAPI;
import com.med.fast.management.medicine.api.MedicineManagementListShowAPIFunc;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class MedicineManagementFragment extends FastBaseFragment implements MedicineManagementShowIntf {
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
    private MedicineManagementAdapter medicineManagementAdapter;
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
        ((MainActivity)getActivity()).changeTitle("MEDICINE MANAGEMENT");
        setHasOptionsMenu(true);

        medicineManagementAdapter = new MedicineManagementAdapter(getActivity());

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
                        MedicineManagementListShowAPI medicineManagementListShowAPI = new MedicineManagementListShowAPI();
                        medicineManagementListShowAPI.data.query.user_id = userId;
                        medicineManagementListShowAPI.data.query.keyword = currentKeyword;
                        medicineManagementListShowAPI.data.query.sort = currentSort;
                        medicineManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        medicineManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        MedicineManagementListShowAPIFunc medicineManagementListShowAPIFunc = new MedicineManagementListShowAPIFunc(getActivity());
                        medicineManagementListShowAPIFunc.setDelegate(MedicineManagementFragment.this);
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

    public void refreshView(boolean showProgress){
        MedicineManagementListShowAPI medicineManagementListShowAPI = new MedicineManagementListShowAPI();
        medicineManagementListShowAPI.data.query.user_id = userId;
        medicineManagementListShowAPI.data.query.keyword = currentKeyword;
        medicineManagementListShowAPI.data.query.sort = currentSort;
        medicineManagementListShowAPI.data.query.counter = "0";
        medicineManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        MedicineManagementListShowAPIFunc medicineManagementListShowAPIFunc = new MedicineManagementListShowAPIFunc(getActivity());
        medicineManagementListShowAPIFunc.setDelegate(MedicineManagementFragment.this);
        medicineManagementListShowAPIFunc.execute(medicineManagementListShowAPI);
        
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
            MedicineManagementListShowAPI medicineManagementListShowAPI = new MedicineManagementListShowAPI();
            medicineManagementListShowAPI.data.query.user_id = userId;
            medicineManagementListShowAPI.data.query.keyword = currentKeyword;
            medicineManagementListShowAPI.data.query.sort = currentSort;
            medicineManagementListShowAPI.data.query.counter = String.valueOf(counter);
            medicineManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

            MedicineManagementListShowAPIFunc medicineManagementListShowAPIFunc = new MedicineManagementListShowAPIFunc(getActivity());
            medicineManagementListShowAPIFunc.setDelegate(MedicineManagementFragment.this);
            medicineManagementListShowAPIFunc.execute(medicineManagementListShowAPI);
            isLoading = true;
        }
    }

    @Override
    public void addItem() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.management_medicine_popup);
        dialog.setCanceledOnTouchOutside(false);

        final CustomFontEditText medicineName = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_medicine_name);
        final CustomFontEditText medicineForm = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_medicine_form);
        final CustomFontEditText administrationMethod = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_administration_method);
        final CustomFontEditText administrationDose = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_administration_dose);
        final CustomFontEditText frequency = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_frequency);
        final CustomFontEditText reason = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_reason);
        final CustomFontEditText medicationStatus = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_status);
        final CustomFontEditText additionalInstruction = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_additional_instruction);

        CustomFontButton backBtn = (CustomFontButton) dialog.findViewById(R.id.medicine_popup_back_btn);
        CustomFontButton createBtn = (CustomFontButton) dialog.findViewById(R.id.medicine_popup_create_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(getActivity());
        mAwesomeValidation.addValidation(medicineName, RegexTemplate.NOT_EMPTY, getString(R.string.medicine_name_required));
        mAwesomeValidation.addValidation(medicineForm, RegexTemplate.NOT_EMPTY, getString(R.string.medicine_form_required));

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (mAwesomeValidation.validate()){
                    MedicineManagementModel medicineManagementModel = new MedicineManagementModel();
                    medicineManagementModel.setMedicine_name(medicineName.getText().toString());
                    medicineManagementModel.setMedicine_form(medicineForm.getText().toString());
                    medicineManagementModel.setMedicine_administration_method(administrationMethod.getText().toString());
                    medicineManagementModel.setMedicine_administration_dose(administrationDose.getText().toString());
                    medicineManagementModel.setMedicine_frequency(frequency.getText().toString());
                    medicineManagementModel.setMedicine_medication_reason(reason.getText().toString());
                    medicineManagementModel.setMedicine_medication_status(medicationStatus.getText().toString());
                    medicineManagementModel.setMedicine_additional_instruction(additionalInstruction.getText().toString());

                    medicineManagementAdapter.addSingle(medicineManagementModel);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
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
                dialog.setContentView(R.layout.management_medicine_popup);
                dialog.setCanceledOnTouchOutside(false);

                final CustomFontEditText medicineName = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_medicine_name);
                final CustomFontEditText medicineForm = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_medicine_form);
                final CustomFontEditText administrationMethod = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_administration_method);
                final CustomFontEditText administrationDose = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_administration_dose);
                final CustomFontEditText frequency = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_frequency);
                final CustomFontEditText reason = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_reason);
                final CustomFontEditText medicationStatus = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_status);
                final CustomFontEditText additionalInstruction = (CustomFontEditText) dialog.findViewById(R.id.medicine_popup_additional_instruction);

                CustomFontButton backBtn = (CustomFontButton) dialog.findViewById(R.id.medicine_popup_back_btn);
                CustomFontButton createBtn = (CustomFontButton) dialog.findViewById(R.id.medicine_popup_create_btn);

                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(getActivity());
                mAwesomeValidation.addValidation(medicineName, RegexTemplate.NOT_EMPTY, getString(R.string.medicine_name_required));
                mAwesomeValidation.addValidation(medicineForm, RegexTemplate.NOT_EMPTY, getString(R.string.medicine_form_required));

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAwesomeValidation.clear();
                        if (mAwesomeValidation.validate()){
                            MedicineManagementModel medicineManagementModel = new MedicineManagementModel();
                            medicineManagementModel.setMedicine_name(medicineName.getText().toString());
                            medicineManagementModel.setMedicine_form(medicineForm.getText().toString());
                            medicineManagementModel.setMedicine_administration_method(administrationMethod.getText().toString());
                            medicineManagementModel.setMedicine_administration_dose(administrationDose.getText().toString());
                            medicineManagementModel.setMedicine_frequency(frequency.getText().toString());
                            medicineManagementModel.setMedicine_medication_reason(reason.getText().toString());
                            medicineManagementModel.setMedicine_medication_status(medicationStatus.getText().toString());
                            medicineManagementModel.setMedicine_additional_instruction(additionalInstruction.getText().toString());

                            medicineManagementAdapter.addSingle(medicineManagementModel);
                            dialog.dismiss();
                        }
                    }
                });
                /*dialog.show();*/
            }
        });
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

                    if (lastItemCounter > 0){
                        medicineManagementAdapter.addSingle(null);
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
                ((MainActivity)getActivity()).forceLogout();
            } else {
                medicineManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
