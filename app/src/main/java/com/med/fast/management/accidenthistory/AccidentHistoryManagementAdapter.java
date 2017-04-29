package com.med.fast.management.accidenthistory;

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
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.DeleteConfirmEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.accidenthistory.accidentinterface.AccidentHistoryDeleteIntf;
import com.med.fast.management.accidenthistory.api.AccidentHistoryDeleteSubmitAPI;
import com.med.fast.management.accidenthistory.api.AccidentHistoryDeleteSubmitAPIFunc;
import com.med.fast.management.allergy.AllergyManagementModel;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/24/2017. FM
 */

public class AccidentHistoryManagementAdapter extends FastBaseRecyclerAdapter implements AccidentHistoryDeleteIntf {

    private final int PROGRESS = 0;
    private final int ACCIDENT = 1;
    private Context context;
    private List<AccidentHistoryManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private String deletionId = "";

    public AccidentHistoryManagementAdapter(Context context){
        super(true);
        this.context = context;
//        EventBus.getDefault().register(this);
    }

    public void addList(List<AccidentHistoryManagementModel> dataset){
        for (AccidentHistoryManagementModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(AccidentHistoryManagementModel accident){
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

    public void updateItem(AccidentHistoryManagementModel item){
        for (int i = getItemCount() - 1; i > 0; i++){
            if (mDataset.get(i).getAccident_id().equals(item.getAccident_id()) &&
                    mDataset.get(i).getDetail().equals(item.getDetail()) &&
                    mDataset.get(i).getInjury_date().equals(item.getInjury_date()) &&
                    mDataset.get(i).getInjury_location().equals(item.getInjury_location()) &&
                    mDataset.get(i).getProgress_status().equals("1")){
                item.setProgress_status("0");
                mDataset.set(i, item);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? ACCIDENT : PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == ACCIDENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_accident_item_card, parent, false);
            viewHolder = new AccidentManagementVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.infiscroll_progress_layout, parent, false);
            viewHolder = new InfiScrollProgressVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ACCIDENT){
            AccidentManagementVH accidentManagementVH = (AccidentManagementVH)holder;
            accidentManagementVH.details.setText(mDataset.get(position).getDetail());
            accidentManagementVH.injuryNature.setText(mDataset.get(position).getInjury_nature());
            accidentManagementVH.injuryLocation.setText(mDataset.get(position).getInjury_location());
            accidentManagementVH.injuryDate.setText(mDataset.get(position).getInjury_date());
            accidentManagementVH.createdDate.setText(mDataset.get(position).getCreated_date());

            if (mDataset.get(position).getProgress_status().equals("1")){
                accidentManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                accidentManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
            } else if (mDataset.get(position).getProgress_status().equals("2")){
                accidentManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                accidentManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
            } else {
                accidentManagementVH.statusProgressBar.setVisibility(View.GONE);
            }

            accidentManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletionId = mDataset.get(holder.getAdapterPosition()).getAccident_id();
                    createDeleteDialog(context, context.getString(R.string.accident_delete_confirmation));
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

    @Subscribe
    public void onDeleteConfirm(DeleteConfirmEvent deleteConfirmEvent){
        for (int i = 0; i < getItemCount(); i++){
            if (deletionId.equals(mDataset.get(i).getAccident_id())){
                mDataset.get(i).setProgress_status("2");
                notifyItemChanged(i);

                AccidentHistoryDeleteSubmitAPI accidentHistoryDeleteSubmitAPI = new AccidentHistoryDeleteSubmitAPI();
                accidentHistoryDeleteSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(context);
                accidentHistoryDeleteSubmitAPI.data.query.accident_id = mDataset.get(i).getAccident_id();

                AccidentHistoryDeleteSubmitAPIFunc accidentHistoryDeleteSubmitAPIFunc = new AccidentHistoryDeleteSubmitAPIFunc(context);
                accidentHistoryDeleteSubmitAPIFunc.setDelegate(AccidentHistoryManagementAdapter.this);
                accidentHistoryDeleteSubmitAPIFunc.execute(accidentHistoryDeleteSubmitAPI);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onFinishAccidentHistoryDelete(ResponseAPI responseAPI) {
        if(responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AccidentHistoryDeleteSubmitAPI output = gson.fromJson(responseAPI.status_response, AccidentHistoryDeleteSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    if (output.data.query.accident_id.equals(mDataset.get(i).getAccident_id())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    }
                }
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

/*    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        EventBus.getDefault().unregister(this);
    }*/

    static class AccidentManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_accident_item_card_progress)
        ProgressBar statusProgressBar;
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
