package com.med.fast;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Kevin Murvie on 3/27/2017. KM
 */

public abstract class FastBaseActivity extends AppCompatActivity {

    private Unbinder unbinder;

    public void replaceFragment(Fragment fragment, String tag, boolean addToBackstack) {
    }

    protected void createImagePickerDialog(final Context context, final File output, String title){

        int hasCameraPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int hasReadPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasWritePermission  = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> permissions = new ArrayList<>();

        if( hasCameraPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.CAMERA );
        }

        if( hasReadPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.READ_EXTERNAL_STORAGE );
        }

        if( hasWritePermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.WRITE_EXTERNAL_STORAGE );
        }


        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, permissions.toArray(new String[permissions.size()]), RequestCodeList.PHOTO_OPERATIONS);
        } else {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setItems(getResources().getStringArray(R.array.select_image_source_arr), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                                        ContentValues values = new ContentValues(1);
                                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                                        Uri fileUri;
                                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                                            fileUri = FileProvider.getUriForFile(context,
                                                    "com.med.fast.fileprovider",
                                                    output);
                                        } else {
                                            fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                        }

                                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    } else {
                                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
                                    }

                                    startActivityForResult(cameraIntent, RequestCodeList.CAMERA);
                                    dialog.dismiss();
                                    break;
                                case 1:
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(FastBaseActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionCodeList.READ_EXTERNAL_STORAGE);
                                    } else {
                                        if (Build.VERSION.SDK_INT < 19) {
                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                            intent.setType("image/* video/*");
                                            intent.setType("image/*");
                                            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)),RequestCodeList.GALLERY);
                                        } else {
                                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                            intent.setType("image/* video/*");
                                            intent.setType("image/*");
                                            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"/*, "video*//*"*/});
                                            startActivityForResult(intent, RequestCodeList.GALLERY);
                                            dialog.dismiss();
                                        }
                                    }
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    })
                        /*.setPositiveButton(R.string.fromCamera, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })*/
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
    
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public CreatedImageModel createImageFile() throws IOException {
        CreatedImageModel createdImageModel = new CreatedImageModel();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.dateFormatPicture, Locale.getDefault());
        String imageFileName = simpleDateFormat.format(date);

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + MediaUtils.FMR_DIR);
        File image;

        if (externalMemoryAvailable()) {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } else {
            image = new File(storageDir, imageFileName + ".jpg");
        }

        // Save a file: path for use with ACTION_VIEW intents
        createdImageModel.currentMediaPath = "file:" + image.getAbsolutePath();
        createdImageModel.mDestinationUri = Uri.parse(createdImageModel.currentMediaPath);
        createdImageModel.image = image;
        return createdImageModel;
    }

    public boolean externalMemoryAvailable() {
        if (Environment.isExternalStorageRemovable()) {
            //device support sd card. We need to check sd card availability.
            String state = Environment.getExternalStorageState();
            return state.equals(Environment.MEDIA_MOUNTED) || state.equals(
                    Environment.MEDIA_MOUNTED_READ_ONLY);
        } else {
            //device not support sd card.
            return false;
        }
    }

    public void forceLogout() {

    }
}
