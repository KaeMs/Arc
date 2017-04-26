package com.med.fast.management.labresult;

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

public class LabResultManagementAdapter extends FastBaseRecyclerAdapter {

    private Context context;
    private List<LabResultManagementModel> mDataset = new ArrayList<>();

    public LabResultManagementAdapter(Context context){
        this.context = context;
    }

    public void addList(List<LabResultManagementModel> dataset){
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void addSingle(LabResultManagementModel model){
        this.mDataset.add(model);
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View visitView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.management_labresult_item_card, parent, false);
        return new LabResultManagementVH(visitView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LabResultManagementVH labResultManagementVH = (LabResultManagementVH)holder;
        labResultManagementVH.testingDate.setText(mDataset.get(position).getTest_date());
        labResultManagementVH.testName.setText(mDataset.get(position).getTest_type());
        labResultManagementVH.testingPlace.setText(mDataset.get(position).getTest_location());
        labResultManagementVH.testingDescription.setText(mDataset.get(position).getTest_description());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class LabResultManagementVH extends FastBaseViewHolder {

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
