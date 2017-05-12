package com.med.fast;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;

import id.zelory.compressor.Compressor;

/**
 * Created by Kevin Murvie on 4/26/2017. FM
 */

public class MediaUtils {

    public static int image_placeholder_black = R.drawable.ic_image_black_48dp;
    public static int image_placeholder_white = R.drawable.ic_image_white_48dp;
    public static int image_error_black = R.drawable.ic_broken_image_black_48dp;
    public static int image_error_white = R.drawable.ic_broken_image_white_48dp;
    public static final String FMR_DIR_SLASH = "/FastMedRec/";
    public static final String FMR_DIR = "/FastMedRec";

    // Compress image
    public static Uri compressImage(Context context, Uri inputUri) {
        try {
            String realPath = UriUtils.getPath(context, inputUri);
            if (realPath == null){
                realPath = inputUri.getPath();
            }

            File realFile = new File(realPath);

            File compressedImg = new Compressor.Builder(context)
                    .setMaxHeight(1920)
                    .setMaxWidth(1920)
                    .setQuality(70)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .build()
                    .compressToFile(realFile);

            return Uri.fromFile(compressedImg);
        } catch (Exception e){
            return null;
        }
    }
}
