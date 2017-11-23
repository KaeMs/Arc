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
import com.google.gson.Gson;
import com.med.fast.FastAppController;
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
 * Created by Kevin Murvie on 5/6/2017. FM
 */

public class LabResultImageAddAdapter extends FastBaseRecyclerAdapter {

    private int PLACEHOLDER = 0;
    private int IMAGE = 1;
    private int savedPos;

    private Context context;
    private List<LabResultImgModel> mDataset = new ArrayList<>();
    private List<LabResultImgModel> editMDataset = new ArrayList<>();

    public LabResultImageAddAdapter(Context context) {
        super(false);
        this.context = context;
    }

    public void addList(List<LabResultImgModel> dataset) {
        this.mDataset.addAll(dataset);
        this.editMDataset.addAll(dataset);
        notifyDataSetChanged();
    }

    public void addSingle(LabResultImgModel labResultImgModel) {
        this.mDataset.add(labResultImgModel);
        notifyItemInserted(mDataset.size() - 1);
    }

    public void updatemDataset(LabResultImgModel labResultImgModel) {
        if (mDataset.get(savedPos) == null ||
                !labResultImgModel.getImage_uri().equals(mDataset.get(savedPos).getImage_uri())) {
            this.mDataset.set(savedPos, labResultImgModel);
        }
        notifyItemChanged(savedPos);
//        ((PostProductActivity)context).smoothScrollToEnd();

        int filledCounter = 0;
        for (LabResultImgModel item :
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

    public List<LabResultImgModel> getmDataset(){
        List<LabResultImgModel> returnMDataset = new ArrayList<>();
        for (LabResultImgModel item:
             mDataset) {
            if (item != null){
                returnMDataset.add(item);
            }
        }
        return returnMDataset;
    }

    public List<File> getUploadFile(){
        List<File> returnFile = new ArrayList<>();
        for (LabResultImgModel item :
                mDataset) {
            if (item != null &&
                    item.getImage_uri() != null){
                returnFile.add(new File(UriUtils.getPath(context, item.getImage_uri())));
            }
        }
        return returnFile;
    }

    public String getGson(){
        Gson gson = new Gson();
        LabResultImgUploadModelWrapper labResultImgUploadModelWrapper = new LabResultImgUploadModelWrapper();
        for (LabResultImgModel item :
                mDataset) {
            if (item != null){
                labResultImgUploadModelWrapper.img_list.add(item.getLabResultImgUploadModel());
            }
        }

        if (labResultImgUploadModelWrapper.img_list.size() > 0)
            return gson.toJson(labResultImgUploadModelWrapper);
        else return "default";
    }

    public String getEditGson(){
        Gson gson = new Gson();
        LabResultImgUploadModelWrapper labResultImgUploadModelWrapper = new LabResultImgUploadModelWrapper();
        for (LabResultImgModel item :
                editMDataset) {
            if (item != null){
                labResultImgUploadModelWrapper.img_list.add(item.getLabResultImgUploadModel());
            }
        }

        if (labResultImgUploadModelWrapper.img_list.size() > 0)
            return gson.toJson(labResultImgUploadModelWrapper);
        else return "default";
    }

    public String getAddedImgGson(){
        Gson gson = new Gson();
        LabResultImgModelWrapper labResultImgModelWrapper = new LabResultImgModelWrapper();
        for (LabResultImgModel item :
                mDataset) {
            if (item != null &&
                    item.getImage_uri() != null){
                labResultImgModelWrapper.img_list.add(item);
            }
        }

        if (labResultImgModelWrapper.img_list.size() > 0)
        return gson.toJson(labResultImgModelWrapper);
        else return "default";
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
                    .inflate(R.layout.image_placeholder, parent, false);
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
            visitImageVH.imageWrapper.getLayoutParams().width = FastAppController.screenWidth * 30 / 100;
            visitImageVH.imageWrapper.getLayoutParams().height = FastAppController.screenWidth * 30 / 100;

            if (mDataset.get(position).getImage_uri() != null){
                Glide.with(context)
                        .load(UriUtils.getPath(context, mDataset.get(position).getImage_uri()))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .placeholder(MediaUtils.image_placeholder_black)
                        .error(MediaUtils.image_error_black)
                        .into(visitImageVH.image);
            } else {
                Glide.with(context)
                        .load(APIConstants.WEB_URL + mDataset.get(position).getPath())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .placeholder(MediaUtils.image_placeholder_black)
                        .error(MediaUtils.image_error_black)
                        .into(visitImageVH.image);
            }

        } else {
            ImagePlaceholderVH imagePlaceholderVH = (ImagePlaceholderVH) holder;
            imagePlaceholderVH.placeholderWrapper.getLayoutParams().width = FastAppController.screenWidth * 30 / 100;
            imagePlaceholderVH.placeholderWrapper.getLayoutParams().height = FastAppController.screenWidth * 30 / 100;
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

                    changeImage.setVisibility(View.GONE);
                    changeImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            savedPos = holder.getAdapterPosition();
                            /*if (context instanceof  LabResultAddActivity){
                                ((LabResultAddActivity)context).addNewImage();
                            } else if (context instanceof LabResultEditActivity){
                                ((LabResultEditActivity)context).addNewImage();
                            }*/
                            dialog.dismiss();
                        }
                    });

                    deleteImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (LabResultImgModel item :
                                    editMDataset) {
                                if (mDataset.get(holder.getAdapterPosition()).getId() != null &&
                                        mDataset.get(holder.getAdapterPosition()).getId().equals(item.getId())){
                                    item.setIs_deleted(true);
                                    break;
                                }
                            }
                            mDataset.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            dialog.dismiss();
                        }
                    });

                } else {
                    savedPos = holder.getAdapterPosition();
                    if (context instanceof  LabResultAddActivity){
                        ((LabResultAddActivity)context).addNewImage();
                    } else if (context instanceof LabResultEditActivity){
                        ((LabResultEditActivity) context).addNewImage();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof VisitImageVH){
            Glide.clear(((VisitImageVH)holder).image);
        }
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
