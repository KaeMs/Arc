package com.med.fast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;

import butterknife.BindView;

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
    protected LinearLayoutManager linearLayoutManager;
    @BindView(R.id.management_mainfragment_progress)
    public ProgressBar progressBar;
    @BindView(R.id.management_mainfragment_nocontent_tv)
    public CustomFontTextView noContentTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void setTitle(String title) {
        ((MainActivity) getActivity()).changeTitle(title);
    }
}
