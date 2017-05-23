package com.med.fast.management.labresult;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.google.gson.Gson;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.HorizontalItemDecoration;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.DeleteConfirmIntf;
import com.med.fast.management.allergy.AllergyEditActivity;
import com.med.fast.management.labresult.api.LabResultManagementDeleteAPIFunc;
import com.med.fast.management.labresult.api.LabResultManagementDeleteSubmitAPI;
import com.med.fast.management.labresult.labresultinterface.LabResultManagementDeleteIntf;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class LabResultManagementAdapter extends FastBaseRecyclerAdapter implements LabResultManagementDeleteIntf, DeleteConfirmIntf {

    private final int PROGRESS = 0;
    private final int LABRESULT = 1;
    private Context context;
    private List<LabResultManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private StartActivityForResultInAdapterIntf startActivityForResultInAdapterIntf;
    private HorizontalItemDecoration horizontalItemDecoration;

    public LabResultManagementAdapter(Context context, StartActivityForResultInAdapterIntf intf){
        this.context = context;
        this.startActivityForResultInAdapterIntf = intf;
        this.horizontalItemDecoration = new HorizontalItemDecoration();
        deleteConfirmIntf = this;
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

    public void addSingle(LabResultManagementModel model, int position){
        this.mDataset.add(position, model);
        notifyItemInserted(position);
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

    public void submitItem(){
        Intent intent = new Intent(context, LabResultAddActivity.class);
        startActivityForResultInAdapterIntf.onStartActivityForResult(intent, RequestCodeList.LABRESULT_CREATE);
    }

    public void reSubmitItem(int position){

    }

    // Update by model
    public void updateItem(LabResultManagementModel item){
        for (int i = 0; i < getItemCount(); i++) {
            if (mDataset.get(i).getId().equals(item.getId())){
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

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? LABRESULT : PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == LABRESULT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_labresult_item_card, parent, false);
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
            labResultManagementVH.testingDate.setText(mDataset.get(position).getDate());
            labResultManagementVH.testName.setText(mDataset.get(position).getTest_name());
            labResultManagementVH.testingPlace.setText(mDataset.get(position).getPlace());
            labResultManagementVH.testingDescription.setText(mDataset.get(position).getDesc_result());

            if (mDataset.get(position).getImg_list().size() > 0) {
                labResultManagementVH.imageRecycler.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                LabResultImageShowAdapter labResultImageShowAdapter = new LabResultImageShowAdapter(context);
                labResultImageShowAdapter.addList(mDataset.get(position).getImg_list());
                SnapHelper snapHelper = new GravityPagerSnapHelper(Gravity.START);
                setItemDecoration(labResultManagementVH.imageRecycler, horizontalItemDecoration);
                labResultManagementVH.imageRecycler.setLayoutManager(linearLayoutManager);
                labResultManagementVH.imageRecycler.setAdapter(labResultImageShowAdapter);
                labResultManagementVH.imageRecycler.setOnFlingListener(null);
                snapHelper.attachToRecyclerView(labResultManagementVH.imageRecycler);
            } else {
                labResultManagementVH.imageRecycler.setVisibility(View.GONE);
            }

            if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD)){
                labResultManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                labResultManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
                labResultManagementVH.editBtn.setEnabled(false);
                labResultManagementVH.deleteBtn.setEnabled(false);
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_DELETE)){
                labResultManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                labResultManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
                labResultManagementVH.editBtn.setEnabled(false);
                labResultManagementVH.deleteBtn.setEnabled(false);
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD_FAIL)){
                labResultManagementVH.statusProgressBar.setVisibility(View.GONE);
                labResultManagementVH.progressFailImg.setVisibility(View.VISIBLE);
                labResultManagementVH.progressFailImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reSubmitItem(holder.getAdapterPosition());
                    }
                });
                labResultManagementVH.editBtn.setEnabled(false);
                labResultManagementVH.deleteBtn.setEnabled(false);
            } else {
                labResultManagementVH.statusProgressBar.setVisibility(View.GONE);
                labResultManagementVH.editBtn.setEnabled(true);
                labResultManagementVH.deleteBtn.setEnabled(true);
            }

            labResultManagementVH.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)) {
                        Intent intent = new Intent(context, AllergyEditActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra(ConstantsManagement.LABRESULT_MODEL_EXTRA,
                                gson.toJson(mDataset.get(holder.getAdapterPosition())));
                        startActivityForResultInAdapterIntf.onStartActivityForResult(intent, RequestCodeList.LABRESULT_EDIT);
                    }
                }
            });

            labResultManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)){
                        createDeleteDialog(context, context.getString(R.string.lab_test_delete_confirmation), "labresult" + mDataset.get(holder.getAdapterPosition()).getId());
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
    public void onDeleteConfirm(String deletionId) {
        for (int i = 0; i < getItemCount(); i++){
            if (deletionId.equals("labresult" + mDataset.get(i).getId())){
                mDataset.get(i).setProgress_status(APIConstants.PROGRESS_DELETE);
                notifyItemChanged(i);

                LabResultManagementDeleteSubmitAPI accidentHistoryDeleteSubmitAPI = new LabResultManagementDeleteSubmitAPI();
                accidentHistoryDeleteSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(context);
                accidentHistoryDeleteSubmitAPI.data.query.lab_result_id = mDataset.get(i).getId();

                LabResultManagementDeleteAPIFunc accidentHistoryDeleteSubmitAPIFunc =
                        new LabResultManagementDeleteAPIFunc(context, LabResultManagementAdapter.this, mDataset.get(i).getId());
                accidentHistoryDeleteSubmitAPIFunc.execute(accidentHistoryDeleteSubmitAPI);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /*@Override
    public void onFinishLabResultManagementCreate(ResponseAPI responseAPI, String tag) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            LabResultManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, LabResultManagementCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                updateItem(tag, output.data.results.new_lab_result_id, true);
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
    }*/

    @Override
    public void onFinishLabResultManagementDelete(ResponseAPI responseAPI, String tag) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            LabResultManagementDeleteSubmitAPI output = gson.fromJson(responseAPI.status_response, LabResultManagementDeleteSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    if (output.data.query.lab_result_id.equals(mDataset.get(i).getId())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    }
                }
            } else {
                updateItem(tag, output.data.query.lab_result_id, false);
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

    static class LabResultManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_status_progress_progressbar)
        ProgressBar statusProgressBar;
        @BindView(R.id.management_status_progress_fail)
        ImageView progressFailImg;
        @BindView(R.id.management_labresult_item_testing_date)
        CustomFontTextView testingDate;
        @BindView(R.id.management_labresult_item_test_name)
        CustomFontTextView testName;
        @BindView(R.id.management_labresult_item_testing_place)
        CustomFontTextView testingPlace;
        @BindView(R.id.management_labresult_item_testing_description)
        CustomFontTextView testingDescription;
        @BindView(R.id.management_labresult_item_images)
        RecyclerView imageRecycler;
        @BindView(R.id.management_operations_edit_btn)
        ImageButton editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageButton deleteBtn;


        public LabResultManagementVH(View view) {
            super(view);

        }
    }
}
