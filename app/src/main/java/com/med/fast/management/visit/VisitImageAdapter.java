package com.med.fast.management.visit;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.FastBaseViewHolder;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.ViewImageActivity;
import com.med.fast.api.APIConstants;
import com.med.fast.utils.GlideUtils;

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
        return new VisitImageVH(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final VisitImageVH visitImageVH = (VisitImageVH) holder;
        visitImageVH.imageWrapper.getLayoutParams().width = width * 30 / 100;
        visitImageVH.imageWrapper.getLayoutParams().height = width * 30 / 100;

        Glide.with(context)
                .load(APIConstants.WEB_URL + mDataset.get(position).getPath())
                .apply(
                        GlideUtils.getDefaultRequestOptions()
                )
                .into(visitImageVH.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra(ViewImageActivity.IMAGE_PATH_EXTRA, mDataset.get(holder.getAdapterPosition()).getPath());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((FastBaseActivity) context, visitImageVH.image, ViewCompat.getTransitionName(visitImageVH.image));
                context.startActivity(intent, options.toBundle());
            }
        });
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
