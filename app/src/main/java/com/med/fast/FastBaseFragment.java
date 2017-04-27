package com.med.fast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Kevin Murvie on 4/6/2017.
 */

public abstract class FastBaseFragment extends Fragment {

    private Unbinder unbinder;

    public void scrollToTop() {
    }

    public void refreshView(boolean showProgress) {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /*protected void createImagePickerDialog(final Context context, final File output, String title){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            if (ActivityCompat.shouldShowRequestPermissionRationale(((FastBaseActivity)context),
                    Manifest.permission.CAMERA)) {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example if the user has previously denied the permission.
                ActivityCompat.requestPermissions(((FastBaseActivity)context),
                        new String[]{Manifest.permission.CAMERA},
                        RequestCodeList.cameraRequestCode);
            } else {
                // Camera permission has not been granted yet. Request it directly.
                ActivityCompat.requestPermissions(((FastBaseActivity)context), new String[]{Manifest.permission.CAMERA},
                        RequestCodeList.cameraRequestPermission);
            }
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(((FastBaseActivity)context),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example if the user has previously denied the permission.
                ActivityCompat.requestPermissions(((FastBaseActivity)context),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        RequestCodeList.writeStoragePermission);
            } else {
                // Camera permission has not been granted yet. Request it directly.
                ActivityCompat.requestPermissions(((FastBaseActivity)context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        RequestCodeList.writeStoragePermission);
            }

        } else {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setItems(Constants.imageCapture, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
//                                case 0:
//                                    Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//
//                                    // set the image file name
//                                    intentVideo.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
//
//                                    // set the video image quality to high
//                                    intentVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
//                                    intentVideo.putExtra(MediaStore.EXTRA_DURATION_LIMIT, Constants.VIDEO_DURATION_LIMIT);
//
//                                    // start the Video Capture Intent
//                                    startActivityForResult(intentVideo, RequestCodeList.videoRequestCode);
//                                    break;
                                case 0:
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                                        ContentValues values = new ContentValues(1);
                                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                                        Uri fileUri;
                                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                                            fileUri = FileProvider.getUriForFile(context,
                                                    "id.mallinternet.www.fileprovider",
                                                    output);
                                        } else {
                                            fileUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                        }

                                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    } else {
                                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
                                    }

                                    startActivityForResult(cameraIntent, RequestCodeList.cameraRequestCode);
                                    dialog.dismiss();
                                    break;
                                case 1:
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ImageCropperBase imageCropperBase = null;
                                        imageCropperBase.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                                                getString(R.string.permission_read_storage_rationale),
                                                imageCropperBase.getWritePermsion());
                                    } else {
                                        if (Build.VERSION.SDK_INT < 19) {
                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                            intent.setType("image*//* video*//*");
                                            intent.setType("image*//*");
                                            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.label_select_picture_video)),RequestCodeList.gallerySelectRequestcode);
                                        } else {
                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                            intent.setType("image*//* video*//*");
                                            intent.setType("image*//*");
                                            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image*//*"*//*, "video*//**//*"*//*});
                                            startActivityForResult(intent, RequestCodeList.gallerySelectRequestcode);
                                            dialog.dismiss();
                                        }
                                    }
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    })
                        *//*.setPositiveButton(R.string.fromCamera, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })*//*
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    public void startCropActivity(@NonNull Uri uri) {
        Uri compressedUri = Utilities.compressImage(getActivity(), uri);
        if (compressedUri != null){
            UCrop uCrop = UCrop.of(compressedUri, compressedUri);
            uCrop.withAspectRatio(1, 1);
            UCrop.Options uCropOption = new UCrop.Options();
            uCropOption.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.miBlack));
            uCropOption.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.miBlack));
            uCropOption.setToolbarTitle(this.getString(R.string.cropTitle));
            uCrop.withOptions(uCropOption);
            //uCrop = basisConfig(uCrop);
            //uCrop = advancedConfig(uCrop);
            uCrop.start(getActivity());
        } else {
            Toast.makeText(getActivity(), getString(R.string.imageRetrievalFailure), Toast.LENGTH_SHORT).show();
        }
    }*/
}
