package com.med.fast.management.labresult;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.med.fast.FastAppController;
import com.med.fast.FastBaseActivity;
import com.med.fast.FastBaseRecyclerAdapter;
import com.med.fast.MediaUtils;
import com.med.fast.R;
import com.med.fast.UriUtils;
import com.med.fast.ViewImageActivity;
import com.med.fast.api.APIConstants;
import com.med.fast.utils.GlideUtils;
import com.med.fast.viewholders.GeneralImageVH;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Murvie on 5/6/2017. FM
 */

public class LabResultImageShowAdapter extends FastBaseRecyclerAdapter {

    private Context context;
    private List<LabResultImgModel> mDataset = new ArrayList<>();

    public LabResultImageShowAdapter(Context context) {
        super(false);
        this.context = context;
    }

    public void addList(List<LabResultImgModel> dataset) {
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void addSingle(LabResultImgModel labResultImageItem) {
        this.mDataset.add(labResultImageItem);
        notifyItemInserted(mDataset.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.management_visit_card_image, parent, false);
        return new GeneralImageVH(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final GeneralImageVH generalImageVH = (GeneralImageVH) holder;
        generalImageVH.imageWrapper.getLayoutParams().width = FastAppController.screenWidth * 30 / 100;
        generalImageVH.imageWrapper.getLayoutParams().height = FastAppController.screenWidth * 30 / 100;

        Glide.with(context)
                .load(APIConstants.WEB_URL + mDataset.get(position).getPath())
                .apply(
                        GlideUtils.getDefaultRequestOptions()
                                .centerCrop()
                                .dontAnimate()
                )
                .into(generalImageVH.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra(ViewImageActivity.IMAGE_PATH_EXTRA, mDataset.get(holder.getAdapterPosition()).getPath());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((FastBaseActivity) context, generalImageVH.image, context.getString(R.string.view_image_transition));
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(((GeneralImageVH)holder).image.getContext()).clear(((GeneralImageVH)holder).image);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
