package com.med.fast.management.allergy;

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
import com.med.fast.adapters.AllergyAdapter;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.models.Allergy;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/23/2017. FM
 */

public class AllergyManagementFragment extends FastBaseFragment {
    @BindView(R.id.management_mainfragment_search_edittxt)
    CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    ImageView searchBtn;
    @BindView(R.id.management_mainfragment_recycler)
    RecyclerView recyclerView;
    private AllergyAdapter allergyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).changeTitle("ALLERGY MANAGEMENT");
        setHasOptionsMenu(true);

        allergyAdapter = new AllergyAdapter(getActivity());
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
                dialog.setContentView(R.layout.allergy_popup);
                dialog.setCanceledOnTouchOutside(false);

                final CustomFontEditText causative = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_causative_et);
                final CustomFontRadioButton drugTypeYes = (CustomFontRadioButton) dialog.findViewById(R.id.allergy_popup_drugtype_rb_yes);
                final CustomFontEditText reaction = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_reaction_et);
                final CustomFontEditText firstExp = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_firsttime_et);
                CustomFontButton cancelBtn = (CustomFontButton) dialog.findViewById(R.id.allergy_popup_cancel_btn);
                CustomFontButton addBtn = (CustomFontButton) dialog.findViewById(R.id.allergy_popup_add_btn);

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(getActivity());
                mAwesomeValidation.addValidation(causative, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));
                mAwesomeValidation.addValidation(reaction, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAwesomeValidation.validate()) {
                            Allergy allergy = new Allergy();
                            allergy.allergy_causative = causative.getText().toString();
                            allergy.allergy_drug = drugTypeYes.isChecked() ? "yes" : "no";
                            allergy.allergy_reaction = reaction.getText().toString();
                            allergy.allergy_first_experience = firstExp.getText().toString();

                            allergyAdapter.addSingle(allergy);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

    }
}
