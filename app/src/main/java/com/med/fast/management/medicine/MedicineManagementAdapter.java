package com.med.fast.management.medicine;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

public class MedicineManagementAdapter extends FastBaseRecyclerAdapter {

    private final int PROGRESS = 0;
    private final int MEDICINE = 1;
    private Context context;
    private List<MedicineManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;

    public MedicineManagementAdapter(Context context){
        this.context = context;
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
            medicineManagementVH.medicineName.setText(mDataset.get(position).getMedicine_name());
            medicineManagementVH.medicineForm.setText(mDataset.get(position).getMedicine_form());
            medicineManagementVH.administrationMethod.setText(mDataset.get(position).getMedicine_administration_method());
            medicineManagementVH.administrationDose.setText(mDataset.get(position).getMedicine_administration_dose());
            medicineManagementVH.frequency.setText(mDataset.get(position).getMedicine_frequency());
            medicineManagementVH.createdDate.setText(mDataset.get(position).getMedicine_created_date());

            if (mDataset.get(position).getProgress_status().equals("1")){
                medicineManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                medicineManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
            } else if (mDataset.get(position).getProgress_status().equals("2")){
                medicineManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                medicineManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
            } else {
                medicineManagementVH.statusProgressBar.setVisibility(View.GONE);
            }

            medicineManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataset.get(holder.getAdapterPosition()).setProgress_status("2");
                    notifyItemChanged(holder.getAdapterPosition());
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

    static class MedicineManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_medicine_item_card_progress)
        ProgressBar statusProgressBar;
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
