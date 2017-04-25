package com.med.fast.management.accidenthistory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.med.fast.Constants;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;

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

public class AccidentHistoryManagementFragment extends FastBaseFragment {
    @BindView(R.id.management_mainfragment_search_edittxt)
    CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    ImageView searchBtn;
    @BindView(R.id.management_mainfragment_recycler)
    RecyclerView recyclerView;
    private AccidentHistoryManagementAdapter accidentHistoryManagementAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).changeTitle("ACCIDENT MANAGEMENT");
        setHasOptionsMenu(true);

        accidentHistoryManagementAdapter = new AccidentHistoryManagementAdapter(getActivity());
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

                            accidentHistoryManagementAdapter.addSingle(accidentHistoryManagementModel);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }
}
