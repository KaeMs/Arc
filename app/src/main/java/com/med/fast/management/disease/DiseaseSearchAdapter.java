package com.med.fast.management.disease;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * MI on 5/3/2016.
 */

public class DiseaseSearchAdapter extends ArrayAdapter<DiseaseNameModel> implements Filterable {

    public interface Listener {
        void onExampleModelClicked(DiseaseNameModel model);
    }

    private final Listener mListener;
    private Context context;
    private int resource;
    private List<DiseaseNameModel> mDataset;
    private List<DiseaseNameModel> filteredMDataset;

    public DiseaseSearchAdapter(Context context, int resource, Listener listener) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.mDataset = new ArrayList<>();
        this.filteredMDataset = new ArrayList<>();
        this.mListener = listener;
    }

    public void changeItem(List<DiseaseNameModel> diseaseNameModels){
        this.mDataset.clear();
        notifyDataSetChanged();
        this.mDataset.addAll(diseaseNameModels);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredMDataset.size();
    }

    @Override
    public DiseaseNameModel getItem(int position) {
        return this.filteredMDataset.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<DiseaseNameModel> tempDiseaseNameModel = new ArrayList<DiseaseNameModel>();
                if(constraint != null) {
                    int length = mDataset.size();
                    int i = 0;
                    while(i<length){

                        //do whatever you wanna do here
                        //adding result set output array

                        DiseaseNameModel item = mDataset.get(i);
//                        tempDiseaseNameModel.add(item);
                        if (item.getName().toLowerCase().contains(constraint.toString().toLowerCase()))  {
                            tempDiseaseNameModel.add(item);
                        }
                        i++;
                    }
                    //following two lines is very important
                    //as publish result can only take FilterResults objects
                    filterResults.values = tempDiseaseNameModel;
                    filterResults.count = tempDiseaseNameModel.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    filteredMDataset = (List<DiseaseNameModel>) results.values;
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        try {
            if(convertView==null){
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resource, parent, false);
            }
            final DiseaseNameModel objectItem = filteredMDataset.get(position);
            CustomFontTextView diseaseName = (CustomFontTextView) convertView.findViewById(R.id.disease_search_item_name);
            diseaseName.setText(objectItem.getName());

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onExampleModelClicked(objectItem);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return convertView;

    }
}
