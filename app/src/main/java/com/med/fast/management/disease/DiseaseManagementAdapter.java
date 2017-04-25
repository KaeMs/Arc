package com.med.fast.management.disease;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.med.fast.Constants;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class DiseaseManagementAdapter extends FastBaseRecyclerAdapter {

    private Context context;
    private List<DiseaseManagementModel> mDataset = new ArrayList<>();

    public DiseaseManagementAdapter(Context context){
        this.context = context;
    }

    public void addList(List<DiseaseManagementModel> dataset){
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void addSingle(DiseaseManagementModel model){
        this.mDataset.add(model);
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View visitView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.management_disease_item_card, parent, false);
        return new DiseaseManagementVH(visitView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiseaseManagementVH diseaseManagementVH = (DiseaseManagementVH)holder;
        diseaseManagementVH.name.setText(mDataset.get(position).getDisease_name());
        diseaseManagementVH.isHereditaryTV.setText(mDataset.get(position).getDisease_hereditary());
        diseaseManagementVH.hereditaryCarriers.setText(mDataset.get(position).getDisease_hereditary_carriers());
        diseaseManagementVH.isOngoingTV.setText(mDataset.get(position).getDisease_ongoing());
        diseaseManagementVH.lastVisit.setText(mDataset.get(position).getDate_last_visit());
        diseaseManagementVH.historicDate.setText(mDataset.get(position).getDate_historic());
        diseaseManagementVH.approximateDate.setText(mDataset.get(position).getDate_approximate());
        diseaseManagementVH.createdDate.setText(mDataset.get(position).getDate_created());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class DiseaseManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_disease_item_name)
        CustomFontTextView name;
        @BindView(R.id.management_disease_item_hereditary)
        CustomFontTextView isHereditaryTV;
        @BindView(R.id.management_disease_item_hereditary_carriers)
        CustomFontTextView hereditaryCarriers;
        @BindView(R.id.management_disease_item_ongoing)
        CustomFontTextView isOngoingTV;
        @BindView(R.id.management_disease_item_injury_date_last_visit)
        CustomFontTextView lastVisit;
        @BindView(R.id.management_disease_item_injury_date_historic)
        CustomFontTextView historicDate;
        @BindView(R.id.management_disease_item_injury_date_approximate)
        CustomFontTextView approximateDate;
        @BindView(R.id.management_disease_item_injury_created_date)
        CustomFontTextView createdDate;
        @BindView(R.id.management_operations_edit_btn)
        ImageView editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageView deleteBtn;


        public DiseaseManagementVH(View view) {
            super(view);

        }
    }
}
