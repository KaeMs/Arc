package com.med.fast.management.surgery;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.med.fast.customevents.ItemAddedEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.DeleteConfirmIntf;
import com.med.fast.management.surgery.api.SurgeryManagementCreateSubmitAPI;
import com.med.fast.management.surgery.api.SurgeryManagementCreateSubmitAPIFunc;
import com.med.fast.management.surgery.api.SurgeryManagementDeleteSubmitAPI;
import com.med.fast.management.surgery.api.SurgeryManagementDeleteSubmitAPIFunc;
import com.med.fast.management.surgery.surgeryinterface.SurgeryManagementCreateDeleteIntf;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;

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

public class SurgeryManagementAdapter extends FastBaseRecyclerAdapter implements SurgeryManagementCreateDeleteIntf, DeleteConfirmIntf {

    private final int PROGRESS = 0;
    private final int SURGERY = 1;
    private Context context;
    private List<SurgeryManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private StartActivityForResultInAdapterIntf startActivityForResultInAdapterIntf;
    private String userId;
    private int year, month, day;

    public SurgeryManagementAdapter(Context context, StartActivityForResultInAdapterIntf intf) {
        this.context = context;
        this.userId = SharedPreferenceUtilities.getUserId(context);
        this.startActivityForResultInAdapterIntf = intf;
        deleteConfirmIntf = this;
    }

    public void addList(List<SurgeryManagementModel> dataset) {
        for (SurgeryManagementModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(SurgeryManagementModel accident) {
        this.mDataset.add(accident);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addSingle(SurgeryManagementModel accident, int position) {
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

    // Update by model
    public void updateItem(SurgeryManagementModel item) {
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
            if (!TextUtils.isEmpty(mDataset.get(i).getId()) &&
                    mDataset.get(i).getId().equals(tag)) {
                if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_DELETE)) {
                    if (success) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    } else {
                        mDataset.get(i).setProgress_status(APIConstants.PROGRESS_NORMAL);
                        notifyItemChanged(i);
                    }
                    break;
                }
            } else {
                if (!TextUtils.isEmpty(mDataset.get(i).getTag()) &&
                        mDataset.get(i).getTag().equals(tag)) {
                    if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_ADD)) {
                        if (success) {
                            mDataset.get(i).setId(newId);
                            mDataset.get(i).setProgress_status(APIConstants.PROGRESS_NORMAL);
                        } else {
                            mDataset.get(i).setProgress_status(APIConstants.PROGRESS_ADD_FAIL);
                        }
                        notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

    public void submitItem() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.management_surgery_popup);
        dialog.setCanceledOnTouchOutside(false);

        final CustomFontEditText surgeryProcedure = (CustomFontEditText) dialog.findViewById(R.id.surgery_popup_procedure);
        final CustomFontEditText physicianName = (CustomFontEditText) dialog.findViewById(R.id.surgery_popup_physician_name);
        final CustomFontEditText hospitalName = (CustomFontEditText) dialog.findViewById(R.id.surgery_popup_hospital_name);
        final CustomFontTextView surgeryDate = (CustomFontTextView) dialog.findViewById(R.id.surgery_popup_surgery_date);
        final CustomFontEditText surgeryNote = (CustomFontEditText) dialog.findViewById(R.id.surgery_popup_note);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        surgeryDate.setOnClickListener(new View.OnClickListener() {
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
                                surgeryDate.setText(date);
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

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(context);
        mAwesomeValidation.addValidation(surgeryProcedure, RegexTemplate.NOT_EMPTY, context.getString(R.string.surgery_procedure_required));

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (!surgeryDate.getText().toString().equals("")) {
                    if (mAwesomeValidation.validate()) {
                        String surgeryProcedureString = surgeryProcedure.getText().toString();
                        String physicianNameString = Utils.processStringForAPI(physicianName.getText().toString());
                        String surgeryDateString = surgeryDate.getText().toString();
                        String hospitalNameString = Utils.processStringForAPI(hospitalName.getText().toString());
                        String surgeryNoteString = Utils.processStringForAPI(surgeryNote.getText().toString());
                        SurgeryManagementModel surgeryManagementModel = new SurgeryManagementModel();
                        surgeryManagementModel.setProcedure(surgeryProcedureString);
                        surgeryManagementModel.setPhysician(physicianNameString);
                        surgeryManagementModel.setHospital(hospitalNameString);
                        surgeryManagementModel.setDate(surgeryDateString);
                        surgeryManagementModel.setNote(surgeryNoteString);
                        surgeryManagementModel.setTag(surgeryProcedureString + String.valueOf(getItemCount()));

                        mDataset.add(0, surgeryManagementModel);
                        notifyItemInserted(0);
                        EventBus.getDefault().post(new ItemAddedEvent());

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

    private void reSubmitItem(int position) {
        SurgeryManagementCreateSubmitAPI surgeryManagementCreateSubmitAPI = new SurgeryManagementCreateSubmitAPI();
        surgeryManagementCreateSubmitAPI.data.query.user_id = userId;
        surgeryManagementCreateSubmitAPI.data.query.procedure = mDataset.get(position).getProcedure();
        surgeryManagementCreateSubmitAPI.data.query.physician = mDataset.get(position).getPhysician();
        surgeryManagementCreateSubmitAPI.data.query.date = mDataset.get(position).getDate();
        surgeryManagementCreateSubmitAPI.data.query.hospital = mDataset.get(position).getHospital();
        surgeryManagementCreateSubmitAPI.data.query.notes = mDataset.get(position).getNote();

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
                    .inflate(R.layout.management_surgery_item_card, parent, false);
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
            SurgeryManagementVH surgeryManagementVH = (SurgeryManagementVH) holder;
            surgeryManagementVH.surgeryDate.setText(mDataset.get(position).getDate());
            surgeryManagementVH.surgeryProcedure.setText(mDataset.get(position).getProcedure());
            surgeryManagementVH.physicianName.setText(mDataset.get(position).getPhysician());
            surgeryManagementVH.hospitalName.setText(mDataset.get(position).getHospital());

            if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD)) {
                surgeryManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                surgeryManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
                surgeryManagementVH.editBtn.setEnabled(false);
                surgeryManagementVH.deleteBtn.setEnabled(false);
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_DELETE)) {
                surgeryManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                surgeryManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
                surgeryManagementVH.editBtn.setEnabled(false);
                surgeryManagementVH.deleteBtn.setEnabled(false);
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD_FAIL)) {
                surgeryManagementVH.statusProgressBar.setVisibility(View.GONE);
                surgeryManagementVH.progressFailImg.setVisibility(View.VISIBLE);
                surgeryManagementVH.progressFailImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reSubmitItem(holder.getAdapterPosition());
                    }
                });
                surgeryManagementVH.editBtn.setEnabled(false);
                surgeryManagementVH.deleteBtn.setEnabled(false);
            } else {
                surgeryManagementVH.statusProgressBar.setVisibility(View.GONE);
                surgeryManagementVH.editBtn.setEnabled(true);
                surgeryManagementVH.deleteBtn.setEnabled(true);
            }

            surgeryManagementVH.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)) {
                        Intent intent = new Intent(context, SurgeryEditActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra(ConstantsManagement.SURGERY_MODEL_EXTRA,
                                gson.toJson(mDataset.get(holder.getAdapterPosition())));
                        startActivityForResultInAdapterIntf.onStartActivityForResult(intent, RequestCodeList.SURGERY_EDIT);
                    }
                }
            });

            surgeryManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)) {
                        createDeleteDialog(context, context.getString(R.string.allergy_delete_confirmation), "surgery" + mDataset.get(holder.getAdapterPosition()).getId());
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

    @Override
    public void onDeleteConfirm(String deletionId) {
        for (int i = 0; i < getItemCount(); i++) {
            if (deletionId.equals("surgery" + mDataset.get(i).getId())) {
                mDataset.get(i).setProgress_status(APIConstants.PROGRESS_DELETE);
                notifyItemChanged(i);

                SurgeryManagementDeleteSubmitAPI surgeryManagementDeleteSubmitAPI = new SurgeryManagementDeleteSubmitAPI();
                surgeryManagementDeleteSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(context);
                surgeryManagementDeleteSubmitAPI.data.query.surgery_id = mDataset.get(i).getId();

                SurgeryManagementDeleteSubmitAPIFunc surgeryManagementDeleteSubmitAPIFunc =
                        new SurgeryManagementDeleteSubmitAPIFunc(context, mDataset.get(i).getId());
                surgeryManagementDeleteSubmitAPIFunc.setDelegate(SurgeryManagementAdapter.this);
                surgeryManagementDeleteSubmitAPIFunc.execute(surgeryManagementDeleteSubmitAPI);
                break;
            }
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
                updateItem(tag, output.data.results.new_surgery_id, true);
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
    public void onFinishSurgeryManagementDelete(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            SurgeryManagementDeleteSubmitAPI output = gson.fromJson(responseAPI.status_response, SurgeryManagementDeleteSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    if (output.data.query.surgery_id.equals(mDataset.get(i).getId())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    }
                }
            } else {
                updateItem(tag, output.data.query.surgery_id, false);
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

    static class SurgeryManagementVH extends FastBaseViewHolder {
        @BindView(R.id.management_status_progress_progressbar)
        ProgressBar statusProgressBar;
        @BindView(R.id.management_status_progress_fail)
        ImageView progressFailImg;
        @BindView(R.id.management_surgery_item_surgery_date)
        CustomFontTextView surgeryDate;
        @BindView(R.id.management_surgery_item_surgery_procedure)
        CustomFontTextView surgeryProcedure;
        @BindView(R.id.management_surgery_item_physician_name)
        CustomFontTextView physicianName;
        @BindView(R.id.management_surgery_item_hospital_name)
        CustomFontTextView hospitalName;

        @BindView(R.id.management_operations_edit_btn)
        ImageButton editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageButton deleteBtn;

        public SurgeryManagementVH(View view) {
            super(view);

        }
    }
}
