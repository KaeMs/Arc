package com.med.fast.management.accidenthistory;

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

public class AccidentHistoryManagementAdapter extends FastBaseRecyclerAdapter {

    private Context context;
    private List<AccidentHistoryManagementModel> mDataset = new ArrayList<>();

    public AccidentHistoryManagementAdapter(Context context){
        this.context = context;
    }

    public void addList(List<AccidentHistoryManagementModel> dataset){
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void addSingle(AccidentHistoryManagementModel accident){
        this.mDataset.add(accident);
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View visitView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.management_accident_item_card, parent, false);
        return new AccidentManagementVH(visitView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AccidentManagementVH accidentManagementVH = (AccidentManagementVH)holder;
        accidentManagementVH.details.setText(mDataset.get(position).getDetail());
        accidentManagementVH.injuryNature.setText(mDataset.get(position).getInjury_nature());
        accidentManagementVH.injuryLocation.setText(mDataset.get(position).getInjury_location());
        accidentManagementVH.injuryDate.setText(mDataset.get(position).getInjury_date());
        accidentManagementVH.createdDate.setText(mDataset.get(position).getCreated_date());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class AccidentManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_accident_item_accident_details)
        CustomFontTextView details;
        @BindView(R.id.management_accident_item_injury_nature)
        CustomFontTextView injuryNature;
        @BindView(R.id.management_accident_item_injury_location)
        CustomFontTextView injuryLocation;
        @BindView(R.id.management_accident_item_injury_date)
        CustomFontTextView injuryDate;
        @BindView(R.id.management_accident_item_injury_created_date)
        CustomFontTextView createdDate;
        @BindView(R.id.management_operations_edit_btn)
        ImageView editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageView deleteBtn;


        public AccidentManagementVH(View view) {
            super(view);
        }
    }
}
