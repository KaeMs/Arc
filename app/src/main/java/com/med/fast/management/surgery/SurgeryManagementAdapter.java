package com.med.fast.management.surgery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class SurgeryManagementAdapter extends FastBaseRecyclerAdapter {

    private final int PROGRESS = 0;
    private final int SURGERY = 1;
    private Context context;
    private List<SurgeryManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;

    public SurgeryManagementAdapter(Context context){
        super(false);
        this.context = context;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == SURGERY) {
            SurgeryManagementVH surgeryManagementVH = (SurgeryManagementVH)holder;
            surgeryManagementVH.surgeryDate.setText(mDataset.get(position).getSurgery_date());
            surgeryManagementVH.surgeryProcedure.setText(mDataset.get(position).getSurgery_procedure());
            surgeryManagementVH.physicianName.setText(mDataset.get(position).getSurgery_physician_name());
            surgeryManagementVH.hospitalName.setText(mDataset.get(position).getSurgery_hospital_name());
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

    static class SurgeryManagementVH extends FastBaseViewHolder {

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
