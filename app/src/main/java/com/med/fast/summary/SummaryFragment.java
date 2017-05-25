package com.med.fast.summary;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.FastAppController;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.SharedPreferenceUtilities;
import com.med.fast.api.ResponseAPI;

import butterknife.BindView;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;

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
    @BindView(R.id.summary_fragment_progress)
    ProgressBar summaryProgress;
    private LinearLayoutManager linearLayoutManager;
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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        summaryAdapter.setWidth(displayMetrics.widthPixels);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        summaryRecycler.setLayoutManager(linearLayoutManager);
        summaryRecycler.setAdapter(summaryAdapter);
        refreshView(false);

        /*Parcelable savedRecyclerLayoutState = getArguments().getParcelable(Constants.MANAGER_STATE);
        if(savedRecyclerLayoutState == null){
            refreshView(false);
        } else {
            linearLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
            refreshView(false);
        }*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView(true);
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        getArguments().putParcelable(Constants.MANAGER_STATE, summaryRecycler.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void scrollToTop() {
        this.summaryRecycler.smoothScrollBy(0, -1000);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                summaryRecycler.scrollToPosition(0);
            }
        }, Constants.scrollTopTime);
    }

    @Override
    public void refreshView(boolean setRefreshing) {
        SummaryShowAPI summaryShowAPI = new SummaryShowAPI();
        summaryShowAPI.data.query.user_id = userId;

        SummaryShowAPIFunc summaryShowAPIFunc = new SummaryShowAPIFunc(getActivity(), SummaryFragment.this);
        summaryShowAPIFunc.execute(summaryShowAPI);
        swipeRefreshLayout.setRefreshing(setRefreshing);
        if (setRefreshing) summaryProgress.setVisibility(View.GONE);
        else summaryProgress.setVisibility(View.VISIBLE);
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
                    Parcelable savedRecyclerLayoutState = getArguments().getParcelable(Constants.MANAGER_STATE);
                    if (savedRecyclerLayoutState != null) {
                        linearLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
                    }
                    final SummaryWrapperModel summaryWrapperModel = new SummaryWrapperModel();
                    summaryWrapperModel.name = output.data.results.name;
                    summaryWrapperModel.date_of_birth = output.data.results.date_of_birth;
                    summaryWrapperModel.gender = output.data.results.gender;
                    summaryWrapperModel.profil_image_path = output.data.results.profil_image_path;
                    summaryWrapperModel.allergies.addAll(output.data.results.allergies);
                    summaryWrapperModel.disease.addAll(output.data.results.disease);
                    summaryWrapperModel.family_anamnesy.addAll(output.data.results.family_anamnesy);
                    summaryWrapperModel.medicine.addAll(output.data.results.medicine);
                    summaryWrapperModel.visit.addAll(output.data.results.visit);
                    summaryWrapperModel.voluptuary_habits = output.data.results.voluptuary_habits;

                    /*FastAppController.realm.beginTransaction();
                    SummaryWrapperModel realmSummaryWrapper = FastAppController.realm.where(SummaryWrapperModel.class).findFirst();
                    if (realmSummaryWrapper != null) {
                        realmSummaryWrapper.deleteFromRealm();
                        FastAppController.realm.copyToRealm(summaryWrapperModel);
                    } else {
                        FastAppController.realm.copyToRealm(summaryWrapperModel);
                    }
                    FastAppController.realm.commitTransaction();*/
                    summaryAdapter.setModel(summaryWrapperModel);

                    /*FastAppController.realm.beginTransaction();
                    SummaryWrapperModel savedSummaryWrapper = FastAppController.realm.where(SummaryWrapperModel.class).findFirst();
                    savedSummaryWrapper.removeAllChangeListeners();
                    savedSummaryWrapper.addChangeListener(new RealmChangeListener<RealmModel>() {
                        @Override
                        public void onChange(RealmModel realmModel) {
                            summaryAdapter.notifyDataSetChanged();
                        }
                    });
                    FastAppController.realm.commitTransaction();*/

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
