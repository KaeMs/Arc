package com.med.fast.management.medicine;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class MedicineManagementFragment extends FastBaseFragment {
    @BindView(R.id.management_mainfragment_search_edittxt)
    CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    ImageView searchBtn;
    @BindView(R.id.management_mainfragment_recycler)
    RecyclerView recyclerView;
    private MedicineManagementAdapter medicineManagementAdapter;

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
                        }
                    }
                });
            }
        });
    }
}
