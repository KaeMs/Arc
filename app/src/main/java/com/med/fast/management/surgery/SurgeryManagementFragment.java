package com.med.fast.management.surgery;

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
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.medicine.MedicineManagementAdapter;
import com.med.fast.management.medicine.MedicineManagementModel;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class SurgeryManagementFragment extends FastBaseFragment {
    @BindView(R.id.management_mainfragment_search_edittxt)
    CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    ImageView searchBtn;
    @BindView(R.id.management_mainfragment_recycler)
    RecyclerView recyclerView;
    private SurgeryManagementAdapter surgeryManagementAdapter;

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
                dialog.setContentView(R.layout.management_surgery_popup);
                dialog.setCanceledOnTouchOutside(false);

                final CustomFontEditText surgeryProcedure = (CustomFontEditText) dialog.findViewById(R.id.surgery_popup_procedure);
                final CustomFontEditText physicianName = (CustomFontEditText) dialog.findViewById(R.id.surgery_popup_physician_name);
                final CustomFontEditText hospitalName = (CustomFontEditText) dialog.findViewById(R.id.surgery_popup_hospital_name);
                final CustomFontTextView surgeryDate = (CustomFontTextView) dialog.findViewById(R.id.surgery_popup_surgery_date);
                final CustomFontEditText surgeryNote = (CustomFontEditText) dialog.findViewById(R.id.surgery_popup_note);

                CustomFontButton backBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_back_btn);
                CustomFontButton createBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_create_btn);

                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(getActivity());
                mAwesomeValidation.addValidation(surgeryProcedure, RegexTemplate.NOT_EMPTY, getString(R.string.surgery_procedure_required));
                mAwesomeValidation.addValidation(hospitalName, RegexTemplate.NOT_EMPTY, getString(R.string.hospital_name_required));
                mAwesomeValidation.addValidation(physicianName, RegexTemplate.NOT_EMPTY, getString(R.string.physician_name_required));

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAwesomeValidation.clear();
                        if (!surgeryDate.getText().toString().equals("")){
                            if (mAwesomeValidation.validate()){
                                SurgeryManagementModel surgeryManagementModel = new SurgeryManagementModel();
                                surgeryManagementModel.setSurgery_procedure(surgeryProcedure.getText().toString());
                                surgeryManagementModel.setSurgery_physician_name(physicianName.getText().toString());
                                surgeryManagementModel.setSurgery_hospital_name(hospitalName.getText().toString());
                                surgeryManagementModel.setSurgery_date(surgeryDate.getText().toString());
                                surgeryManagementModel.setSurgery_note(surgeryNote.getText().toString());

                                surgeryManagementAdapter.addSingle(surgeryManagementModel);
                                dialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.surgery_date_required), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });
    }
}
