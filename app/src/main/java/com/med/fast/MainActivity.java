package com.med.fast;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.login.LoginActivity;
import com.med.fast.management.allergy.AllergyManagementFragment;
import com.med.fast.management.idcard.IDCardFragment;
import com.med.fast.management.misc.MiscManagementFragment;
import com.med.fast.summary.SummaryFragment;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public class MainActivity extends FastBaseActivity implements MenuOnClick {

    private FragmentManager fragmentManager;
    private DrawerFragment drawerFragment;

    @BindView(R.id.fmcontainer_appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_main_top_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_main_top_title)
    CustomFontTextView toolbarTitle;
    @BindView(R.id.fmcontainer_toolbar_shadow)
    View toolbarShadow;
    @BindView(R.id.dashboard_drawer_layout)
    DrawerLayout dashboardDrawer;
    @BindView(R.id.fmcontainer_frame)
    FrameLayout dashboardFrame;
    @BindView(R.id.fmcontainer_fab)
    FloatingActionButton dashboardFab;

    private ShowcaseView mainSV;
    private int step = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fmcontainer_main_layout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        FastAppController.screenWidth = displayMetrics.widthPixels;

        setSupportActionBar(toolbar);

        SharedPreferenceUtilities.setUserInformation(this, SharedPreferenceUtilities.INIT_DATA_STEP, "5");
        // For version 21++, shadow is disabled
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbarShadow.setVisibility(View.GONE);
        }

        fragmentManager = getSupportFragmentManager();
        drawerFragment = (DrawerFragment) fragmentManager.findFragmentById(R.id.dashboard_drawerfrag);
        dashboardFab.setAlpha(0.25f);
        dashboardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment() instanceof FastBaseFragment) {
                    ((FastBaseFragment) currentFragment()).addItem();
                }
            }
        });

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                checkCurrentFragment();
            }
        });

        String mainGuideSP = SharedPreferenceUtilities.getFromGuideSP(this, SharedPreferenceUtilities.GUIDE_MAIN_SCREEN);

        if (mainGuideSP == null || mainGuideSP.equals("0")) {
            Target drawerTarget = new ViewTarget(R.id.dashboard_coordinator_layout, this);
//            TextPaint content = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//            content.setTextSize(getResources().getDimension(R.dimen.font_small));
//            content.setColor(Color.WHITE);
//            content.setTypeface(font);

//            TextPaint title = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//            title.setTextSize(getResources().getDimension(R.dimen.font_size_extra_large));
//            title.setColor(Color.WHITE);
//            title.setTypeface(font);

            mainSV = new ShowcaseView.Builder(MainActivity.this)
                    .withMaterialShowcase()
//                    .setTarget(drawerTarget)
                    .setContentTitle(getString(R.string.showcase_main_title))
//                    .setContentTextPaint(content)
                    .setContentText(R.string.showcase_main_text)
//                    .setContentTitlePaint(title)
                    .setStyle(R.style.FMShowcaseTheme)
                    .blockAllTouches()
                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                        @Override
                        public void onShowcaseViewHide(ShowcaseView showcaseView) {
                            SharedPreferenceUtilities.setUserInformation(MainActivity.this, SharedPreferenceUtilities.GUIDE_SP, SharedPreferenceUtilities.GUIDE_MAIN_SCREEN, "1");
                        }

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                        }
                    })
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (step) {
                                case 0:
                                    drawerToggle(true);
                                    mainSV.setContentTitle(getString(R.string.showcase_drawer_title));
                                    mainSV.setContentText(getString(R.string.showcase_drawer_text));
                                    mainSV.setShouldCentreText(true);

                                    final List<Target> showcaseTargets = DrawerFragList.getFragTargetList(MainActivity.this);
                                    for (int i = 0; i < showcaseTargets.size() ;i++) {
                                        final int finalI = i;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mainSV.setShowcase(showcaseTargets.get(finalI), true);
                                                if (finalI == showcaseTargets.size() - 1){
                                                    mainSV.showButton();
                                                } else {
                                                    mainSV.hideButton();
                                                }
                                            }
                                        }, 300 + (300 * i));
                                    }

                                    step = 1;
                                    break;
                                case 1:
                                    drawerToggle(false);
                                    AllergyManagementFragment allergyManagementFragment = new AllergyManagementFragment();
                                    replaceFragment(allergyManagementFragment, Tag.ALLERGY_FRAG, true);
                                    Target target = new ViewTarget(R.id.toolbar_main_top_title, MainActivity.this);
                                    mainSV.setShowcase(target, true);
                                    mainSV.setContentTitle(getString(R.string.showcase_allergy_title));
                                    mainSV.setContentText(getString(R.string.showcase_allergy_text));
                                    step = 2;
                                    break;
                                case 2:
                                    ViewTarget fab = new ViewTarget(R.id.fmcontainer_fab, MainActivity.this);
                                    mainSV.setShowcase(fab, true);
                                    mainSV.setContentTitle(getString(R.string.showcase_allergy_title));
                                    mainSV.setContentText(getString(R.string.showcase_allergy_create_text));
                                    step = 3;
                                    break;
                                case 3:
                                    mainSV.hide();
                                    drawerToggle(false);
//                                    reinitializeMain();
                                    step = 4;
                                    break;
                            }
                        }
                    })
                    .build();

        }

        SummaryFragment summaryFragment = new SummaryFragment();
        replaceFragment(summaryFragment, Tag.SUMMARY_FRAG, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra(IntentNames.SETTING_FINISHED) != null) {
            if (currentFragment() instanceof SummaryFragment) {
                ((SummaryFragment) currentFragment()).refreshView(true);
            }
        }
    }

    @Override
    public void scrollToTop() {
        if (currentFragment() instanceof FastBaseFragment) {
            ((FastBaseFragment) currentFragment()).scrollToTop();
            appBarLayout.setExpanded(true, true);
        }
    }

    public void expandAppBarLayout(boolean expand, boolean animation) {
        appBarLayout.setExpanded(expand, animation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_toolbar_top, menu);

        MenuItem drawerMenu = menu.findItem(R.id.menu_toolbar_top_drawer);

        drawerMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                drawerToggle();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        backstackPopper();
    }

    public void replaceFragment(final Fragment fragment, final String tag, boolean addToBackstack) {
        Bundle args = new Bundle();
        /*ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);*/
        if (fragment.getArguments() == null) {
            fragment.setArguments(args);
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fmcontainer_frame, fragment, tag)
                .setAllowOptimization(false);
        if (addToBackstack) {
            ft.addToBackStack(tag);
        }
        ft.commit();
        fragmentManager.executePendingTransactions();

        if (!addToBackstack){
            checkCurrentFragment();
        }
    }

    private void checkCurrentFragment(){
        if (currentFragment() instanceof SummaryFragment ||
                currentFragment() instanceof MiscManagementFragment ||
                currentFragment() instanceof IDCardFragment) {
            dashboardFab.hide();
        } else {
            if (!dashboardFab.isShown()) {
                dashboardFab.show();
            }
        }
        drawerFragment.activateCurrentFragment();
    }

    public void reinitializeMain() {
        try {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception ignored) {

        }
        SummaryFragment summaryFragment = new SummaryFragment();
        replaceFragment(summaryFragment, Tag.SUMMARY_FRAG, true);
    }

    public Fragment currentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fmcontainer_frame);
    }

    public void drawerToggle() {
//        SharedPreferenceUtilities.clearSharedPreference(this, SharedPreferenceUtilities.GUIDE_SP);
        if (dashboardDrawer.isDrawerOpen(GravityCompat.END)) {
            dashboardDrawer.closeDrawer(GravityCompat.END);
        } else {
            dashboardDrawer.openDrawer(GravityCompat.END);
        }
    }

    public void drawerToggle(boolean open) {
        if (open) {
            dashboardDrawer.openDrawer(GravityCompat.END);
        } else {
            if (dashboardDrawer.isDrawerOpen(GravityCompat.END)) {
                dashboardDrawer.closeDrawer(GravityCompat.END);
            }
        }
    }

    public void changeTitle(String title) {
        toolbarTitle.setText(title);
    }

    public void backstackPopper() {
        if (fragmentManager.getBackStackEntryCount() - 2 >= 0) {
            fragmentManager.popBackStackImmediate();
        } else {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }
    }

    public void displayLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.logout_question))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferenceUtilities.clearSharedPreference(MainActivity.this, SharedPreferenceUtilities.SESSION_SP);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in_slow, R.anim.fade_out_slow);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public void onMenuClick(int position, View clickedView, MenuModel menuModel) {

    }
}