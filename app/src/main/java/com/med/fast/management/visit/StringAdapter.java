package com.med.fast.management.visit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontTextView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 5/2/2017. FM
 */

public class StringAdapter extends FastBaseRecyclerAdapter {
    private Context context;
    private List<String> mDataset;

    public StringAdapter(Context context){
        this.context = context;
    }

    public void addList(List<String> newDataset){
        this.mDataset.addAll(newDataset);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_textview, parent, false);
        return new StringVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class StringVH extends FastBaseViewHolder{

        @BindView(R.id.textview_tv)
        CustomFontTextView textview;

        public StringVH(View itemView) {
            super(itemView);
        }
    }
}
