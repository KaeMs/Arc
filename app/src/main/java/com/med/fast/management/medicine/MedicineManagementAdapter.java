package com.med.fast.management.medicine;

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

public class MedicineManagementAdapter extends FastBaseRecyclerAdapter {

    private Context context;
    private List<MedicineManagementModel> mDataset = new ArrayList<>();

    public MedicineManagementAdapter(Context context){
        this.context = context;
    }

    public void addList(List<MedicineManagementModel> dataset){
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void addSingle(MedicineManagementModel model){
        this.mDataset.add(model);
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View visitView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.management_medicine_item_card, parent, false);
        return new MedicineManagementVH(visitView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MedicineManagementVH medicineManagementVH = (MedicineManagementVH)holder;

        medicineManagementVH.medicineName.setText(mDataset.get(position).getMedicine_name());
        medicineManagementVH.medicineForm.setText(mDataset.get(position).getMedicine_form());
        medicineManagementVH.administrationMethod.setText(mDataset.get(position).getMedicine_administration_method());
        medicineManagementVH.administrationDose.setText(mDataset.get(position).getMedicine_administration_dose());
        medicineManagementVH.frequency.setText(mDataset.get(position).getMedicine_frequency());
        medicineManagementVH.createdDate.setText(mDataset.get(position).getMedicine_created_date());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class MedicineManagementVH extends FastBaseViewHolder {

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

        @BindView(R.id.allergy_item_card_edit)
        ImageView editBtn;
        @BindView(R.id.allergy_item_card_delete)
        ImageView deleteBtn;


        public MedicineManagementVH(View view) {
            super(view);

        }
    }
}
