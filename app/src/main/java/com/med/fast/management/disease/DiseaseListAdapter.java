package com.med.fast.management.disease;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.visit.VisitDiseaseModel;

import java.util.List;

/**
 * Created by Kevin Murvie on 5/23/2017. FM
 */

public class DiseaseListAdapter extends ArrayAdapter<VisitDiseaseModel> {

    public DiseaseListAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<VisitDiseaseModel> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.layout_textview, parent, false);
        }

        VisitDiseaseModel visitDiseaseModel = getItem(position);

        if (visitDiseaseModel != null) {
            CustomFontTextView diseaseName = (CustomFontTextView) v.findViewById(R.id.textview_tv);
            diseaseName.setText(visitDiseaseModel.name);
        }
        return v;
    }
}
