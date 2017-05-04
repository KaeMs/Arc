package com.med.fast.management.disease;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.Utils;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.DeleteConfirmEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.disease.api.DiseaseManagementCreateSubmitAPI;
import com.med.fast.management.disease.api.DiseaseManagementCreateSubmitAPIFunc;
import com.med.fast.management.disease.api.DiseaseManagementDeleteSubmitAPI;
import com.med.fast.management.disease.api.DiseaseManagementDeleteSubmitAPIFunc;
import com.med.fast.management.disease.diseaseinterface.DiseaseManagementCreateDeleteIntf;
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

public class DiseaseManagementAdapter extends FastBaseRecyclerAdapter implements DiseaseManagementCreateDeleteIntf {

    private final int PROGRESS = 0;
    private final int DISEASE = 1;
    private Context context;
    private List<DiseaseManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private StartActivityForResultInAdapterIntf startActivityForResultInAdapterIntf;
    private String userId;
    private boolean initial = false;
    private int year, month, day;

    public DiseaseManagementAdapter(Context context, boolean initial){
        super(true);
        userId = SharedPreferenceUtilities.getUserId(context);
        this.context = context;
        this.initial = initial;
    }

    public DiseaseManagementAdapter(Context context, StartActivityForResultInAdapterIntf intf, boolean initial){
        super(true);
        userId = SharedPreferenceUtilities.getUserId(context);
        this.context = context;
        this.startActivityForResultInAdapterIntf = intf;
        this.initial = initial;
    }

    public void addList(List<DiseaseManagementModel> dataset){
        for (DiseaseManagementModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(DiseaseManagementModel model){
        this.mDataset.add(model);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeProgress(){
        if (mDataset.size() > 0){
            if (mDataset.get(mDataset.size() - 1) == null){
                mDataset.remove(mDataset.size() - 1);
                notifyItemRemoved(mDataset.size());
            }
        }
    }
    
    public void submitItem(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.management_disease_popup);
        dialog.setCanceledOnTouchOutside(false);

        final CustomFontEditText diseaseName = (CustomFontEditText) dialog.findViewById(R.id.disease_popup_name);
        final CustomFontRadioButton hereditaryY = (CustomFontRadioButton) dialog.findViewById(R.id.disease_popup_hereditary_y_rb);
        final CustomFontEditText inheritedFrom = (CustomFontEditText) dialog.findViewById(R.id.disease_popup_inherited_from);

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(context);
        mAwesomeValidation.addValidation(diseaseName, RegexTemplate.NOT_EMPTY, context.getString(R.string.full_accident_details_required));

        // Setting Hereditary RB so when it is at "no", inheritance won't be added
        hereditaryY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                inheritedFrom.setEnabled(isChecked);
                if (isChecked){
                    mAwesomeValidation.addValidation(inheritedFrom, RegexTemplate.NOT_EMPTY, context.getString(R.string.disease_hereditary_inherited_from_required));
                } else {
                    mAwesomeValidation.clear();
                }
            }
        });

        final CustomFontRadioButton ongoingY = (CustomFontRadioButton) dialog.findViewById(R.id.disease_popup_currently_having_y_rb);
        final CustomFontTextView historicDate = (CustomFontTextView) dialog.findViewById(R.id.disease_popup_historic_date_tv);
        final Spinner approximateDateSpinner = (Spinner) dialog.findViewById(R.id.disease_popup_date_spinner);

        String[] approximates = context.getResources().getStringArray(R.array.accident_approximate_values);
        final ArrayAdapter<String> approximateSpinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, approximates);
        approximateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        approximateDateSpinner.setAdapter(approximateSpinnerAdapter);


        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        historicDate.setOnClickListener(new View.OnClickListener() {
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
                                historicDate.setText(date);
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
                    String diseaseNameString = diseaseName.getText().toString();
                    String diseaseHereditaryString = "";
                    String diseaseHereditaryCarriersString = "";
                    if (hereditaryY.isChecked()){
                        diseaseHereditaryString = "yes";
                        diseaseHereditaryCarriersString = inheritedFrom.getText().toString();
                    } else {
                        diseaseHereditaryString = "no";
                        diseaseHereditaryCarriersString = context.getString(R.string.sign_dash);
                    }
                    String historicDateString = historicDate.getText().toString();
                    String approximateDateString = approximateDateSpinner.getSelectedItemPosition() > 0 ?
                            approximateSpinnerAdapter.getItem(approximateDateSpinner.getSelectedItemPosition()) :
                            "default";

                    DiseaseManagementModel diseaseManagementModel = new DiseaseManagementModel();
                    diseaseManagementModel.setDisease_name(diseaseNameString);
                    diseaseManagementModel.setDisease_hereditary(diseaseHereditaryString);
                    diseaseManagementModel.setDisease_hereditary_carriers(diseaseHereditaryCarriersString);
                    diseaseManagementModel.setDisease_ongoing(ongoingY.isChecked() ? "yes" : "no");

                    diseaseManagementModel.setDate_last_visit("-");
                    if (!historicDate.getText().toString().equals("")){
                        diseaseManagementModel.setDate_historic(historicDate.getText().toString());
                    } else if (approximateDateSpinner.getSelectedItemPosition() > 0){
                        diseaseManagementModel.setDate_approximate(approximateSpinnerAdapter.getItem(approximateDateSpinner.getSelectedItemPosition()));
                    }
                    diseaseManagementModel.setDate_created(Utils.getCurrentDate());
                    diseaseManagementModel.setTag(diseaseNameString + String.valueOf(getItemCount()));
                    addSingle(diseaseManagementModel);

                    DiseaseManagementCreateSubmitAPI diseaseManagementCreateSubmitAPI = new DiseaseManagementCreateSubmitAPI();
                    diseaseManagementCreateSubmitAPI.data.query.disease_name = diseaseNameString;
                    diseaseManagementCreateSubmitAPI.data.query.user_id = userId;
                    diseaseManagementCreateSubmitAPI.data.query.is_hereditary = diseaseHereditaryString;
                    diseaseManagementCreateSubmitAPI.data.query.is_ongoing = ongoingY.isChecked() ? "yes" : "no";
                    diseaseManagementCreateSubmitAPI.data.query.history_date_text = historicDateString;
                    diseaseManagementCreateSubmitAPI.data.query.date = approximateDateString;
                    diseaseManagementCreateSubmitAPI.data.query.hereditary_carrier = diseaseHereditaryCarriersString;
                    diseaseManagementCreateSubmitAPI.data.query.tag = diseaseNameString + String.valueOf(getItemCount());

                    DiseaseManagementCreateSubmitAPIFunc diseaseManagementCreateSubmitAPIFunc = new DiseaseManagementCreateSubmitAPIFunc(context, diseaseManagementModel.getTag(), initial);
                    diseaseManagementCreateSubmitAPIFunc.setDelegate(DiseaseManagementAdapter.this);
                    diseaseManagementCreateSubmitAPIFunc.execute(diseaseManagementCreateSubmitAPI);

                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void reSubmitItem(int position){
        DiseaseManagementCreateSubmitAPI diseaseManagementCreateSubmitAPI = new DiseaseManagementCreateSubmitAPI();
        diseaseManagementCreateSubmitAPI.data.query.disease_name = mDataset.get(position).getDisease_name();
        diseaseManagementCreateSubmitAPI.data.query.user_id = userId;
        diseaseManagementCreateSubmitAPI.data.query.is_hereditary = mDataset.get(position).getDisease_hereditary();
        diseaseManagementCreateSubmitAPI.data.query.is_ongoing = mDataset.get(position).getDisease_ongoing();
        diseaseManagementCreateSubmitAPI.data.query.history_date_text = mDataset.get(position).getDate_historic();
        diseaseManagementCreateSubmitAPI.data.query.date = mDataset.get(position).getDate_approximate();
        diseaseManagementCreateSubmitAPI.data.query.hereditary_carrier = mDataset.get(position).getDisease_hereditary_carriers();

        DiseaseManagementCreateSubmitAPIFunc diseaseManagementCreateSubmitAPIFunc = new DiseaseManagementCreateSubmitAPIFunc(context, mDataset.get(position).getTag(), initial);
        diseaseManagementCreateSubmitAPIFunc.setDelegate(DiseaseManagementAdapter.this);
        diseaseManagementCreateSubmitAPIFunc.execute(diseaseManagementCreateSubmitAPI);
    }
    
    public void clearList(){
        if (mDataset.size() > 0){
            mDataset.clear();
        }
    }

    public void setFailLoad(boolean failLoad) {
        this.failLoad = failLoad;
        notifyItemChanged(getItemCount() - 1);
        if (!failLoad){
            removeProgress();
        }
    }

    // Update by model
    public void updateItem(DiseaseManagementModel item){
        for (int i = getItemCount() - 1; i > 0; i++){
            if (mDataset.get(i).getDisease_name().equals(item.getDisease_name()) &&
                    mDataset.get(i).getDisease_hereditary_carriers().equals(item.getDisease_hereditary_carriers())){
                item.setProgress_status(APIConstants.PROGRESS_NORMAL);
                mDataset.set(i, item);
                break;
            }
        }
    }

    // Update by tag
    public void updateItem(String tag, String newId, boolean success) {
        for (int i = getItemCount() - 1; i > 0; i++) {
            if (mDataset.get(i).getTag().equals(tag)) {
                if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_ADD)) {
                    if (success) {
                        mDataset.get(i).setDisease_id(newId);
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

    @Subscribe
    public void onDeleteConfirm(DeleteConfirmEvent deleteConfirmEvent){
        for (int i = 0; i < getItemCount(); i++){
            if (deleteConfirmEvent.deletionId.equals("disease" + mDataset.get(i).getDisease_id())){
                mDataset.get(i).setProgress_status(APIConstants.PROGRESS_DELETE);
                notifyItemChanged(i);

                DiseaseManagementDeleteSubmitAPI diseaseManagementDeleteSubmitAPI = new DiseaseManagementDeleteSubmitAPI();
                diseaseManagementDeleteSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(context);
                diseaseManagementDeleteSubmitAPI.data.query.disease_id = mDataset.get(i).getDisease_id();

                DiseaseManagementDeleteSubmitAPIFunc diseaseManagementDeleteSubmitAPIFunc = new DiseaseManagementDeleteSubmitAPIFunc(context, mDataset.get(i).getDisease_id());
                diseaseManagementDeleteSubmitAPIFunc.setDelegate(DiseaseManagementAdapter.this);
                diseaseManagementDeleteSubmitAPIFunc.execute(diseaseManagementDeleteSubmitAPI);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? DISEASE : PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == DISEASE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_accident_item_card, parent, false);
            viewHolder = new DiseaseManagementVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.infiscroll_progress_layout, parent, false);
            viewHolder = new InfiScrollProgressVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == DISEASE){
            DiseaseManagementVH diseaseManagementVH = (DiseaseManagementVH)holder;
            diseaseManagementVH.name.setText(mDataset.get(position).getDisease_name());
            diseaseManagementVH.isHereditaryTV.setText(mDataset.get(position).getDisease_hereditary());
            diseaseManagementVH.hereditaryCarriers.setText(mDataset.get(position).getDisease_hereditary_carriers());
            diseaseManagementVH.isOngoingTV.setText(mDataset.get(position).getDisease_ongoing());
            diseaseManagementVH.lastVisit.setText(mDataset.get(position).getDate_last_visit());
            diseaseManagementVH.historicDate.setText(mDataset.get(position).getDate_historic());
            diseaseManagementVH.approximateDate.setText(mDataset.get(position).getDate_approximate().equals("default")? 
                    context.getString(R.string.sign_dash) :
                    mDataset.get(position).getDate_approximate());
            diseaseManagementVH.createdDate.setText(mDataset.get(position).getDate_created());

            if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD)){
                diseaseManagementVH.statusProgressBar.setOnClickListener(null);
                diseaseManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                diseaseManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_DELETE)){
                diseaseManagementVH.statusProgressBar.setOnClickListener(null);
                diseaseManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                diseaseManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD_FAIL)){
                diseaseManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                diseaseManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_tosca));
                diseaseManagementVH.statusProgressBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reSubmitItem(holder.getAdapterPosition());
                    }
                });
            } else {
                diseaseManagementVH.statusProgressBar.setOnClickListener(null);
                diseaseManagementVH.statusProgressBar.setVisibility(View.GONE);
            }

            diseaseManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)){
                        createDeleteDialog(context, context.getString(R.string.disease_delete_confirmation), "disease" + mDataset.get(holder.getAdapterPosition()).getDisease_id());
                    }
                }
            });

        } else {
            InfiScrollProgressVH infiScrollProgressVH = (InfiScrollProgressVH)holder;
            infiScrollProgressVH.setFailLoad(failLoad);
            infiScrollProgressVH.failTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new LoadMoreEvent());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onFinishDiseaseManagementCreate(ResponseAPI responseAPI, String tag) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            DiseaseManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, DiseaseManagementCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                updateItem(tag, output.data.results.new_disease_id, true);
            } else {
                updateItem(tag, APIConstants.NO_ID, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            updateItem(tag, APIConstants.NO_ID, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity)context).forceLogout();
        } else {
            updateItem(tag, APIConstants.NO_ID, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishDiseaseManagementDelete(ResponseAPI responseAPI, String tag) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            DiseaseManagementDeleteSubmitAPI output = gson.fromJson(responseAPI.status_response, DiseaseManagementDeleteSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    if (output.data.query.disease_id.equals(mDataset.get(i).getDisease_id())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    }
                }
            } else {
                updateItem(tag, output.data.query.disease_id, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            updateItem(tag, APIConstants.NO_ID, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity)context).forceLogout();
        } else {
            updateItem(tag, APIConstants.NO_ID, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    static class DiseaseManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_status_progress_progressbar)
        ProgressBar statusProgressBar;
        @BindView(R.id.management_disease_item_name)
        CustomFontTextView name;
        @BindView(R.id.management_disease_item_hereditary)
        CustomFontTextView isHereditaryTV;
        @BindView(R.id.management_disease_item_hereditary_carriers)
        CustomFontTextView hereditaryCarriers;
        @BindView(R.id.management_disease_item_ongoing)
        CustomFontTextView isOngoingTV;
        @BindView(R.id.management_disease_item_injury_date_last_visit)
        CustomFontTextView lastVisit;
        @BindView(R.id.management_disease_item_injury_date_historic)
        CustomFontTextView historicDate;
        @BindView(R.id.management_disease_item_injury_date_approximate)
        CustomFontTextView approximateDate;
        @BindView(R.id.management_disease_item_injury_created_date)
        CustomFontTextView createdDate;
        @BindView(R.id.management_operations_edit_btn)
        ImageView editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageView deleteBtn;


        public DiseaseManagementVH(View view) {
            super(view);

        }
    }
}
