package com.med.fast.management.allergy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.ConstantsManagement;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.RequestCodeList;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.StartActivityForResultInAdapterIntf;
import com.med.fast.Utils;
import com.med.fast.api.APIConstants;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.ItemAddedEvent;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.DeleteConfirmIntf;
import com.med.fast.management.allergy.allergyinterface.AllergyManagementCreateDeleteIntf;
import com.med.fast.management.allergy.api.AllergyManagementCreateSubmitAPI;
import com.med.fast.management.allergy.api.AllergyManagementCreateSubmitAPIFunc;
import com.med.fast.management.allergy.api.AllergyManagementDeleteAPI;
import com.med.fast.management.allergy.api.AllergyManagementDeleteAPIFunc;
import com.med.fast.viewholders.InfiScrollProgressVH;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class AllergyManagementAdapter extends FastBaseRecyclerAdapter implements AllergyManagementCreateDeleteIntf, DeleteConfirmIntf {

    private final int PROGRESS = 0;
    private final int ALLERGY = 1;
    private Context context;
    private List<AllergyManagementModel> mDataset = new ArrayList<>();
    private boolean failLoad = false;
    private StartActivityForResultInAdapterIntf startActivityForResultInAdapterIntf;
    private String userId;
    private boolean initial = false;

    public AllergyManagementAdapter(Context context, StartActivityForResultInAdapterIntf intf, boolean initial) {
        this.context = context;
        this.userId = SharedPreferenceUtilities.getUserId(context);
        if (intf != null){
            this.startActivityForResultInAdapterIntf = intf;
        }
        this.initial = initial;
        deleteConfirmIntf = this;
    }

    public void addList(List<AllergyManagementModel> dataset) {
        for (AllergyManagementModel model :
                dataset) {
            this.mDataset.add(model);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addSingle(AllergyManagementModel accident) {
        this.mDataset.add(accident);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addSingle(AllergyManagementModel accident, int position) {
        this.mDataset.add(position, accident);
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

    // Update by model
    public void updateItem(AllergyManagementModel item) {
        for (int i = 0; i < getItemCount(); i++) {
            if (mDataset.get(i).getId().equals(item.getId())) {
                item.setProgress_status("0");
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

    public void submitItem() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.management_allergy_popup);
        dialog.setCanceledOnTouchOutside(false);

        final CustomFontEditText causative = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_causative_et);
        final CustomFontRadioButton drugTypeYes = (CustomFontRadioButton) dialog.findViewById(R.id.allergy_popup_drugtype_rb_yes);
        final CustomFontEditText reaction = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_reaction_et);
        final CustomFontEditText firstExp = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_firsttime_et);
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
        mAwesomeValidation.addValidation(causative, RegexTemplate.NOT_EMPTY, context.getString(R.string.causative_agent_empty));
        mAwesomeValidation.addValidation(reaction, RegexTemplate.NOT_EMPTY, context.getString(R.string.reaction_empty));
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()) {
                    String causativeString = Utils.processStringForAPI(causative.getText().toString());
                    String drugTypeString = String.valueOf(drugTypeYes.isChecked());
                    String reactionString = Utils.processStringForAPI(reaction.getText().toString());
                    String firstExpString = Utils.processStringForAPI(firstExp.getText().toString());

                    AllergyManagementModel allergyManagementModel = new AllergyManagementModel();
                    allergyManagementModel.setAgent(causativeString);
                    allergyManagementModel.setIs_drug(drugTypeYes.isChecked());
                    allergyManagementModel.setReaction(reactionString);
                    allergyManagementModel.setFirst_experience(firstExpString);
                    allergyManagementModel.setCreated_date(Utils.getCurrentDate());
                    allergyManagementModel.setProgress_status(APIConstants.PROGRESS_ADD);
                    allergyManagementModel.setTag(causativeString + String.valueOf(getItemCount()));
                    mDataset.add(0, allergyManagementModel);
                    notifyItemInserted(0);
                    EventBus.getDefault().post(new ItemAddedEvent());

                    AllergyManagementCreateSubmitAPI allergyManagementCreateSubmitAPI = new AllergyManagementCreateSubmitAPI();
                    allergyManagementCreateSubmitAPI.data.query.user_id = userId;
                    allergyManagementCreateSubmitAPI.data.query.agent = causativeString;
                    allergyManagementCreateSubmitAPI.data.query.is_drug = drugTypeString;
                    allergyManagementCreateSubmitAPI.data.query.reaction = reactionString;
                    allergyManagementCreateSubmitAPI.data.query.first_experience = firstExpString;

                    AllergyManagementCreateSubmitAPIFunc allergyManagementCreateSubmitAPIFunc = new AllergyManagementCreateSubmitAPIFunc(context, AllergyManagementAdapter.this,
                            allergyManagementModel.getTag(), initial);
                    allergyManagementCreateSubmitAPIFunc.execute(allergyManagementCreateSubmitAPI);

                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void reSubmitItem(int position) {
        AllergyManagementCreateSubmitAPI allergyManagementCreateSubmitAPI = new AllergyManagementCreateSubmitAPI();
        allergyManagementCreateSubmitAPI.data.query.user_id = userId;
        allergyManagementCreateSubmitAPI.data.query.agent = mDataset.get(position).getAgent();
        allergyManagementCreateSubmitAPI.data.query.is_drug = String.valueOf(mDataset.get(position).getIs_drug());
        allergyManagementCreateSubmitAPI.data.query.reaction = mDataset.get(position).getReaction();
        allergyManagementCreateSubmitAPI.data.query.first_experience = mDataset.get(position).getFirst_experience();

        AllergyManagementCreateSubmitAPIFunc allergyManagementCreateSubmitAPIFunc = new AllergyManagementCreateSubmitAPIFunc(context, AllergyManagementAdapter.this,
                mDataset.get(position).getTag(), initial);
        allergyManagementCreateSubmitAPIFunc.execute(allergyManagementCreateSubmitAPI);
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
        if (getItemViewType(position) == ALLERGY) {
            AllergyManagementVH allergyManagementVH = (AllergyManagementVH) holder;
            allergyManagementVH.agent.setText(mDataset.get(position).getAgent());
            allergyManagementVH.drug.setText(mDataset.get(position).getDrug_display());
            allergyManagementVH.reaction.setText(mDataset.get(position).getReaction_display());
            allergyManagementVH.firstExperience.setText(mDataset.get(position).getFirst_experience_display());
            allergyManagementVH.date.setText(mDataset.get(position).getCreated_date());

            if (initial)allergyManagementVH.editBtn.setVisibility(View.GONE);
            else allergyManagementVH.editBtn.setVisibility(View.VISIBLE);
            if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD)) {
                allergyManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                allergyManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_tosca));
                allergyManagementVH.editBtn.setEnabled(false);
                allergyManagementVH.deleteBtn.setEnabled(false);
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_DELETE)) {
                allergyManagementVH.statusProgressBar.setVisibility(View.VISIBLE);
                allergyManagementVH.statusProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.progressbar_red));
                allergyManagementVH.editBtn.setEnabled(false);
                allergyManagementVH.deleteBtn.setEnabled(false);
            } else if (mDataset.get(position).getProgress_status().equals(APIConstants.PROGRESS_ADD_FAIL)) {
                allergyManagementVH.statusProgressBar.setVisibility(View.GONE);
                allergyManagementVH.progressFailImg.setVisibility(View.VISIBLE);
                allergyManagementVH.progressFailImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reSubmitItem(holder.getAdapterPosition());
                    }
                });
                allergyManagementVH.editBtn.setEnabled(false);
                allergyManagementVH.deleteBtn.setEnabled(false);
            } else {
                allergyManagementVH.statusProgressBar.setVisibility(View.GONE);
                allergyManagementVH.editBtn.setEnabled(true);
                allergyManagementVH.deleteBtn.setEnabled(true);
            }

            allergyManagementVH.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)) {
                        Intent intent = new Intent(context, AllergyEditActivity.class);
                        Gson gson = new Gson();
                        intent.putExtra(ConstantsManagement.ALLERGY_MODEL_EXTRA,
                                gson.toJson(mDataset.get(holder.getAdapterPosition())));
                        startActivityForResultInAdapterIntf.onStartActivityForResult(intent, RequestCodeList.ALLERGY_EDIT);
                    }
                }
            });

            allergyManagementVH.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(holder.getAdapterPosition()).getProgress_status().equals(APIConstants.PROGRESS_NORMAL)) {
                        createDeleteDialog(context, context.getString(R.string.allergy_delete_confirmation), "allergy" + mDataset.get(holder.getAdapterPosition()).getId());
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
            if (deletionId.equals("allergy" + mDataset.get(i).getId())) {
                mDataset.get(i).setProgress_status(APIConstants.PROGRESS_DELETE);
                notifyItemChanged(i);

                AllergyManagementDeleteAPI allergyManagementDeleteAPI = new AllergyManagementDeleteAPI();
                allergyManagementDeleteAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(context);
                allergyManagementDeleteAPI.data.query.allergy_id = mDataset.get(i).getId();

                AllergyManagementDeleteAPIFunc allergyManagementDeleteAPIFunc = new AllergyManagementDeleteAPIFunc(context, mDataset.get(i).getId());
                allergyManagementDeleteAPIFunc.setDelegate(AllergyManagementAdapter.this);
                allergyManagementDeleteAPIFunc.execute(allergyManagementDeleteAPI);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onFinishAllergyManagementCreateSubmit(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AllergyManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementCreateSubmitAPI.class);
            if (output.data.status.code.equals("200")) {
                updateItem(tag, output.data.results.new_allergy_id, true);
            } else {
                updateItem(tag, APIConstants.NO_ID, false);
                Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        } else if (responseAPI.status_code == 504) {
            updateItem(tag, APIConstants.NO_ID, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        } else if (responseAPI.status_code == 401 ||
                responseAPI.status_code == 505) {
            ((FastBaseActivity) context).forceLogout();
        } else {
            updateItem(tag, APIConstants.NO_ID, false);
            Toast.makeText(context, context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinishAllergyManagementDelete(ResponseAPI responseAPI, String tag) {
        if (responseAPI.status_code == 200) {
            Gson gson = new Gson();
            AllergyManagementDeleteAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementDeleteAPI.class);
            if (output.data.status.code.equals("200")) {
                for (int i = 0; i < getItemCount(); i++) {
                    if (output.data.query.allergy_id.equals(mDataset.get(i).getId())) {
                        mDataset.remove(i);
                        notifyItemRemoved(i);
                    }
                }
            } else {
                updateItem(tag, output.data.query.allergy_id, false);
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

    static class AllergyManagementVH extends FastBaseViewHolder {

        @BindView(R.id.management_status_progress_progressbar)
        ProgressBar statusProgressBar;
        @BindView(R.id.management_status_progress_fail)
        ImageView progressFailImg;
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
        ImageButton editBtn;
        @BindView(R.id.management_operations_delete_btn)
        ImageButton deleteBtn;

        public AllergyManagementVH(View view) {
            super(view);

        }
    }
}
