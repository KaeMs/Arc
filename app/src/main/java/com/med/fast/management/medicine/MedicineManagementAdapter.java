package com.med.fast.management.medicine;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
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
import com.med.fast.management.medicine.api.MedicineManagementDeleteAPI;
import com.med.fast.management.medicine.api.MedicineManagementDeleteAPIFunc;
import com.med.fast.management.medicine.api.MedicineManagementSubmitAPI;
import com.med.fast.management.medicine.api.MedicineManagementSubmitAPIFunc;
import com.med.fast.management.medicine.medicineinterface.MedicineCreateDeleteIntf;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class MedicineManagementAdapter extends FastBaseRecyclerAdapter implements MedicineCreateDeleteIntf {

    private final int PROGRESS = 0;
    private final int MEDICINE = 1;
    private Context context;
    private List<MedicineManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private StartActivityForResultInAdapterIntf startActivityForResultInAdapterIntf;
    private String userId;
    private boolean initial = false;

    public MedicineManagementAdapter(Context context, boolean initial){
        super(true);
        this.context = context;
        this.userId = SharedPreferenceUtilities.getUserId(context);
        this.initial = initial;
    }

    public MedicineManagementAdapter(Context context, StartActivityForResultInAdapterIntf intf, boolean initial){
        super(true);
        this.context = context;
        this.userId = SharedPreferenceUtilities.getUserId(context);
        this.startActivityForResultInAdapterIntf = intf;
        this.initial = initial;
    }

    public void addList(List<MedicineManagementModel> dataset){
        for (MedicineManagementModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(MedicineManagementModel accident){
        this.mDataset.add(accident);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addSingle(MedicineManagementModel accident, int position){
        this.mDataset.add(position, accident);
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

    public void clearList(){
        if (mDataset.size() > 0){
            mDataset.clear();
            notifyDataSetChanged();
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
    public void updateItem(MedicineManagementModel item) {
        for (int i = 0; i < getItemCount(); i++) {
            if (mDataset.get(i).getId().equals(item.getId())) {
                item.setProgress_status(APIConstants.PROGRESS_NORMAL);
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

    public void submitItem(){
        final Dialog dialog = new Dialog(context);
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

        CustomFontButton backBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_back_btn);
        CustomFontButton createBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_create_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(context);
        mAwesomeValidation.addValidation(medicineName, RegexTemplate.NOT_EMPTY, context.getString(R.string.medicine_name_required));
        mAwesomeValidation.addValidation(medicineForm, RegexTemplate.NOT_EMPTY, context.getString(R.string.medicine_form_required));

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (mAwesomeValidation.validate()){
                    String nameString = medicineName.getText().toString();
                    String routeString = Utils.processStringForAPI(administrationMethod.getText().toString());
                    String formString = medicineForm.getText().toString();
                    String doseString = Utils.processStringForAPI(administrationDose.getText().toString());
                    String frequencyString = Utils.processStringForAPI(frequency.getText().toString());
                    String reasonString = Utils.processStringForAPI(reason.getText().toString());
                    String statusString = Utils.processStringForAPI(medicationStatus.getText().toString());
                    String additionalInstructionString = Utils.processStringForAPI(additionalInstruction.getText().toString());

                    MedicineManagementModel medicineManagementModel = new MedicineManagementModel();
                    medicineManagementModel.setName(nameString);
                    medicineManagementModel.setForm(formString);
                    medicineManagementModel.setAdministration_method(routeString);
                    medicineManagementModel.setAdministration_dose(doseString);
                    medicineManagementModel.setFrequency(frequencyString);
                    medicineManagementModel.setMedication_reason(reasonString);
                    medicineManagementModel.setMedication_status(statusString);
                    medicineManagementModel.setAdditional_instruction(additionalInstructionString);
                    medicineManagementModel.setTag(nameString + String.valueOf(getItemCount()));
                    addSingle(medicineManagementModel, 0);

                    MedicineManagementSubmitAPI medicineManagementSubmitAPI = new MedicineManagementSubmitAPI();
                    medicineManagementSubmitAPI.data.query.user_id = userId;
                    medicineManagementSubmitAPI.data.query.name = nameString;
                    medicineManagementSubmitAPI.data.query.route = routeString;
                    medicineManagementSubmitAPI.data.query.form = formString;
                    medicineManagementSubmitAPI.data.query.dose = doseString;
                    medicineManagementSubmitAPI.data.query.frequency = frequencyString;
                    medicineManagementSubmitAPI.data.query.reason = reasonString;
                    medicineManagementSubmitAPI.data.query.status = statusString;
                    medicineManagementSubmitAPI.data.query.additional_instruction = additionalInstructionString;

                    MedicineManagementSubmitAPIFunc medicineManagementSubmitAPIFunc = new MedicineManagementSubmitAPIFunc(context, medicineManagementModel.getTag(), initial);
                    medicineManagementSubmitAPIFunc.setDelegate(MedicineManagementAdapter.this);
                    medicineManagementSubmitAPIFunc.execute(medicineManagementSubmitAPI);

                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void reSubmitItem(int position){
        MedicineManagementSubmitAPI medicineManagementSubmitAPI = new MedicineManagementSubmitAPI();
        medicineManagementSubmitAPI.data.query.user_id = userId;
        medicineManagementSubmitAPI.data.query.name = mDataset.get(position).getName();
        medicineManagementSubmitAPI.data.query.route = mDataset.get(position).getAdministration_method();
        medicineManagementSubmitAPI.data.query.form = mDataset.get(position).getForm();
        medicineManagementSubmitAPI.data.query.dose = mDataset.get(position).getAdministration_dose();
        medicineManagementSubmitAPI.data.query.frequency = mDataset.get(position).getFrequency();
        medicineManagementSubmitAPI.data.query.reason = mDataset.get(position).getMedication_reason();
        medicineManagementSubmitAPI.data.query.status = mDataset.get(position).getMedication_status();
        medicineManagementSubmitAPI.data.query.additional_instruction = mDataset.get(position).getAdditional_instruction();

        MedicineManagementSubmitAPIFunc medicineManagementSubmitAPIFunc = new MedicineManagementSubmitAPIFunc(context, mDataset.get(position).getTag(), initial);
        medicineManagementSubmitAPIFunc.setDelegate(MedicineManagementAdapter.this);
        medicineManagementSubmitAPIFunc.execute(medicineManagementSubmitAPI);
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? MEDICINE : PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == MEDICINE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_medicine_item_card, parent, false);
            viewHolder = new MedicineManagementVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.infiscroll_progress_layout, parent, false);
            viewHolder = new InfiScrollProgressVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == MEDICINE){
            MedicineManagementVH medicineManagementVH = (MedicineManagementVH)holder;
            medicineManagementVH.medicineName.setText(mDataset.get(position).getName());
            medicineManagementVH.medicineForm.setText(mDataset.get(position).getForm());
            medicineManagementVH.administrationMethod.setText(mDataset.get(position).getAdministration_method());
            medicineManagementVH.administrationDose.setText(mDataset.get(position).getAdministration_dose());
            medicineManagementVH.frequency.setText(mDataset.get(position).getFrequency());
            medicineManagementVH.createdDate.setText(mDataset.get(position).getCreated_date());

            if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD)){
                medicineManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                medicineManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_DELETE)){
                medicineManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                medicineManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD_FAIL)){
                medicineManagementVH.statusProgressBar.setVisibility(View.GONE);
                medicineManagementVH.progressFailImg.setVisibility(View.VISIBLE);
                medicineManagementVH.progressFailImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reSubmitItem(holder.getAdapterPosition());
                    }
                });
            } else {
                medicineManagementVH.statusProgressBar.setVisibility(View.GONE);
            }

            medicineManagementVH.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)) {
                        Intent intent = new Intent(context, MedicineEditActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra(ConstantsManagement.MEDICINE_MODEL_EXTRA,
                                gson.toJson(mDataset.get(holder.getAdapterPosition())));
                        startActivityForResultInAdapterIntf.onStartActivityForResult(intent, RequestCodeList.MEDICINE_EDIT);
                    }
                }
            });

            medicineManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)){
                        createDeleteDialog(context, context.getString(R.string.allergy_delete_confirmation), "medicine" + mDataset.get(holder.getAdapterPosition()).getId());
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

    @Subscribe
    public void onDeleteConfirm(DeleteConfirmEvent deleteConfirmEvent) {
        for (int i = 0; i < getItemCount(); i++) {
            if (deleteConfirmEvent.deletionId.equals("medicine" + mDataset.get(i).getId())) {
                mDataset.get(i).setProgress_status(APIConstants.PROGRESS_DELETE);
                notifyItemChanged(i);

                MedicineManagementDeleteAPI medicineManagementDeleteAPI = new MedicineManagementDeleteAPI();
                medicineManagementDeleteAPI.data.query.user_id = userId;
                medicineManagementDeleteAPI.data.query.medicine_id = mDataset.get(i).getId();

                MedicineManagementDeleteAPIFunc medicineManagementDeleteAPIFunc = new MedicineManagementDeleteAPIFunc(context, mDataset.get(i).getId());
                medicineManagementDeleteAPIFunc.setDelegate(MedicineManagementAdapter.this);
                medicineManagementDeleteAPIFunc.execute(medicineManagementDeleteAPI);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onFinishMedicineCreate(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            MedicineManagementSubmitAPI output = gson.fromJson(responseAPI.status_response, MedicineManagementSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                updateItem(tag, output.data.results.new_medicine_id, true);
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
    public void onFinishMedicineDelete(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            MedicineManagementDeleteAPI output = gson.fromJson(responseAPI.status_response, MedicineManagementDeleteAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    if (output.data.query.medicine_id.equals(mDataset.get(i).getId())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    }
                }
            } else {
                updateItem(tag, output.data.query.medicine_id, false);
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

    static class MedicineManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_status_progress_progressbar)
        ProgressBar statusProgressBar;
        @BindView(R.id.management_status_progress_fail)
        ImageView progressFailImg;
        @BindView(R.id.management_medicine_item_medicine_name)
        CustomFontTextView medicineName;
        @BindView(R.id.management_medicine_item_medicine_form)
        CustomFontTextView medicineForm;
        @BindView(R.id.management_medicine_item_administration_method)
        CustomFontTextView administrationMethod;
        @BindView(R.id.management_medicine_item_administration_dose)
        CustomFontTextView administrationDose;
        @BindView(R.id.management_medicine_item_frequency)
        CustomFontTextView frequency;
        @BindView(R.id.management_medicine_item_created_date)
        CustomFontTextView createdDate;

        @BindView(R.id.management_operations_edit_btn)
        ImageView editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageView deleteBtn;


        public MedicineManagementVH(View view) {
            super(view);

        }
    }
}
