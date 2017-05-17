package com.med.fast.management.visit;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.ImagePlaceholderVH;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.UriUtils;
import com.med.fast.api.APIConstants;
import com.med.fast.customviews.CustomFontTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitImageAdapter extends FastBaseRecyclerAdapter {

    private int width;

    private Context context;
    private List<VisitImageItem> mDataset = new ArrayList<>();

    public VisitImageAdapter(Context context, int width) {
        super(false);
        this.context = context;
        this.width = width;
    }

    public void addList(List<VisitImageItem> dataset) {
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.management_visit_card_image, parent, false);
        return new ImagePlaceholderVH(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        VisitImageVH visitImageVH = (VisitImageVH) holder;
        visitImageVH.imageWrapper.getLayoutParams().width = width * 30 / 100;
        visitImageVH.imageWrapper.getLayoutParams().height = width * 30 / 100;

        Glide.with(context)
                .load(APIConstants.WEB_URL + mDataset.get(position).getUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .placeholder(MediaUtils.image_placeholder_black)
                .error(MediaUtils.image_error_black)
                .into(visitImageVH.image);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class VisitImageVH extends FastBaseViewHolder {

        @BindView(R.id.visit_card_image_wrapper)
        LinearLayout imageWrapper;
        @BindView(R.id.visit_card_image_image_view)
        ImageView image;

        public VisitImageVH(View itemView) {
            super(itemView);
        }
    }
}
