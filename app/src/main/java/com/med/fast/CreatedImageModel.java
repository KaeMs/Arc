package com.med.fast;

import android.net.Uri;

import java.io.File;

/**
 * Created by Kevin Murvie on 5/5/2017. FM
 */

public class CreatedImageModel {
    public String currentMediaPath;
    public Uri mDestinationUri;
    public File image;

    public CreatedImageModel(){}
    public CreatedImageModel(String currentMediaPath, Uri mDestinationUri, File image){
        this.currentMediaPath = currentMediaPath;
        this.mDestinationUri = mDestinationUri;
        this.image = image;
    }
}