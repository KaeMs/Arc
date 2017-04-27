package com.med.fast.management.allergy;

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
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class AllergyManagementAdapter extends FastBaseRecyclerAdapter {

    private final int PROGRESS = 0;
    private final int ALLERGY = 1;
    private Context context;
    private List<AllergyManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;

    public AllergyManagementAdapter(Context context){
        this.context = context;
    }

    public void addList(List<AllergyManagementModel> dataset){
        for (AllergyManagementModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(AllergyManagementModel accident){
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
        return mDataset.get(position) != null ? ALLERGY : PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == ALLERGY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_allergy_item_card, parent, false);
            viewHolder = new AllergyManagementVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.infiscroll_progress_layout, parent, false);
            viewHolder = new InfiScrollProgressVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ALLERGY){
            AllergyManagementVH allergyManagementVH = (AllergyManagementVH)holder;
            allergyManagementVH.agent.setText(mDataset.get(position).getAgent());
            allergyManagementVH.drug.setText(mDataset.get(position).getDrug());
            allergyManagementVH.reaction.setText(mDataset.get(position).getReaction());
            allergyManagementVH.firstExperience.setText(mDataset.get(position).getFirst_experience());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.dateFormatSlash, Locale.getDefault());
            allergyManagementVH.date.setText(simpleDateFormat.format(mDataset.get(position).getCreated_date()));
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

    static class AllergyManagementVH extends FastBaseViewHolder {

        @BindView(R.id.allergy_item_card_agent)
        CustomFontTextView agent;
        @BindView(R.id.allergy_item_card_drug)
        CustomFontTextView drug;
        @BindView(R.id.allergy_item_card_reaction)
        CustomFontTextView reaction;
        @BindView(R.id.allergy_item_card_first_experience)
        CustomFontTextView firstExperience;
        @BindView(R.id.allergy_item_card_date)
        CustomFontTextView date;
        @BindView(R.id.management_operations_edit_btn)
        ImageView editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageView deleteBtn;


        public AllergyManagementVH(View view) {
            super(view);

        }
    }
}
