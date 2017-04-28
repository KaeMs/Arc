package com.med.fast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.visit.VisitFragment;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class MainActivity extends FastBaseActivity {

    private FragmentManager fragmentManager;
    private DrawerFragment drawerFragment;

    @BindView(R.id.toolbartitledivider_title)
    CustomFontTextView toolbarTitle;
    @BindView(R.id.dashboard_drawer_layout)
    DrawerLayout dashboardDrawer;
    @BindView(R.id.fmcontainer_frame)
    FrameLayout dashboardFrame;
    @BindView(R.id.fmcontainer_fab)
    FloatingActionButton dashboardFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fmcontainer_main_layout);

        fragmentManager = getSupportFragmentManager();
        drawerFragment = (DrawerFragment) fragmentManager.findFragmentById(R.id.dashboard_drawerfrag);

        dashboardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment() instanceof FastBaseFragment){
                    ((FastBaseFragment) currentFragment()).addItem();
                }
            }
        });
    }

    public void replaceFragment(final Fragment fragment, final String tag, boolean addToBackstack){
        Bundle args = new Bundle();
//        String[] splitTag = tag.split(":");
        /*ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);*/
        if (fragment.getArguments() == null) {
            fragment.setArguments(args);
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fmcontainer_frame, fragment, tag)
                .addToBackStack(tag)
                .setAllowOptimization(false);
        if (addToBackstack){
            ft.addToBackStack(tag);
        }
        ft.commit();
        fragmentManager.executePendingTransactions();
    }

    public Fragment currentFragment(){
        return getSupportFragmentManager().findFragmentById(R.id.fmcontainer_frame);
    }

    public void drawerToggle(){
        if (dashboardDrawer.isDrawerOpen(GravityCompat.END)) {
            dashboardDrawer.closeDrawer(GravityCompat.END);
        } else {
            dashboardDrawer.openDrawer(GravityCompat.END);
        }
    }

    public void changeTitle(String title){
        toolbarTitle.setText(title);
    }
}
