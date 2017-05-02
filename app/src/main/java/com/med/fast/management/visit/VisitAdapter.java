package com.med.fast.management.visit;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.Utils;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.DeleteConfirmEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.visit.api.VisitManagementCreateSubmitAPI;
import com.med.fast.management.visit.api.VisitManagementCreateSubmitAPIFunc;
import com.med.fast.management.visit.api.VisitManagementDeleteSubmitAPI;
import com.med.fast.management.visit.visitinterface.VisitCreateDeleteIntf;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitAdapter extends FastBaseRecyclerAdapter implements VisitCreateDeleteIntf {

    private final int PROGRESS = 0;
    private final int VISIT = 1;
    private Context context;
    private List<VisitModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private String deletionId = "";
    private StartActivityForResultInAdapterIntf startActivityForResultInAdapterIntf;
    private String userId;
    private ArrayAdapter<String> leftRecyclerAdapter;
    private List<String> leftDataset;
    private ArrayAdapter<String> rightRecyclerAdapter;
    private List<String> rightDataset;

    public VisitAdapter(Context context, StartActivityForResultInAdapterIntf intf){
        super(true);
        this.context = context;
        this.startActivityForResultInAdapterIntf = intf;
        this.userId = SharedPreferenceUtilities.getUserId(context);
        this.leftRecyclerAdapter = new ArrayAdapter<>(context, R.layout.layout_textview, R.id.textview_tv);
        leftDataset = new ArrayList<>();
        this.rightRecyclerAdapter = new ArrayAdapter<>(context, R.layout.layout_textview, R.id.textview_tv);
        rightDataset = new ArrayList<>();
    }

    public void addList(List<VisitModel> dataset) {
        for (VisitModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(VisitModel visit) {
        this.mDataset.add(0, visit);
        notifyItemInserted(getItemCount() - 1);
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
        }
    }

    public void setFailLoad(boolean failLoad) {
        this.failLoad = failLoad;
        notifyItemChanged(getItemCount() - 1);
        if (!failLoad) {
            removeProgress();
        }
    }

    // Update by model
    public void updateItem(VisitModel item) {
        for (int i = getItemCount() - 1; i > 0; i++) {
            if (mDataset.get(i).getVisit_id().equals(item.getVisit_id()) &&
                    mDataset.get(i).getProgress_status().equals("1")) {
                item.setProgress_status("0");
                mDataset.set(i, item);
                break;
            }
        }
    }

    // Update by tag
    public void updateItem(String tag, boolean success){
        for (int i = getItemCount() - 1; i > 0; i++){
            if (tag != null){
                if (mDataset.get(i).getTag().equals(tag)){
                    mDataset.get(i).setProgress_status(success ? "0" : "3");
                    notifyItemChanged(i);
                    break;
                }
            } else {
                if (mDataset.get(i).getProgress_status().equals("1")){
                    mDataset.get(i).setProgress_status("3");
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void submitItem(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.management_visit_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final CustomFontEditText doctorName = (CustomFontEditText) dialog.findViewById(R.id.visit_popup_doctor_name);
        final CustomFontEditText hospitalName = (CustomFontEditText) dialog.findViewById(R.id.visit_popup_hospital_name);
        final CustomFontEditText diagnose = (CustomFontEditText) dialog.findViewById(R.id.visit_popup_diagnose);
        RecyclerView imageRecycler = (RecyclerView) dialog.findViewById(R.id.visit_popup_imagerecycler);
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
                    visitModel.setVisit_id("");
                    visitModel.setOwner_id(userId);
                    visitModel.setCreated_date(Utils.getCurrentDate());
                    visitModel.setHospital_name(hospitalNameString);
                    visitModel.setDoctor_name(doctorNameString);
                    visitModel.setDiagnose(diagnoseString);
                    visitModel.setDisease("disease");

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

                    VisitManagementCreateSubmitAPIFunc visitManagementCreateSubmitAPIFunc = new VisitManagementCreateSubmitAPIFunc(context);
                    visitManagementCreateSubmitAPIFunc.setDelegate(VisitAdapter.this);
                    visitManagementCreateSubmitAPIFunc.execute(visitManagementCreateSubmitAPI);
                }
            }
        });
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VISIT) {
            VisitViewHolder visitViewHolder = (VisitViewHolder)holder;

            visitViewHolder.visitDate.setText(mDataset.get(position).getCreated_date());
            visitViewHolder.hospitalName.setText(mDataset.get(position).getHospital_name());
            visitViewHolder.doctorName.setText(mDataset.get(position).getDoctor_name());
            visitViewHolder.diagnose.setText(mDataset.get(position).getDiagnose());
            visitViewHolder.diagnosedDisease.setText(mDataset.get(position).getDisease());

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            VisitImageAdapter visitImageAdapter = new VisitImageAdapter(context);
            SnapHelper snapHelper = new PagerSnapHelper();
            visitViewHolder.imageRecycler.setLayoutManager(linearLayoutManager);
            visitViewHolder.imageRecycler.setAdapter(visitImageAdapter);
            snapHelper.attachToRecyclerView(visitViewHolder.imageRecycler);
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

    @Subscribe
    public void onDeleteConfirm(DeleteConfirmEvent deleteConfirmEvent) {
        for (int i = 0; i < getItemCount(); i++) {
            if (deletionId.equals(mDataset.get(i).getVisit_id())) {
                mDataset.get(i).setProgress_status("2");
                notifyItemChanged(i);

                VisitManagementDeleteSubmitAPI visitManagementDeleteSubmitAPI = new VisitManagementDeleteSubmitAPI();
                visitManagementDeleteSubmitAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(context);
                visitManagementDeleteSubmitAPI.data.query.visit_id = mDataset.get(i).getVisit_id();

                /*AllergyManagementDeleteAPIFunc allergyManagementDeleteAPIFunc = new AllergyManagementDeleteAPIFunc(context);
                allergyManagementDeleteAPIFunc.setDelegate(AllergyManagementAdapter.this);
                allergyManagementDeleteAPIFunc.execute(visitManagementDeleteSubmitAPI);*/
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onFinishVisitCreate(ResponseAPI responseAPI) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            VisitManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, VisitManagementCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                updateItem(output.data.query.tag, true);
            } else {
                updateItem(output.data.query.tag, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if (responseAPI.status_code == 504) {
            updateItem(null, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity) context).forceLogout();
        } else {
            updateItem(null, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishVisitDelete(ResponseAPI responseAPI) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            VisitManagementDeleteSubmitAPI output = gson.fromJson(responseAPI.status_response, VisitManagementDeleteSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    /*if (output.data.query.disease_id_list.equals(mDataset.get(i).getAllergy_id())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    }*/
                }
            } else {
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if (responseAPI.status_code == 504) {
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity) context).forceLogout();
        } else {
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    static class VisitViewHolder extends FastBaseViewHolder {

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
        @BindView(R.id.visit_card_edit)
        ImageView editBtn;
        @BindView(R.id.visit_card_delete)
        ImageView deleteBtn;


        public VisitViewHolder(View view) {
            super(view);

        }
    }
}
