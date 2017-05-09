package com.med.fast.summary;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.ResponseAPI;

import butterknife.BindView;

/**
 * Created by kevindreyar on 23-Apr-17. FM
 */

public class SummaryFragment extends FastBaseFragment implements SummaryShowIntf {
    @BindView(R.id.summary_greeting)
    TextView summaryGreeting;
    @BindView(R.id.summary_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.summary_fragment_recycler)
    RecyclerView summaryRecycler;
    private LinearLayoutManager linearLayoutManager;
    @BindView(R.id.summary_fragment_progress)
    ProgressBar summaryProgress;
    private SummaryAdapter summaryAdapter;

    private String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.summary_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).changeTitle("SUMMARY");
        userId = SharedPreferenceUtilities.getUserId(getActivity());

        summaryAdapter = new SummaryAdapter(getActivity());

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        summaryRecycler.setLayoutManager(linearLayoutManager);
        summaryRecycler.setAdapter(summaryAdapter);
        refreshView(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView(false);
            }
        });
    }

    @Override
    public void refreshView(boolean setRefreshing) {
        SummaryShowAPI summaryShowAPI = new SummaryShowAPI();
        summaryShowAPI.data.query.user_id = userId;

        SummaryShowAPIFunc summaryShowAPIFunc = new SummaryShowAPIFunc(getActivity(), SummaryFragment.this);
        summaryShowAPIFunc.execute(summaryShowAPI);
        swipeRefreshLayout.setRefreshing(!setRefreshing);
        if (setRefreshing) summaryProgress.setVisibility(View.VISIBLE);
        else summaryProgress.setVisibility(View.GONE);
    }

    @Override
    public void onFinishSummaryShow(ResponseAPI responseAPI) {
        if (this.isVisible()) {
            summaryProgress.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if (responseAPI.status_code == 200) {
                Gson gson = new Gson();
                SummaryShowAPI output = gson.fromJson(responseAPI.status_response, SummaryShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    SummaryWrapperModel summaryWrapperModel = new SummaryWrapperModel();
                    summaryWrapperModel.name = output.data.results.name;
                    summaryWrapperModel.date_of_birth = output.data.results.date_of_birth;
                    summaryWrapperModel.gender = output.data.results.gender;
                    summaryWrapperModel.profil_image_path = output.data.results.profil_image_path;
                    summaryWrapperModel.allergies = output.data.results.allergies;
                    summaryWrapperModel.disease = output.data.results.disease;
                    summaryWrapperModel.family_anamnesy = output.data.results.family_anamnesy;
                    summaryWrapperModel.medicine = output.data.results.medicine;
                    summaryWrapperModel.visit = output.data.results.visit;

                    summaryAdapter.setModel(summaryWrapperModel);

                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linearLayoutManager.scrollToPosition(0);
                        }
                    }, 100);*/
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (responseAPI.status_code == 504) {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if (responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((MainActivity) getActivity()).forceLogout();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
