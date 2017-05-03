package com.med.fast.management.labresult;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.med.fast.Constants;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.DeleteConfirmEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.labresult.api.LabResultManagementCreateSubmitAPI;
import com.med.fast.management.labresult.api.LabResultManagementCreateSubmitAPIFunc;
import com.med.fast.management.labresult.api.LabResultManagementDeleteAPIFunc;
import com.med.fast.management.labresult.api.LabResultManagementDeleteSubmitAPI;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementCreateDeleteIntf;
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

public class LabResultManagementAdapter extends FastBaseRecyclerAdapter implements LabResultManagementCreateDeleteIntf {

    private final int PROGRESS = 0;
    private final int LABRESULT = 1;
    private Context context;
    private List<LabResultManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private StartActivityForResultInAdapterIntf startActivityForResultInAdapterIntf;
    private String userId;

    public LabResultManagementAdapter(Context context, StartActivityForResultInAdapterIntf intf){
        super(true);
        this.context = context;
        this.startActivityForResultInAdapterIntf = intf;
        this.userId = SharedPreferenceUtilities.getUserId(context);
    }

    public void addList(List<LabResultManagementModel> dataset){
//        this.mDataset.addAll(dataset);
//        notifyDataSetChanged();
        for (LabResultManagementModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(LabResultManagementModel model){
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

    public void submitItem(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.management_labresult_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final CustomFontEditText testType = (CustomFontEditText) dialog.findViewById(R.id.labresult_popup_test_type);
        final CustomFontEditText testLocation = (CustomFontEditText) dialog.findViewById(R.id.labresult_popup_test_location);
        final CustomFontEditText testDescription = (CustomFontEditText) dialog.findViewById(R.id.labresult_popup_test_description);
        final CustomFontEditText testFinishedDate = (CustomFontEditText) dialog.findViewById(R.id.labresult_popup_test_finished_date);

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(context);
        mAwesomeValidation.addValidation(testType, RegexTemplate.NOT_EMPTY, context.getString(R.string.lab_test_type_question_required));
        mAwesomeValidation.addValidation(testLocation, RegexTemplate.NOT_EMPTY, context.getString(R.string.lab_test_location_question_required));
        mAwesomeValidation.addValidation(testDescription, RegexTemplate.NOT_EMPTY, context.getString(R.string.lab_test_description_question_required));
        mAwesomeValidation.addValidation(testFinishedDate, RegexTemplate.NOT_EMPTY, context.getString(R.string.lab_test_date_question_required));

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        testFinishedDate.setOnClickListener(new View.OnClickListener() {
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
                                testFinishedDate.setText(date);
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
                if (mAwesomeValidation.validate()) {
                    String testTypeString = testType.getText().toString();
                    String testLocationString = testLocation.getText().toString();
                    String testDescriptionString = testDescription.getText().toString();
                    String testFinishedDateString = testFinishedDate.getText().toString();

                    LabResultManagementModel labResultManagementModel = new LabResultManagementModel();
                    labResultManagementModel.setTest_type(testTypeString);
                    labResultManagementModel.setTest_location(testLocationString);
                    labResultManagementModel.setTest_description(testDescriptionString);
                    labResultManagementModel.setTest_date(testFinishedDateString);
                    labResultManagementModel.setProgress_status(APIConstants.PROGRESS_ADD);
                    labResultManagementModel.setTag(APIConstants.PROGRESS_ADD);

                    addSingle(labResultManagementModel);

                    LabResultManagementCreateSubmitAPI labResultManagementCreateSubmitAPI = new LabResultManagementCreateSubmitAPI();
                    labResultManagementCreateSubmitAPI.data.query.user_id = userId;
                    labResultManagementCreateSubmitAPI.data.query.test_name = testTypeString;
                    labResultManagementCreateSubmitAPI.data.query.desc_result = testDescriptionString;
                    labResultManagementCreateSubmitAPI.data.query.place = testLocationString;
                    labResultManagementCreateSubmitAPI.data.query.date = testFinishedDateString;
//                    labResultManagementCreateSubmitAPI.data.query.img_object = testFinishedDateString;

                    LabResultManagementCreateSubmitAPIFunc labResultManagementCreateSubmitAPIFunc = new
                            LabResultManagementCreateSubmitAPIFunc(context, LabResultManagementAdapter.this, labResultManagementModel.getTag());

                    dialog.dismiss();
                }
            }
        });
    }

    public void reSubmitItem(int position){

    }

    // Update by model
    public void updateItem(LabResultManagementModel item){
        for (int i = getItemCount() - 1; i > 0; i++){
            if (mDataset.get(i).getTest_id().equals(item.getTest_id())){
                item.setProgress_status(APIConstants.PROGRESS_NORMAL);
                mDataset.set(i, item);
                break;
            }
        }
    }

    // Update by tag
    public void updateItem(String tag, boolean success){
        for (int i = getItemCount() - 1; i > 0; i++){
            if (mDataset.get(i).getTag().equals(tag)){
                if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_ADD)){
                    if (success)mDataset.get(i).setProgress_status(APIConstants.PROGRESS_NORMAL);
                    else mDataset.get(i).setProgress_status(APIConstants.PROGRESS_ADD_FAIL);
                } else if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_DELETE)){
                    mDataset.get(i).setProgress_status(APIConstants.PROGRESS_NORMAL);
                }
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? LABRESULT : PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == LABRESULT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_accident_item_card, parent, false);
            viewHolder = new LabResultManagementVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.infiscroll_progress_layout, parent, false);
            viewHolder = new InfiScrollProgressVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == LABRESULT){
            LabResultManagementVH labResultManagementVH = (LabResultManagementVH)holder;
            labResultManagementVH.testingDate.setText(mDataset.get(position).getTest_date());
            labResultManagementVH.testName.setText(mDataset.get(position).getTest_type());
            labResultManagementVH.testingPlace.setText(mDataset.get(position).getTest_location());
            labResultManagementVH.testingDescription.setText(mDataset.get(position).getTest_description());

            if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD)){
                labResultManagementVH.statusProgressBar.setOnClickListener(null);
                labResultManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                labResultManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_DELETE)){
                labResultManagementVH.statusProgressBar.setOnClickListener(null);
                labResultManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                labResultManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD_FAIL)){
                labResultManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                labResultManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_tosca));
                labResultManagementVH.statusProgressBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        reSubmitItem(holder.getAdapterPosition());
                    }
                });
            } else {
                labResultManagementVH.statusProgressBar.setOnClickListener(null);
                labResultManagementVH.statusProgressBar.setVisibility(View.GONE);
            }

            labResultManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)){
                        createDeleteDialog(context, context.getString(R.string.lab_test_delete_confirmation), "labresult" + mDataset.get(holder.getAdapterPosition()).getTest_id());
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
    public void onDeleteConfirm(DeleteConfirmEvent deleteConfirmEvent){
        for (int i = 0; i < getItemCount(); i++){
            if (deleteConfirmEvent.deletionId.equals("labresult" + mDataset.get(i).getTest_id())){
                mDataset.get(i).setProgress_status(APIConstants.PROGRESS_DELETE);
                notifyItemChanged(i);

                LabResultManagementDeleteSubmitAPI accidentHistoryDeleteSubmitAPI = new LabResultManagementDeleteSubmitAPI();
                accidentHistoryDeleteSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(context);
                accidentHistoryDeleteSubmitAPI.data.query.lab_result_id = mDataset.get(i).getTest_id();

                LabResultManagementDeleteAPIFunc accidentHistoryDeleteSubmitAPIFunc =
                        new LabResultManagementDeleteAPIFunc(context, LabResultManagementAdapter.this, mDataset.get(i).getTest_id());
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
    public void onFinishLabResultManagementCreate(ResponseAPI responseAPI, String tag) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            LabResultManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, LabResultManagementCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                updateItem(tag, true);
            } else {
                updateItem(tag, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            updateItem(tag, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity)context).forceLogout();
        } else {
            updateItem(tag, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishLabResultManagementDelete(ResponseAPI responseAPI, String tag) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            LabResultManagementDeleteSubmitAPI output = gson.fromJson(responseAPI.status_response, LabResultManagementDeleteSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    if (output.data.query.lab_result_id.equals(mDataset.get(i).getTest_id())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    }
                }
            } else {
                updateItem(tag, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            updateItem(tag, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity)context).forceLogout();
        } else {
            updateItem(tag, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    static class LabResultManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_status_progress_progressbar)
        ProgressBar statusProgressBar;
        @BindView(R.id.management_labresult_item_testing_date)
        CustomFontTextView testingDate;
        @BindView(R.id.management_labresult_item_test_name)
        CustomFontTextView testName;
        @BindView(R.id.management_labresult_item_testing_place)
        CustomFontTextView testingPlace;
        @BindView(R.id.management_labresult_item_testing_description)
        CustomFontTextView testingDescription;
        @BindView(R.id.management_operations_edit_btn)
        ImageView editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageView deleteBtn;


        public LabResultManagementVH(View view) {
            super(view);

        }
    }
}
