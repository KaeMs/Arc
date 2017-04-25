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
import com.med.fast.customviews.CustomFontTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class SurgeryManagementAdapter extends FastBaseRecyclerAdapter {

    private Context context;
    private List<SurgeryManagementModel> mDataset = new ArrayList<>();

    public SurgeryManagementAdapter(Context context){
        this.context = context;
    }

    public void addList(List<SurgeryManagementModel> dataset){
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void addSingle(SurgeryManagementModel model){
        this.mDataset.add(model);
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View visitView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.management_surgery_item_card, parent, false);
        return new SurgeryManagementVH(visitView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SurgeryManagementVH surgeryManagementVH = (SurgeryManagementVH)holder;

        surgeryManagementVH.surgeryDate.setText(mDataset.get(position).getSurgery_date());
        surgeryManagementVH.surgeryProcedure.setText(mDataset.get(position).getSurgery_procedure());
        surgeryManagementVH.physicianName.setText(mDataset.get(position).getSurgery_physician_name());
        surgeryManagementVH.hospitalName.setText(mDataset.get(position).getSurgery_hospital_name());
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
