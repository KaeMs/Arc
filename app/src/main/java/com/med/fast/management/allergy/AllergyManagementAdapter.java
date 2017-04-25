package com.med.fast.management.allergy;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.med.fast.Constants;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.visit.VisitImageAdapter;
import com.med.fast.management.visit.VisitModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class AllergyManagementAdapter extends FastBaseRecyclerAdapter {

    private Context context;
    private List<AllergyManagementModel> mDataset = new ArrayList<>();

    public AllergyManagementAdapter(Context context){
        this.context = context;
    }

    public void addList(List<AllergyManagementModel> dataset){
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View visitView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.management_allergy_item_card, parent, false);
        return new AllergyManagementVH(visitView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AllergyManagementVH allergyManagementVH = (AllergyManagementVH)holder;
        allergyManagementVH.agent.setText(mDataset.get(position).getAgent());
        allergyManagementVH.drug.setText(mDataset.get(position).getDrug());
        allergyManagementVH.reaction.setText(mDataset.get(position).getReaction());
        allergyManagementVH.firstExperience.setText(mDataset.get(position).getFirst_experience());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.dateFormatSlash, Locale.getDefault());
        allergyManagementVH.date.setText(simpleDateFormat.format(mDataset.get(position).getCreated_date()));
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
