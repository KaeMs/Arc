package com.med.fast.management.disease;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.med.fast.Constants;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class DiseaseManagementAdapter extends FastBaseRecyclerAdapter {

    private final int PROGRESS = 0;
    private final int DISEASE = 1;
    private Context context;
    private List<DiseaseManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private String deletionId = "";

    public DiseaseManagementAdapter(Context context){
        super(true);
        this.context = context;
    }

    public void addList(List<DiseaseManagementModel> dataset){
        for (DiseaseManagementModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(DiseaseManagementModel model){
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

    public void updateItem(DiseaseManagementModel item){
        for (int i = getItemCount() - 1; i > 0; i++){
            if (mDataset.get(i).getDisease_name().equals(item.getDisease_name()) &&
                    mDataset.get(i).getDisease_hereditary_carriers().equals(item.getDisease_hereditary_carriers()) &&
                    mDataset.get(i).getProgress_status().equals("1")){
                item.setProgress_status("0");
                mDataset.set(i, item);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? DISEASE : PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == DISEASE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_accident_item_card, parent, false);
            viewHolder = new DiseaseManagementVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.infiscroll_progress_layout, parent, false);
            viewHolder = new InfiScrollProgressVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == DISEASE){
            DiseaseManagementVH diseaseManagementVH = (DiseaseManagementVH)holder;
            diseaseManagementVH.name.setText(mDataset.get(position).getDisease_name());
            diseaseManagementVH.isHereditaryTV.setText(mDataset.get(position).getDisease_hereditary());
            diseaseManagementVH.hereditaryCarriers.setText(mDataset.get(position).getDisease_hereditary_carriers());
            diseaseManagementVH.isOngoingTV.setText(mDataset.get(position).getDisease_ongoing());
            diseaseManagementVH.lastVisit.setText(mDataset.get(position).getDate_last_visit());
            diseaseManagementVH.historicDate.setText(mDataset.get(position).getDate_historic());
            diseaseManagementVH.approximateDate.setText(mDataset.get(position).getDate_approximate());
            diseaseManagementVH.createdDate.setText(mDataset.get(position).getDate_created());

            if (mDataset.get(position).getProgress_status().equals("1")){
                diseaseManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                diseaseManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
            } else if (mDataset.get(position).getProgress_status().equals("2")){
                diseaseManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                diseaseManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
            } else {
                diseaseManagementVH.statusProgressBar.setVisibility(View.GONE);
            }

            diseaseManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletionId = mDataset.get(holder.getAdapterPosition()).getDisease_name();
                    createDeleteDialog(context, context.getString(R.string.disease_delete_confirmation));
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

    static class DiseaseManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_status_progress_progressbar)
        ProgressBar statusProgressBar;
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
