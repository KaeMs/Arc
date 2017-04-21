package com.med.fast.dashboard.visit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.med.fast.FastBaseFragment;
import com.med.fast.R;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.dashboard.DashboardActivity;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class VisitFragment extends FastBaseFragment {

    @BindView(R.id.dashboard_visit_search_edittxt)
    CustomFontEditText searchET;
    @BindView(R.id.dashboard_visit_search_btn)
    ImageView searchBtn;
    @BindView(R.id.dashboard_visit_recycler)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard_visit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DashboardActivity)getActivity()).changeTitle("VISIT MANAGEMENT");

    }

    public void addNewVisit(){

    }
}
