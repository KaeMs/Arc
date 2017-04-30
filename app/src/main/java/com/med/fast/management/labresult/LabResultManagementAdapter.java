package com.med.fast.management.labresult;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontTextView;
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

public class LabResultManagementAdapter extends FastBaseRecyclerAdapter implements LabResultManagementDeleteIntf {

    private final int PROGRESS = 0;
    private final int LABRESULT = 1;
    private Context context;
    private List<LabResultManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private String deletionId = "";

    public LabResultManagementAdapter(Context context){
        super(true);
        this.context = context;
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

    public void updateItem(LabResultManagementModel item){
        for (int i = getItemCount() - 1; i > 0; i++){
            if (mDataset.get(i).getTest_type().equals(item.getTest_type()) &&
                    mDataset.get(i).getTest_description().equals(item.getTest_description()) &&
                    mDataset.get(i).getProgress_status().equals("1")){
                item.setProgress_status("0");
                mDataset.set(i, item);
                break;
            }
        }
    }

    // Update by tag
    public void updateItem(String tag, boolean success){
        for (int i = getItemCount() - 1; i > 0; i++){
            if (tag != null){
                if (mDataset.get(i).getTag().equals(tag)){
                    mDataset.get(i).setProgress_status(success ? "0" : "3");
                    notifyItemChanged(i);
                    break;
                }
            } else {
                if (mDataset.get(i).getProgress_status().equals("1")){
                    mDataset.get(i).setProgress_status("3");
                    notifyItemChanged(i);
                    break;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
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
                        deletionId = mDataset.get(holder.getAdapterPosition()).getTest_id();
                        createDeleteDialog(context, context.getString(R.string.lab_test_delete_confirmation), mDataset.get(position).getTest_id());
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
    public void onFinishLabResultManagementDelete(ResponseAPI responseAPI) {
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
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity)context).forceLogout();
        } else {
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
