package com.med.fast.management.misc;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontTextView;
import com.med.fast.management.misc.api.MiscManagementShowAPI;
import com.med.fast.management.misc.api.MiscManagementShowAPIFunc;
import com.med.fast.management.misc.miscinterface.MiscFragmentIntf;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public class MiscManagementFragment extends FastBaseFragment implements SwipeRefreshLayout.OnRefreshListener, MiscFragmentIntf {
    @BindView(R.id.management_miscfragment_swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.management_miscfragment_voluptuary_habit)
    CustomFontTextView voluptuaryTV;
    @BindView(R.id.management_miscfragment_voluptuary_habit_progress)
    ProgressBar voluptuaryProgress;
    @BindView(R.id.management_miscfragment_voluptuary_edit_btn)
    ImageView editBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.managment_misc_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        voluptuaryProgress.setVisibility(View.VISIBLE);
        refreshView();
    }

    void refreshView(){
        MiscManagementShowAPI miscManagementShowAPI = new MiscManagementShowAPI();
        miscManagementShowAPI.data.query.user_id = SharedPreferenceUtilities.getUserId(getActivity());

        MiscManagementShowAPIFunc miscManagementShowAPIFunc = new MiscManagementShowAPIFunc(getActivity());
        miscManagementShowAPIFunc.setDelegate(this);
        miscManagementShowAPIFunc.execute(miscManagementShowAPI);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refreshView();
    }

    @Override
    public void addItem() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.management_misc_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        CustomFontEditText voluptuaryET = (CustomFontEditText) dialog.findViewById(R.id.misc_popup_voluptuary_habit);

        CustomFontButton saveBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_gravitystart_left_btn);
        CustomFontButton backBtn = (CustomFontButton) dialog.findViewById(R.id.management_operations_gravitystart_right_btn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onFinishMiscShow(ResponseAPI responseAPI) {
        if (this.isVisible()) {
            voluptuaryProgress.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if (responseAPI.status_code == 200) {
                Gson gson = new Gson();
                MiscManagementShowAPI output = gson.fromJson(responseAPI.status_response, MiscManagementShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    voluptuaryTV.setText(output.data.results.voluptuary_habit);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if(responseAPI.status_code == 504) {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if(responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((MainActivity)getActivity()).forceLogout();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFinishMiscCreateSubmit(ResponseAPI responseAPI) {

    }

}
