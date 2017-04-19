package com.med.fast.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.models.Allergy;
import com.med.fast.signup.InitialDataAllergyActivity;

import java.util.ArrayList;
import java.util.List;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/11/2017. FM
 */

public class AllergyAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Allergy> allergies = new ArrayList<>();

    public AllergyAdapter(Context context){
        this.context = context;
    }

    public void addSingle(Allergy allergy){
        this.allergies.add(allergy);
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.initialdata_allergy_item, parent, false);
        return new AllergyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        AllergyViewHolder allergyViewHolder = (AllergyViewHolder)holder;

        allergyViewHolder.agent.setText(allergies.get(position).allergy_causative);
        allergyViewHolder.drugAllergy.setText(allergies.get(position).allergy_drug);
        allergyViewHolder.reaction.setText(allergies.get(position).allergy_reaction);
        allergyViewHolder.firstExperience.setText(allergies.get(position).allergy_first_experience);

        allergyViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allergies.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.allergy_popup);
                dialog.setCanceledOnTouchOutside(true);

                final CustomFontEditText causative = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_causative_et);
                final CustomFontRadioButton drugTypeYes = (CustomFontRadioButton) dialog.findViewById(R.id.allergy_popup_drugtype_rb_yes);
                final CustomFontEditText reaction = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_reaction_et);
                final CustomFontEditText firstExp = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_firsttime_et);
                CustomFontButton cancelBtn = (CustomFontButton) dialog.findViewById(R.id.allergy_popup_cancel_btn);
                CustomFontButton addBtn = (CustomFontButton) dialog.findViewById(R.id.allergy_popup_add_btn);

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(context);
                mAwesomeValidation.addValidation(causative, RegexTemplate.NOT_EMPTY, context.getString(R.string.causative_agent_empty));
                mAwesomeValidation.addValidation(reaction, RegexTemplate.NOT_EMPTY, context.getString(R.string.reaction_empty));
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAwesomeValidation.validate()) {
                            allergies.get(holder.getAdapterPosition()).allergy_causative = causative.getText().toString();
                            allergies.get(holder.getAdapterPosition()).allergy_drug = drugTypeYes.isChecked() ? "yes" : "no";
                            allergies.get(holder.getAdapterPosition()).allergy_reaction = reaction.getText().toString();
                            allergies.get(holder.getAdapterPosition()).allergy_first_experience = firstExp.getText().toString();

                            notifyItemChanged(holder.getAdapterPosition());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return allergies.size();
    }
}
