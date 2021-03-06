package com.med.fast.management.visit;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.rubensousa.gravitysnaphelper.GravityPagerSnapHelper;
import com.google.gson.Gson;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.HorizontalItemDecoration;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.DeleteConfirmEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.DeleteConfirmIntf;
import com.med.fast.management.visit.api.VisitManagementDeleteSubmitAPI;
import com.med.fast.management.visit.api.VisitManagementDeleteSubmitAPIFunc;
import com.med.fast.management.visit.visitinterface.VisitDeleteIntf;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitAdapter extends FastBaseRecyclerAdapter implements VisitDeleteIntf, DeleteConfirmIntf {

    private final int PROGRESS = 0;
    private final int VISIT = 1;
    private Context context;
    private List<VisitModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private StartActivityForResultInAdapterIntf startActivityForResultInAdapterIntf;
    private int width;
    private HorizontalItemDecoration horizontalItemDecoration;

    public VisitAdapter(Context context, StartActivityForResultInAdapterIntf intf, int width){
        this.context = context;
        this.startActivityForResultInAdapterIntf = intf;
        this.width = width;
        horizontalItemDecoration = new HorizontalItemDecoration(context);
        deleteConfirmIntf = this;
    }

    public void addList(List<VisitModel> dataset) {
        for (VisitModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(VisitModel visit) {
        this.mDataset.add(visit);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addSingle(VisitModel visit, int position) {
        this.mDataset.add(position, visit);
        notifyItemInserted(position);
    }

    public void removeProgress() {
        if (mDataset.size() > 0) {
            if (mDataset.get(mDataset.size() - 1) == null) {
                mDataset.remove(mDataset.size() - 1);
                notifyItemRemoved(mDataset.size());
            }
        }
    }

    public void clearList() {
        if (mDataset.size() > 0) {
            mDataset.clear();
            notifyDataSetChanged();
        }
    }

    public void setFailLoad(boolean failLoad) {
        this.failLoad = failLoad;
        notifyItemChanged(getItemCount() - 1);
        if (!failLoad) {
            removeProgress();
        }
    }

    public void submitItem(){
        Intent intent = new Intent(context, VisitAddActivity.class);
        startActivityForResultInAdapterIntf.onStartActivityForResult(intent, RequestCodeList.VISIT_CREATE);
        /*final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.management_visit_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final CustomFontEditText doctorName = (CustomFontEditText) dialog.findViewById(R.id.visit_popup_doctor_name);
        final CustomFontEditText hospitalName = (CustomFontEditText) dialog.findViewById(R.id.visit_popup_hospital_name);
        final CustomFontEditText diagnose = (CustomFontEditText) dialog.findViewById(R.id.visit_popup_diagnose);
        RecyclerView imageRecycler = (RecyclerView) dialog.findViewById(R.id.visit_popup_imagerecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        imageRecycler.setAdapter(visitImageAdapter);
        imageRecycler.setLayoutManager(linearLayoutManager);
        ListView diseaseHistoryListView = (ListView) dialog.findViewById(R.id.visit_popup_disease_history_recycler);
        ListView diseaseInputListView = (ListView) dialog.findViewById(R.id.visit_popup_disease_input_recycler);

        diseaseHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempHistoryStr = leftDataset.get(position);
                rightDataset.add(tempHistoryStr);
                rightRecyclerAdapter.notifyDataSetChanged();
                leftDataset.remove(position);
                leftRecyclerAdapter.notifyDataSetChanged();
            }
        });

        diseaseInputListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempDiseaseStr = rightDataset.get(position);
                leftDataset.add(tempDiseaseStr);
                leftRecyclerAdapter.notifyDataSetChanged();
                rightDataset.remove(position);
                rightRecyclerAdapter.notifyDataSetChanged();
            }
        });

        CustomFontButton backBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_back_btn);
        CustomFontButton createBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_create_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(context);
        mAwesomeValidation.addValidation(doctorName, RegexTemplate.NOT_EMPTY, context.getString(R.string.doctor_name_required));
        mAwesomeValidation.addValidation(hospitalName, RegexTemplate.NOT_EMPTY, context.getString(R.string.hospital_name_required));
        mAwesomeValidation.addValidation(diagnose, RegexTemplate.NOT_EMPTY, context.getString(R.string.diagnose_required));

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAwesomeValidation.clear();
                if (mAwesomeValidation.validate()) {
                    String doctorNameString = doctorName.getText().toString();
                    String hospitalNameString = hospitalName.getText().toString();
                    String diagnoseString = diagnose.getText().toString();

//                    List<VisitImageItem> image_list;

                    VisitModel visitModel = new VisitModel();
                    visitModel.setId("");
                    visitModel.setOwner_id(userId);
                    visitModel.setCreated_date(Utils.getCurrentDate());
                    visitModel.setHospital_name(hospitalNameString);
                    visitModel.setDoctor_name(doctorNameString);
                    visitModel.setDiagnose(diagnoseString);
                    visitModel.setDiseases("disease");

                    visitModel.setProgress_status("1");
                    visitModel.setTag(doctorNameString + getItemCount());

                    addSingle(visitModel);

                    VisitManagementCreateSubmitAPI visitManagementCreateSubmitAPI = new VisitManagementCreateSubmitAPI();
                    visitManagementCreateSubmitAPI.data.query.user_id = userId;
                    visitManagementCreateSubmitAPI.data.query.doctor = doctorNameString;
                    visitManagementCreateSubmitAPI.data.query.hospital = hospitalNameString;
                    visitManagementCreateSubmitAPI.data.query.diagnose = diagnoseString;
                    visitManagementCreateSubmitAPI.data.query.disease_id_list = userId;
                    visitManagementCreateSubmitAPI.data.query.is_image_uploaded = userId;

                    VisitManagementCreateSubmitAPIFunc visitManagementCreateSubmitAPIFunc =
                            new VisitManagementCreateSubmitAPIFunc(context, VisitAdapter.this, visitModel.getTag());
                    visitManagementCreateSubmitAPIFunc.execute(visitManagementCreateSubmitAPI);
                }
            }
        });*/
    }

    private void reSubmitItem(int position){
        /*VisitManagementCreateSubmitAPI visitManagementCreateSubmitAPI = new VisitManagementCreateSubmitAPI();
        visitManagementCreateSubmitAPI.data.query.user_id = userId;
        visitManagementCreateSubmitAPI.data.query.doctor = mDataset.get(position).getDoctor_name();
        visitManagementCreateSubmitAPI.data.query.hospital = mDataset.get(position).getHospital_name();
        visitManagementCreateSubmitAPI.data.query.diagnose = mDataset.get(position).getDiagnose();
//        visitManagementCreateSubmitAPI.data.query.disease_id_list = mDataset.get(position).getDoctor_name();
//        visitManagementCreateSubmitAPI.data.query.is_image_uploaded = userId;

        VisitManagementCreateSubmitAPIFunc visitManagementCreateSubmitAPIFunc =
                new VisitManagementCreateSubmitAPIFunc(context, VisitAdapter.this, mDataset.get(position).getTag());
        visitManagementCreateSubmitAPIFunc.execute(visitManagementCreateSubmitAPI);*/
    }

    // Update by model
    public void updateItem(VisitModel item){
        for (int i = 0; i < getItemCount(); i++) {
            if (mDataset.get(i).getId().equals(item.getId())){
                item.setProgress_status(APIConstants.PROGRESS_NORMAL);
                mDataset.set(i, item);
                notifyItemChanged(i);
                break;
            }
        }
    }

    // Update by tag
    public void updateItem(String tag, String newId, boolean success) {
        for (int i = 0; i < getItemCount(); i++) {
            if (!TextUtils.isEmpty(mDataset.get(i).getId()) &&
                    mDataset.get(i).getId().equals(tag)) {
                if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_DELETE)) {
                    if (success) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    } else {
                        mDataset.get(i).setProgress_status(APIConstants.PROGRESS_NORMAL);
                        notifyItemChanged(i);
                    }
                    break;
                }
            } else {
                if (!TextUtils.isEmpty(mDataset.get(i).getTag()) &&
                        mDataset.get(i).getTag().equals(tag)) {
                    if (mDataset.get(i).getProgress_status().equals(APIConstants.PROGRESS_ADD)) {
                        if (success) {
                            mDataset.get(i).setId(newId);
                            mDataset.get(i).setProgress_status(APIConstants.PROGRESS_NORMAL);
                        } else {
                            mDataset.get(i).setProgress_status(APIConstants.PROGRESS_ADD_FAIL);
                        }
                        notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? VISIT : PROGRESS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VISIT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_visit_card, parent, false);
            viewHolder = new VisitViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.infiscroll_progress_layout, parent, false);
            viewHolder = new InfiScrollProgressVH(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VISIT) {
            VisitViewHolder visitViewHolder = (VisitViewHolder)holder;

            visitViewHolder.visitDate.setText(mDataset.get(position).getCreated_date());
            visitViewHolder.hospitalName.setText(mDataset.get(position).getHospital_name());
            visitViewHolder.doctorName.setText(mDataset.get(position).getDoctor_name());
            visitViewHolder.diagnose.setText(mDataset.get(position).getDiagnose());
            visitViewHolder.diagnosedDisease.setText(mDataset.get(position).getDiseases_display());

            if (mDataset.get(position).getImage_list().size() > 0) {
                visitViewHolder.imageRecycler.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                VisitImageAdapter visitImageAdapter = new VisitImageAdapter(context, width);
                visitImageAdapter.addList(mDataset.get(position).getImage_list());
                SnapHelper snapHelper = new GravityPagerSnapHelper(Gravity.START);
                setItemDecoration(visitViewHolder.imageRecycler, horizontalItemDecoration);
                visitViewHolder.imageRecycler.setLayoutManager(linearLayoutManager);
                visitViewHolder.imageRecycler.setAdapter(visitImageAdapter);
                visitViewHolder.imageRecycler.setOnFlingListener(null);
                snapHelper.attachToRecyclerView(visitViewHolder.imageRecycler);
            } else {
                visitViewHolder.imageRecycler.setVisibility(View.GONE);
            }

            if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD)){
                visitViewHolder.statusProgressBar.setVisibility(View.VISIBLE);
                visitViewHolder.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
                visitViewHolder.editBtn.setEnabled(false);
                visitViewHolder.deleteBtn.setEnabled(false);
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_DELETE)){
                visitViewHolder.statusProgressBar.setVisibility(View.VISIBLE);
                visitViewHolder.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
                visitViewHolder.editBtn.setEnabled(false);
                visitViewHolder.deleteBtn.setEnabled(false);
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD_FAIL)){
                visitViewHolder.statusProgressBar.setVisibility(View.GONE);
                visitViewHolder.progressFailImg.setVisibility(View.VISIBLE);
                visitViewHolder.progressFailImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reSubmitItem(holder.getAdapterPosition());
                    }
                });
                visitViewHolder.editBtn.setEnabled(false);
                visitViewHolder.deleteBtn.setEnabled(false);
            } else {
                visitViewHolder.statusProgressBar.setVisibility(View.GONE);
                visitViewHolder.editBtn.setEnabled(true);
                visitViewHolder.deleteBtn.setEnabled(true);
            }

            visitViewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)){
                        Intent intent = new Intent(context, VisitEditActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra(ConstantsManagement.VISIT_MODEL_EXTRA,
                                gson.toJson(mDataset.get(holder.getAdapterPosition())));
                        startActivityForResultInAdapterIntf.onStartActivityForResult(intent, RequestCodeList.VISIT_EDIT);
                    }
                }
            });

            visitViewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)){
                        createDeleteDialog(context, context.getString(R.string.visit_delete_confirmation),
                                "visit" + mDataset.get(holder.getAdapterPosition()).getId());
                    }
                }
            });
            
        } else {
            InfiScrollProgressVH infiScrollProgressVH = (InfiScrollProgressVH) holder;
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
    public void onDeleteConfirm(String deletionId) {
        for (int i = 0; i < getItemCount(); i++) {
            if (deletionId.equals("visit" + mDataset.get(i).getId())) {
                mDataset.get(i).setProgress_status(APIConstants.PROGRESS_DELETE);
                notifyItemChanged(i);

                VisitManagementDeleteSubmitAPI visitManagementDeleteSubmitAPI = new VisitManagementDeleteSubmitAPI();
                visitManagementDeleteSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(context);
                visitManagementDeleteSubmitAPI.data.query.visit_id = mDataset.get(i).getId();

                VisitManagementDeleteSubmitAPIFunc visitManagementDeleteSubmitAPIFunc =
                        new VisitManagementDeleteSubmitAPIFunc(context, VisitAdapter.this, mDataset.get(i).getId());
                visitManagementDeleteSubmitAPIFunc.execute(visitManagementDeleteSubmitAPI);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onFinishVisitDelete(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            VisitManagementDeleteSubmitAPI output = gson.fromJson(responseAPI.status_response, VisitManagementDeleteSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    if (tag.equals(mDataset.get(i).getId())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                        break;
                    }
                }
            } else {
                updateItem(tag, output.data.query.visit_id, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if (responseAPI.status_code == 504) {
            updateItem(tag, tag, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity) context).forceLogout();
        } else {
            updateItem(tag, tag, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    static class VisitViewHolder extends FastBaseViewHolder {

        @BindView(R.id.management_status_progress_progressbar)
        ProgressBar statusProgressBar;
        @BindView(R.id.management_status_progress_fail)
        ImageView progressFailImg;
        @BindView(R.id.visit_card_date)
        CustomFontTextView visitDate;
        @BindView(R.id.visit_card_hospital_name)
        CustomFontTextView hospitalName;
        @BindView(R.id.visit_card_doctor_name)
        CustomFontTextView doctorName;
        @BindView(R.id.visit_card_diagnose)
        CustomFontTextView diagnose;
        @BindView(R.id.visit_card_diagnosed_diseases)
        CustomFontTextView diagnosedDisease;
        @BindView(R.id.visit_card_images)
        RecyclerView imageRecycler;
        @BindView(R.id.management_operations_edit_btn)
        ImageButton editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageButton deleteBtn;


        public VisitViewHolder(View view) {
            super(view);

        }
    }
}
