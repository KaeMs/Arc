package com.med.fast.management.allergy;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.allergy.allergyinterface.AllergyManagementDeleteIntf;
import com.med.fast.management.allergy.api.AllergyManagementDeleteAPI;
import com.med.fast.management.allergy.api.AllergyManagementDeleteAPIFunc;
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

public class AllergyManagementAdapter extends FastBaseRecyclerAdapter implements AllergyManagementDeleteIntf {

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

    public void updateItem(AllergyManagementModel item){
        for (int i = getItemCount() - 1; i > 0; i++){
            if (mDataset.get(i).getAgent().equals(item.getAgent()) &&
                    mDataset.get(i).getDrug().equals(item.getDrug()) &&
                    mDataset.get(i).getReaction().equals(item.getReaction()) &&
                    mDataset.get(i).getFirst_experience().equals(item.getFirst_experience()) &&
                    mDataset.get(i).getProgress_status().equals("1")){
                mDataset.set(i, item);
                break;
            }
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ALLERGY){
            AllergyManagementVH allergyManagementVH = (AllergyManagementVH)holder;
            allergyManagementVH.agent.setText(mDataset.get(position).getAgent());
            allergyManagementVH.drug.setText(mDataset.get(position).getDrug());
            allergyManagementVH.reaction.setText(mDataset.get(position).getReaction());
            allergyManagementVH.firstExperience.setText(mDataset.get(position).getFirst_experience());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.dateFormatSlash, Locale.getDefault());
            allergyManagementVH.date.setText(simpleDateFormat.format(mDataset.get(position).getCreated_date()));

            if (mDataset.get(position).getProgress_status().equals("1")){
                allergyManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                allergyManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
            } else if (mDataset.get(position).getProgress_status().equals("2")){
                allergyManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                allergyManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
            } else {
                allergyManagementVH.statusProgressBar.setVisibility(View.GONE);
            }

            allergyManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataset.get(holder.getAdapterPosition()).setProgress_status("2");
                    notifyItemChanged(holder.getAdapterPosition());

                    AllergyManagementDeleteAPI allergyManagementDeleteAPI = new AllergyManagementDeleteAPI();
                    allergyManagementDeleteAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(context);
                    allergyManagementDeleteAPI.data.query.allergy_id = mDataset.get(holder.getAdapterPosition()).getAllergy_id();

                    AllergyManagementDeleteAPIFunc allergyManagementDeleteAPIFunc = new AllergyManagementDeleteAPIFunc(context);
                    allergyManagementDeleteAPIFunc.setDelegate(AllergyManagementAdapter.this);
                    allergyManagementDeleteAPIFunc.execute(allergyManagementDeleteAPI);
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

    @Override
    public void onFinishAllergyManagementDelete(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AllergyManagementDeleteAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementDeleteAPI.class);
            if (output.data.status.code.equals("200")) {
                /*for (AllergyManagementModel item :
                        mDataset) {
                    if ()
                }*/
            } else {
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if(responseAPI.status_code == 504) {
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if(responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity)context).forceLogout();
        } else {
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    static class AllergyManagementVH extends FastBaseViewHolder {

        @BindView(R.id.allergy_item_card_progress)
        ProgressBar statusProgressBar;
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
