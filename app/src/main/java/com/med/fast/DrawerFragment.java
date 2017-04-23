package com.med.fast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.med.fast.visit.VisitFragment;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class DrawerFragment extends FastBaseFragment {

    @BindView(R.id.drawer_visit_wrapper)
    LinearLayout visitWrapper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dashboard_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        visitWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).drawerToggle();
                VisitFragment visitFragment = new VisitFragment();
                ((FastBaseActivity)getActivity()).replaceFragment(visitFragment, Tag.VISIT_FRAG, false);
            }
        });
    }
}
