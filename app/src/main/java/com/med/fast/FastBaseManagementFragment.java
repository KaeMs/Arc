package com.med.fast;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/6/2017. FM
 */

public abstract class FastBaseManagementFragment extends FastBaseFragment {
    @BindView(R.id.management_mainfragment_search_edittxt)
    public CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    public ImageView searchBtn;
    /*@BindView(R.id.management_mainfragment_add_btn)
    public ImageButton addbtn;*/
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (getActivity() != null) {
                        ((MainActivity)getActivity()).dashboardFab.hide();
                    }
                }
            }
        });*/
    }

    public void setTitle(String title) {
        ((MainActivity) getActivity()).changeTitle(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getArguments() != null){
            getArguments().putParcelable(Constants.MANAGER_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
        }
        recyclerView.clearOnScrollListeners();
    }
}
