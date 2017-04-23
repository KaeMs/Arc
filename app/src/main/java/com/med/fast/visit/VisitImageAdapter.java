package com.med.fast.visit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitImageAdapter extends FastBaseRecyclerAdapter {

    private Context context;
    private List<VisitImageItem> mDataset = new ArrayList<>();

    public VisitImageAdapter(Context context){
        this.context = context;
    }

    public void addList(List<VisitImageItem> dataset){
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View visitImgView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_visit_card_image, parent, false);
        return new VisitImageVH(visitImgView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        VisitImageVH visitImageVH = (VisitImageVH)holder;
        Glide.with(context)
                .load(/*Constants.WEB_ADDRESS + */mDataset.get(position).path)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                /*.placeholder(ConstantDrawables.imagePlacholder)
                .error(ConstantDrawables.imageError)*/
                .into(visitImageVH.image);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class VisitImageVH extends FastBaseViewHolder {

        @BindView(R.id.visit_card_image_imageView)
        ImageView image;

        public VisitImageVH(View itemView) {
            super(itemView);
        }
    }
}
