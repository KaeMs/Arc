package com.med.fast;

import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Kevin Murvie on 3/27/2017. KM
 */

public abstract class FastBaseActivity extends AppCompatActivity {

    private Unbinder unbinder;

    public void replaceFragment(Fragment fragment, String tag, boolean addToBackstack) {
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

    public void forceLogout() {}
}
