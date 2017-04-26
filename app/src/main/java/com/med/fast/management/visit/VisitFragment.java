package com.med.fast.management.visit;

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
import com.med.fast.R;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.MainActivity;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitFragment extends FastBaseFragment {

    @BindView(R.id.management_mainfragment_search_edittxt)
    CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    ImageView searchBtn;
    @BindView(R.id.management_mainfragment_recycler)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).changeTitle("VISIT MANAGEMENT");
        setHasOptionsMenu(true);

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
                dialog.setContentView(R.layout.management_visit_popup);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                CustomFontEditText doctorName = (CustomFontEditText) dialog.findViewById(R.id.visit_popup_doctor_name);
                CustomFontEditText hospitalName = (CustomFontEditText) dialog.findViewById(R.id.visit_popup_hospital_name);
                CustomFontEditText diagnose = (CustomFontEditText) dialog.findViewById(R.id.visit_popup_diagnose);
                RecyclerView imageRecycler = (RecyclerView) dialog.findViewById(R.id.visit_popup_imagerecycler);
                RecyclerView diseaseHistoryRecycler = (RecyclerView) dialog.findViewById(R.id.visit_popup_disease_history_recycler);
                RecyclerView diseaseInputRecycler = (RecyclerView) dialog.findViewById(R.id.visit_popup_disease_input_recycler);

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
                mAwesomeValidation.addValidation(doctorName, RegexTemplate.NOT_EMPTY, getString(R.string.doctor_name_required));
                mAwesomeValidation.addValidation(hospitalName, RegexTemplate.NOT_EMPTY, getString(R.string.hospital_name_required));
                mAwesomeValidation.addValidation(diagnose, RegexTemplate.NOT_EMPTY, getString(R.string.diagnose_required));


                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAwesomeValidation.clear();
                        if (mAwesomeValidation.validate()) {

                        }
                    }
                });
            }
        });
    }

}
