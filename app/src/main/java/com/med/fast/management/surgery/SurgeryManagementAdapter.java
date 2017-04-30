package com.med.fast.management.surgery;

import android.app.Dialog;
import android.content.Context;
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
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.surgery.api.SurgeryManagementCreateSubmitAPI;
import com.med.fast.management.surgery.api.SurgeryManagementCreateSubmitAPIFunc;
import com.med.fast.management.surgery.api.SurgeryManagementDeleteSubmitAPI;
import com.med.fast.management.surgery.surgeryinterface.SurgeryManagementCreateDeleteIntf;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class SurgeryManagementAdapter extends FastBaseRecyclerAdapter implements SurgeryManagementCreateDeleteIntf {

    private final int PROGRESS = 0;
    private final int SURGERY = 1;
    private Context context;
    private List<SurgeryManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private String userId;

    public SurgeryManagementAdapter(Context context){
        super(false);
        this.context = context;
        this.userId = SharedPreferenceUtilities.getUserId(context);
    }

    public void addList(List<SurgeryManagementModel> dataset){
        for (SurgeryManagementModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(SurgeryManagementModel accident){
        this.mDataset.add(accident);
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
    public void updateItem(SurgeryManagementModel item) {
        for (int i = getItemCount() - 1; i > 0; i++) {
            if (mDataset.get(i).getSurgery_id().equals(item.getSurgery_id())) {
                item.setProgress_status(APIConstants.PROGRESS_NORMAL);
                mDataset.set(i, item);
                break;
            }
        }
    }

    // Update by tag
    public void updateItem(String tag, boolean success) {
        for (int i = getItemCount() - 1; i > 0; i++) {
            if (mDataset.get(i).getTag().equals(tag)) {
                if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_ADD)){
                    if (success)mDataset.get(i).setProgress_status(APIConstants.PROGRESS_NORMAL);
                    else mDataset.get(i).setProgress_status(APIConstants.PROGRESS_ADD_FAIL);
                } else if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_DELETE)){
                    mDataset.get(i).setProgress_status(APIConstants.PROGRESS_NORMAL);
                }
                break;
            }
        }
    }

    public void submitItem(){
        final Dialog dialog = new Dialog(context);
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
        mAwesomeValidation.setContext(context);
        mAwesomeValidation.addValidation(surgeryProcedure, RegexTemplate.NOT_EMPTY, context.getString(R.string.surgery_procedure_required));
        mAwesomeValidation.addValidation(hospitalName, RegexTemplate.NOT_EMPTY, context.getString(R.string.hospital_name_required));
        mAwesomeValidation.addValidation(physicianName, RegexTemplate.NOT_EMPTY, context.getString(R.string.physician_name_required));

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (!surgeryDate.getText().toString().equals("")){
                    if (mAwesomeValidation.validate()){
                        String surgeryProcedureString = surgeryProcedure.getText().toString();
                        String physicianNameString = physicianName.getText().toString();
                        String surgeryDateString = surgeryDate.getText().toString();
                        String hospitalNameString = hospitalName.getText().toString();
                        String surgeryNoteString = surgeryNote.getText().toString();
                        SurgeryManagementModel surgeryManagementModel = new SurgeryManagementModel();
                        surgeryManagementModel.setSurgery_procedure(surgeryProcedureString);
                        surgeryManagementModel.setSurgery_physician_name(physicianNameString);
                        surgeryManagementModel.setSurgery_hospital_name(hospitalNameString);
                        surgeryManagementModel.setSurgery_date(surgeryDateString);
                        surgeryManagementModel.setSurgery_note(surgeryNoteString);
                        surgeryManagementModel.setTag(surgeryProcedureString + String.valueOf(getItemCount()));

                        addSingle(surgeryManagementModel);

                        SurgeryManagementCreateSubmitAPI surgeryManagementCreateSubmitAPI = new SurgeryManagementCreateSubmitAPI();
                        surgeryManagementCreateSubmitAPI.data.query.user_id = userId;
                        surgeryManagementCreateSubmitAPI.data.query.procedure = surgeryProcedureString;
                        surgeryManagementCreateSubmitAPI.data.query.physician = physicianNameString;
                        surgeryManagementCreateSubmitAPI.data.query.date = surgeryDateString;
                        surgeryManagementCreateSubmitAPI.data.query.hospital = hospitalNameString;
                        surgeryManagementCreateSubmitAPI.data.query.notes = surgeryNoteString;

                        SurgeryManagementCreateSubmitAPIFunc surgeryManagementCreateSubmitAPIFunc = new SurgeryManagementCreateSubmitAPIFunc(context, surgeryManagementModel.getTag());
                        surgeryManagementCreateSubmitAPIFunc.setDelegate(SurgeryManagementAdapter.this);
                        surgeryManagementCreateSubmitAPIFunc.execute(surgeryManagementCreateSubmitAPI);
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.surgery_date_required), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void reSubmitItem(int position){
        SurgeryManagementCreateSubmitAPI surgeryManagementCreateSubmitAPI = new SurgeryManagementCreateSubmitAPI();
        surgeryManagementCreateSubmitAPI.data.query.user_id = userId;
        surgeryManagementCreateSubmitAPI.data.query.procedure = mDataset.get(position).getSurgery_procedure();
        surgeryManagementCreateSubmitAPI.data.query.physician = mDataset.get(position).getSurgery_physician_name();
        surgeryManagementCreateSubmitAPI.data.query.date = mDataset.get(position).getSurgery_date();
        surgeryManagementCreateSubmitAPI.data.query.hospital = mDataset.get(position).getSurgery_hospital_name();
        surgeryManagementCreateSubmitAPI.data.query.notes = mDataset.get(position).getSurgery_note();

        SurgeryManagementCreateSubmitAPIFunc surgeryManagementCreateSubmitAPIFunc = new SurgeryManagementCreateSubmitAPIFunc(context, mDataset.get(position).getTag());
        surgeryManagementCreateSubmitAPIFunc.setDelegate(SurgeryManagementAdapter.this);
        surgeryManagementCreateSubmitAPIFunc.execute(surgeryManagementCreateSubmitAPI);        
    }
    
    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? SURGERY : PROGRESS;
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == SURGERY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_allergy_item_card, parent, false);
            viewHolder = new SurgeryManagementVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.infiscroll_progress_layout, parent, false);
            viewHolder = new InfiScrollProgressVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == SURGERY) {
            SurgeryManagementVH surgeryManagementVH = (SurgeryManagementVH)holder;
            surgeryManagementVH.surgeryDate.setText(mDataset.get(position).getSurgery_date());
            surgeryManagementVH.surgeryProcedure.setText(mDataset.get(position).getSurgery_procedure());
            surgeryManagementVH.physicianName.setText(mDataset.get(position).getSurgery_physician_name());
            surgeryManagementVH.hospitalName.setText(mDataset.get(position).getSurgery_hospital_name());

            if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD)){
                surgeryManagementVH.statusProgressBar.setOnClickListener(null);
                surgeryManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                surgeryManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_DELETE)){
                surgeryManagementVH.statusProgressBar.setOnClickListener(null);
                surgeryManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                surgeryManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD_FAIL)){
                surgeryManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                surgeryManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_tosca));
                surgeryManagementVH.statusProgressBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reSubmitItem(holder.getAdapterPosition());
                    }
                });
            } else {
                surgeryManagementVH.statusProgressBar.setOnClickListener(null);
                surgeryManagementVH.statusProgressBar.setVisibility(View.GONE);
            }

            surgeryManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)){
                        createDeleteDialog(context, context.getString(R.string.allergy_delete_confirmation), "surgery" + mDataset.get(holder.getAdapterPosition()).getSurgery_id());
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
    public void onFinishSurgeryManagementCreate(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SurgeryManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, SurgeryManagementCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                updateItem(tag, true);
            } else {
                updateItem(tag, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if (responseAPI.status_code == 504) {
            updateItem(tag, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity) context).forceLogout();
        } else {
            updateItem(tag, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishSurgeryManagementDelete(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SurgeryManagementDeleteSubmitAPI output = gson.fromJson(responseAPI.status_response, SurgeryManagementDeleteSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    if (output.data.query.surgery_id.equals(mDataset.get(i).getSurgery_id())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    }
                }
            } else {
                updateItem(tag, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if (responseAPI.status_code == 504) {
            updateItem(tag, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity) context).forceLogout();
        } else {
            updateItem(tag, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    static class SurgeryManagementVH extends FastBaseViewHolder {
        @BindView(R.id.management_medicine_item_card_progress)
        ProgressBar statusProgressBar;
        @BindView(R.id.management_surgery_item_surgery_date)
        CustomFontTextView surgeryDate;
        @BindView(R.id.management_surgery_item_surgery_procedure)
        CustomFontTextView surgeryProcedure;
        @BindView(R.id.management_surgery_item_physician_name)
        CustomFontTextView physicianName;
        @BindView(R.id.management_surgery_item_hospital_name)
        CustomFontTextView hospitalName;

        @BindView(R.id.management_operations_edit_btn)
        ImageView editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageView deleteBtn;


        public SurgeryManagementVH(View view) {
            super(view);

        }
    }
}
