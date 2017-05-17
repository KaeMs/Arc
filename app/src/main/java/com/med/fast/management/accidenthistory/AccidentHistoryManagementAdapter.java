package com.med.fast.management.accidenthistory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.Utils;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.DeleteConfirmEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.accidenthistory.accidentinterface.AccidentHistoryCreateDeleteIntf;
import com.med.fast.management.accidenthistory.api.AccidentHistoryCreateSubmitAPI;
import com.med.fast.management.accidenthistory.api.AccidentHistoryCreateSubmitAPIFunc;
import com.med.fast.management.accidenthistory.api.AccidentHistoryDeleteSubmitAPI;
import com.med.fast.management.accidenthistory.api.AccidentHistoryDeleteSubmitAPIFunc;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class AccidentHistoryManagementAdapter extends FastBaseRecyclerAdapter implements AccidentHistoryCreateDeleteIntf {

    private final int PROGRESS = 0;
    private final int ACCIDENT = 1;
    private Context context;
    private List<AccidentHistoryManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private StartActivityForResultInAdapterIntf startActivityForResultInAdapterIntf;
    private String userId;
    private int year, month, day;

    public AccidentHistoryManagementAdapter(Context context, StartActivityForResultInAdapterIntf intf) {
        super(true);
        this.context = context;
        this.userId = SharedPreferenceUtilities.getUserId(context);
        this.startActivityForResultInAdapterIntf = intf;
    }

    public void addList(List<AccidentHistoryManagementModel> dataset) {
        for (AccidentHistoryManagementModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(AccidentHistoryManagementModel accident) {
        this.mDataset.add(accident);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addSingle(AccidentHistoryManagementModel accident, int position) {
        this.mDataset.add(position, accident);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeProgress() {
        if (mDataset.size() > 0) {
            if (mDataset.get(mDataset.size() - 1) == null) {
                mDataset.remove(mDataset.size() - 1);
                notifyItemRemoved(mDataset.size());
            }
        }
    }

    public void clearList() {
        if (mDataset.size() > 0) {
            mDataset.clear();
            notifyDataSetChanged();
        }
    }

    public void setFailLoad(boolean failLoad) {
        this.failLoad = failLoad;
        notifyItemChanged(getItemCount() - 1);
        if (!failLoad) {
            removeProgress();
        }
    }

    public void submitItem() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.management_accident_popup);
        dialog.setCanceledOnTouchOutside(false);

        final CustomFontEditText accidentDetails = (CustomFontEditText) dialog.findViewById(R.id.accident_popup_accident_details);
        final CustomFontEditText injuryNature = (CustomFontEditText) dialog.findViewById(R.id.accident_popup_injury_nature);
        final CustomFontEditText injuryLocation = (CustomFontEditText) dialog.findViewById(R.id.accident_popup_injury_location);
        final CustomFontTextView accidentDateTV = (CustomFontTextView) dialog.findViewById(R.id.accident_popup_accident_date_tv);
        final Spinner accidentDateSpinner = (Spinner) dialog.findViewById(R.id.accident_popup_accident_date_spinner);
        final CustomFontTextView injuryDateCustomTV = (CustomFontTextView) dialog.findViewById(R.id.accident_popup_accident_other_date);
        String[] approximates = context.getResources().getStringArray(R.array.accident_approximate_values);
        final ArrayAdapter<String> accidentSpinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, approximates);
        accidentSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accidentDateSpinner.setAdapter(accidentSpinnerAdapter);

        accidentDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == accidentSpinnerAdapter.getCount() - 1){
                    injuryDateCustomTV.setVisibility(View.VISIBLE);
                } else {
                    injuryDateCustomTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        accidentDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(context, null, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.getDatePicker().updateDate(year, month, day);
                datePickerDialog.show();
                datePickerDialog.setCanceledOnTouchOutside(true);

                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                year = datePickerDialog.getDatePicker().getYear();
                                month = datePickerDialog.getDatePicker().getMonth();
                                day = datePickerDialog.getDatePicker().getDayOfMonth();
                                // Formatting date from MM to MMM
                                SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy", Locale.getDefault());
                                Date newDate = null;
                                try {
                                    newDate = format.parse(String.valueOf(month + 1) + " " + String.valueOf(day) + " " + String.valueOf(year));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                format = new SimpleDateFormat(Constants.dateFormatComma, Locale.getDefault());
                                String date = format.format(newDate);
                                accidentDateTV.setText(date);
                            }
                        });

                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel),
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
        mAwesomeValidation.setContext(context);
        mAwesomeValidation.addValidation(accidentDetails, RegexTemplate.NOT_EMPTY, context.getString(R.string.full_accident_details_required));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (mAwesomeValidation.validate()) {
                    String detail = Utils.processStringForAPI(accidentDetails.getText().toString());
                    String injuryNatureString = Utils.processStringForAPI(injuryNature.getText().toString());
                    String injuryLocationString = Utils.processStringForAPI(injuryLocation.getText().toString());
                    String accidentDate = Utils.processStringForAPI(accidentDateTV.getText().toString());
                    String accidentDateTmp;
                    if (accidentDateSpinner.getSelectedItemPosition() > 0) {
                        accidentDateTmp = accidentSpinnerAdapter.getItem(accidentDateSpinner.getSelectedItemPosition());
                    } else {
                        accidentDateTmp = APIConstants.DEFAULT;
                    }
                    String injuryDateCustom = Utils.processStringForAPI(injuryDateCustomTV.getText().toString());
                    AccidentHistoryManagementModel accidentHistoryManagementModel = new AccidentHistoryManagementModel();
                    accidentHistoryManagementModel.setDetail(detail);
                    accidentHistoryManagementModel.setInjury_nature(injuryNatureString);
                    accidentHistoryManagementModel.setInjury_location(injuryLocationString);
                    accidentHistoryManagementModel.setInjury_date(accidentDate);
                    accidentHistoryManagementModel.setInjury_date_tmp(accidentDateTmp);
                    accidentHistoryManagementModel.setInjury_date_custom(injuryDateCustom);
                    accidentHistoryManagementModel.setCreated_date(Utils.getCurrentDate());
                    accidentHistoryManagementModel.setProgress_status(APIConstants.PROGRESS_ADD);

                    addSingle(accidentHistoryManagementModel, 0);

                    AccidentHistoryCreateSubmitAPI accidentHistoryCreateSubmitAPI = new AccidentHistoryCreateSubmitAPI();
                    accidentHistoryCreateSubmitAPI.data.query.user_id = userId;
                    accidentHistoryCreateSubmitAPI.data.query.detail = detail;
                    accidentHistoryCreateSubmitAPI.data.query.injury_nature = injuryNatureString;
                    accidentHistoryCreateSubmitAPI.data.query.injury_location = injuryLocationString;
                    accidentHistoryCreateSubmitAPI.data.query.injury_date = accidentDate;
                    accidentHistoryCreateSubmitAPI.data.query.injury_date_tmp = accidentDateTmp;
                    accidentHistoryCreateSubmitAPI.data.query.injury_date_custom = injuryDateCustom;
                    accidentHistoryCreateSubmitAPI.data.query.tag = detail + String.valueOf(getItemCount());

                    AccidentHistoryCreateSubmitAPIFunc accidentHistoryCreateSubmitAPIFunc =
                            new AccidentHistoryCreateSubmitAPIFunc(context, AccidentHistoryManagementAdapter.this, detail + String.valueOf(getItemCount()));
                    accidentHistoryCreateSubmitAPIFunc.execute(accidentHistoryCreateSubmitAPI);
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }
        });

        dialog.show();
    }

    private void reSubmitItem(int position) {
        AccidentHistoryCreateSubmitAPI accidentHistoryCreateSubmitAPI = new AccidentHistoryCreateSubmitAPI();
        accidentHistoryCreateSubmitAPI.data.query.user_id = userId;
        accidentHistoryCreateSubmitAPI.data.query.detail = mDataset.get(position).getDetail();
        accidentHistoryCreateSubmitAPI.data.query.injury_nature = mDataset.get(position).getInjury_nature();
        accidentHistoryCreateSubmitAPI.data.query.injury_location = mDataset.get(position).getInjury_location();
        accidentHistoryCreateSubmitAPI.data.query.injury_date = mDataset.get(position).getInjury_date();
        accidentHistoryCreateSubmitAPI.data.query.injury_date_tmp = mDataset.get(position).getInjury_date_tmp();
        accidentHistoryCreateSubmitAPI.data.query.injury_date_custom = "default";
        accidentHistoryCreateSubmitAPI.data.query.tag = mDataset.get(position).getTag();
        mDataset.get(position).setProgress_status("1");

        AccidentHistoryCreateSubmitAPIFunc accidentHistoryCreateSubmitAPIFunc = new
                AccidentHistoryCreateSubmitAPIFunc(context, AccidentHistoryManagementAdapter.this, mDataset.get(position).getTag());
        accidentHistoryCreateSubmitAPIFunc.execute(accidentHistoryCreateSubmitAPI);
    }

    // Update by model
    public void updateItem(AccidentHistoryManagementModel item) {
        for (int i = 0; i < getItemCount(); i++) {
            if (mDataset.get(i).getId().equals(item.getId())) {
                item.setProgress_status("0");
                mDataset.set(i, item);
                notifyItemChanged(i);
                break;
            }
        }
    }

    // Update by tag
    public void updateItem(String tag, String newId, boolean success) {
        for (int i = 0; i < getItemCount(); i++) {
            if (mDataset.get(i).getTag().equals(tag)) {
                if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_ADD)) {
                    if (success) {
                        mDataset.get(i).setId(newId);
                        mDataset.get(i).setProgress_status(APIConstants.PROGRESS_NORMAL);
                    } else {
                        mDataset.get(i).setProgress_status(APIConstants.PROGRESS_ADD_FAIL);
                    }
                } else if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_DELETE)) {
                    if (success) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    } else {
                        mDataset.get(i).setProgress_status(APIConstants.PROGRESS_NORMAL);
                    }
                }
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? ACCIDENT : PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == ACCIDENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_accident_item_card, parent, false);
            viewHolder = new AccidentManagementVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.infiscroll_progress_layout, parent, false);
            viewHolder = new InfiScrollProgressVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ACCIDENT) {
            AccidentManagementVH accidentManagementVH = (AccidentManagementVH) holder;
            accidentManagementVH.details.setText(mDataset.get(position).getDetail());
            accidentManagementVH.injuryNature.setText(mDataset.get(position).getInjury_nature());
            accidentManagementVH.injuryLocation.setText(mDataset.get(position).getInjury_location());
            accidentManagementVH.injuryDate.setText(mDataset.get(position).getInjury_date());
            accidentManagementVH.createdDate.setText(mDataset.get(position).getCreated_date());

            if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD)) {
                accidentManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                accidentManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_DELETE)) {
                accidentManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                accidentManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD_FAIL)) {
                accidentManagementVH.statusProgressBar.setVisibility(View.GONE);
                accidentManagementVH.progressFailImg.setVisibility(View.VISIBLE);
                accidentManagementVH.progressFailImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reSubmitItem(holder.getAdapterPosition());
                    }
                });
            } else {
                accidentManagementVH.statusProgressBar.setVisibility(View.GONE);
            }

            accidentManagementVH.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)) {
                        Intent intent = new Intent(context, AccidentEditActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra(ConstantsManagement.ACCIDENT_MODEL_EXTRA,
                                gson.toJson(mDataset.get(holder.getAdapterPosition())));
                        startActivityForResultInAdapterIntf.onStartActivityForResult(intent, RequestCodeList.ACCIDENT_EDIT);
                    }
                }
            });

            accidentManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)) {
                        createDeleteDialog(context, context.getString(R.string.accident_delete_confirmation), "accident" + mDataset.get(holder.getAdapterPosition()).getId());
                    }
                }
            });

        } else {
            InfiScrollProgressVH infiScrollProgressVH = (InfiScrollProgressVH) holder;
            infiScrollProgressVH.setFailLoad(failLoad);
            infiScrollProgressVH.failTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new LoadMoreEvent());
                }
            });
        }
    }

    @Subscribe
    public void onDeleteConfirm(DeleteConfirmEvent deleteConfirmEvent) {
        for (int i = 0; i < getItemCount(); i++) {
            if (deleteConfirmEvent.deletionId.equals("accident" + mDataset.get(i).getId())) {
                mDataset.get(i).setProgress_status(APIConstants.PROGRESS_DELETE);
                notifyItemChanged(i);

                AccidentHistoryDeleteSubmitAPI accidentHistoryDeleteSubmitAPI = new AccidentHistoryDeleteSubmitAPI();
                accidentHistoryDeleteSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(context);
                accidentHistoryDeleteSubmitAPI.data.query.accident_id = mDataset.get(i).getId();

                AccidentHistoryDeleteSubmitAPIFunc accidentHistoryDeleteSubmitAPIFunc = new AccidentHistoryDeleteSubmitAPIFunc(context, mDataset.get(i).getId());
                accidentHistoryDeleteSubmitAPIFunc.setDelegate(AccidentHistoryManagementAdapter.this);
                accidentHistoryDeleteSubmitAPIFunc.execute(accidentHistoryDeleteSubmitAPI);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onFinishAccidentHistoryCreateSubmit(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AccidentHistoryCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, AccidentHistoryCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                updateItem(tag, output.data.results.new_accident_id, true);
            } else {
                updateItem(tag, APIConstants.NO_ID, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if (responseAPI.status_code == 504) {
            updateItem(tag, APIConstants.NO_ID, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity) context).forceLogout();
        } else {
            updateItem(tag, APIConstants.NO_ID, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishAccidentHistoryDelete(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AccidentHistoryDeleteSubmitAPI output = gson.fromJson(responseAPI.status_response, AccidentHistoryDeleteSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    if (output.data.query.accident_id.equals(mDataset.get(i).getId())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    }
                }
            } else {
                updateItem(tag, output.data.query.accident_id, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if (responseAPI.status_code == 504) {
            updateItem(tag, APIConstants.NO_ID, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity) context).forceLogout();
        } else {
            updateItem(tag, APIConstants.NO_ID, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    static class AccidentManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_status_progress_progressbar)
        ProgressBar statusProgressBar;
        @BindView(R.id.management_status_progress_fail)
        ImageView progressFailImg;
        @BindView(R.id.management_accident_item_accident_details)
        CustomFontTextView details;
        @BindView(R.id.management_accident_item_injury_nature)
        CustomFontTextView injuryNature;
        @BindView(R.id.management_accident_item_injury_location)
        CustomFontTextView injuryLocation;
        @BindView(R.id.management_accident_item_injury_date)
        CustomFontTextView injuryDate;
        @BindView(R.id.management_accident_item_injury_created_date)
        CustomFontTextView createdDate;
        @BindView(R.id.management_operations_edit_btn)
        ImageView editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageView deleteBtn;


        public AccidentManagementVH(View view) {
            super(view);
        }
    }
}
