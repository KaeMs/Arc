package com.med.fast.management.labresult;

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
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.visit.VisitAddActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 5/6/2017. FM
 */

public class LabResultImageAdapter extends FastBaseRecyclerAdapter {

    private int PLACEHOLDER = 0;
    private int IMAGE = 1;
    private int savedPos;
    private int width;

    private Context context;
    private List<LabResultImageItem> mDataset = new ArrayList<>();

    public LabResultImageAdapter(Context context) {
        super(false);
        this.context = context;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void addList(List<LabResultImageItem> dataset) {
        this.mDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void updateImage(LabResultImageItem labResultImageItem) {
        mDataset.set(savedPos, labResultImageItem);
    }

    public void addSingle(LabResultImageItem labResultImageItem) {
        this.mDataset.add(labResultImageItem);
        notifyItemInserted(mDataset.size() - 1);
    }

    public void updatemDataset(LabResultImageItem labResultImageItem) {
        if (mDataset.get(savedPos) == null ||
                !labResultImageItem.getImage_uri().equals(mDataset.get(savedPos).getImage_uri())) {
            this.mDataset.set(savedPos, new LabResultImageItem());
            this.mDataset.get(savedPos).setImage_uri(MediaUtils.compressImage(context, labResultImageItem.getImage_uri()));
        }
        notifyItemChanged(savedPos);
//        ((PostProductActivity)context).smoothScrollToEnd();

        int filledCounter = 0;
        for (LabResultImageItem item :
                mDataset) {
            if (item != null) {
                filledCounter++;
            }
        }
        if (filledCounter == mDataset.size()) {
            this.mDataset.add(null);
            notifyItemInserted(mDataset.size() - 1);
        }
    }

    public List<LabResultImageItem> getmDataset(){
        List<LabResultImageItem> returnMDataset = new ArrayList<>();
        for (LabResultImageItem item:
             mDataset) {
            if (item != null){
                returnMDataset.add(item);
            }
        }
        return returnMDataset;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataset.get(position) == null) {
            return PLACEHOLDER;
        }
        return IMAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == PLACEHOLDER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_visit_card_image, parent, false);
            vh = new ImagePlaceholderVH(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.management_visit_card_image, parent, false);
            vh = new VisitImageVH(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == IMAGE) {
            VisitImageVH visitImageVH = (VisitImageVH) holder;
            visitImageVH.imageWrapper.getLayoutParams().width = width * 30 / 100;
            visitImageVH.imageWrapper.getLayoutParams().height = width * 30 / 100;

            Glide.with(context)
                    .load(mDataset.get(position).getImage_uri())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .placeholder(MediaUtils.image_placeholder_black)
                    .error(MediaUtils.image_error_black)
                    .into(visitImageVH.image);

        } else {
            ImagePlaceholderVH imagePlaceholderVH = (ImagePlaceholderVH) holder;
            imagePlaceholderVH.placeholderWrapper.getLayoutParams().width = width * 30 / 100;
            imagePlaceholderVH.placeholderWrapper.getLayoutParams().height = width * 30 / 100;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataset.get(holder.getAdapterPosition()) != null) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.management_visit_card_image_editpopup);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();

                    CustomFontTextView changeImage = (CustomFontTextView) dialog.findViewById(R.id.visit_card_imagepopup_imageDescText);
                    CustomFontTextView deleteImage = (CustomFontTextView) dialog.findViewById(R.id.visit_card_imagepopup_deleteImageText);

                    changeImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            savedPos = holder.getAdapterPosition();

                            dialog.dismiss();
                        }
                    });

                    deleteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDataset.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            dialog.dismiss();
                        }
                    });

                } else {
                    ((VisitAddActivity)context).addNewImage();
                }
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
