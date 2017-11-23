package com.med.fast;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Kevin Murvie on 4/6/2017. FM
 */

public abstract class FastBaseManagementFragment extends FastBaseFragment {
    @BindView(R.id.management_mainfragment_search_edittxt)
    public CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    public ImageView searchBtn;
    @BindView(R.id.management_mainfragment_swipe_refresh)
    public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.management_mainfragment_recycler)
    public RecyclerView recyclerView;
    @BindView(R.id.management_mainfragment_progress)
    public ProgressBar progressBar;
    @BindView(R.id.management_mainfragment_nocontent_tv)
    public CustomFontTextView noContentTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    public void setTitle(String title){
        ((MainActivity) getActivity()).changeTitle(title);
    }
//    protected abstract void onItemAdded();
}
