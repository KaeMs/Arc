package com.med.fast.management.allergy;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.gson.Gson;
import com.med.fast.Constants;
import com.med.fast.FastBaseFragment;
import com.med.fast.MainActivity;
import com.med.fast.R;
import com.med.fast.api.ResponseAPI;
import com.med.fast.customevents.LoadMoreEvent;
import com.med.fast.customviews.CustomFontButton;
import com.med.fast.customviews.CustomFontEditText;
import com.med.fast.customviews.CustomFontRadioButton;
import com.med.fast.management.allergy.allergyinterface.AllergyManagementFragmentIntf;
import com.med.fast.management.allergy.api.AllergyManagementCreateSubmitAPI;
import com.med.fast.management.allergy.api.AllergyManagementCreateSubmitAPIFunc;
import com.med.fast.management.allergy.api.AllergyManagementListShowAPI;
import com.med.fast.management.allergy.api.AllergyManagementListShowAPIFunc;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

/**
 * Created by Kevin Murvie on 4/23/2017. FM
 */

public class AllergyManagementFragment extends FastBaseFragment implements AllergyManagementFragmentIntf {
    @BindView(R.id.management_mainfragment_search_edittxt)
    CustomFontEditText searchET;
    @BindView(R.id.management_mainfragment_search_btn)
    ImageView searchBtn;
    @BindView(R.id.management_mainfragment_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.management_mainfragment_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.management_mainfragment_progress)
    ProgressBar progressBar;
    private AllergyManagementAdapter allergyManagementAdapter;
    private boolean isLoading = false;
    private int counter = 0;
    private int lastItemCounter = 0;
    private String currentKeyword = "default";
    private String currentSort = "default";
    private String userId = "18";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.management_mainfragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).changeTitle("ALLERGY MANAGEMENT");
        setHasOptionsMenu(true);

        allergyManagementAdapter = new AllergyManagementAdapter(getActivity());

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        refreshView(false);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                swipeRefreshLayout.setEnabled(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);

                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                int visibleThreshold = 1;

                // When threshold is reached, API call is made to get new items
                // for infinite scroll
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (lastItemCounter > 10) {
                        AllergyManagementListShowAPI allergyManagementListShowAPI = new AllergyManagementListShowAPI();
                        allergyManagementListShowAPI.data.query.user_id = userId;
                        allergyManagementListShowAPI.data.query.keyword = currentKeyword;
                        allergyManagementListShowAPI.data.query.sort = currentSort;
                        allergyManagementListShowAPI.data.query.counter = String.valueOf(counter);
                        allergyManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

                        AllergyManagementListShowAPIFunc allergyManagementListShowAPIFunc = new AllergyManagementListShowAPIFunc(getActivity());
                        allergyManagementListShowAPIFunc.setDelegate(AllergyManagementFragment.this);
                        allergyManagementListShowAPIFunc.execute(allergyManagementListShowAPI);
                        isLoading = true;
                    }
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentKeyword = searchET.getText().toString();
                refreshView(false);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void refreshView(boolean showProgress){
        AllergyManagementListShowAPI allergyManagementListShowAPI = new AllergyManagementListShowAPI();
        allergyManagementListShowAPI.data.query.user_id = userId;
        allergyManagementListShowAPI.data.query.keyword = currentKeyword;
        allergyManagementListShowAPI.data.query.sort = currentSort;
        allergyManagementListShowAPI.data.query.counter = "0";
        allergyManagementListShowAPI.data.query.flag = Constants.FLAG_REFRESH;

        AllergyManagementListShowAPIFunc allergyManagementListShowAPIFunc = new AllergyManagementListShowAPIFunc(getActivity());
        allergyManagementListShowAPIFunc.setDelegate(AllergyManagementFragment.this);
        allergyManagementListShowAPIFunc.execute(allergyManagementListShowAPI);
        if (showProgress){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.VISIBLE);
        }
        isLoading = true;
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

    @Subscribe
    public void handleLoadMoreEvent (LoadMoreEvent loadMoreEvent){
        if (this.isVisible()){
            AllergyManagementListShowAPI allergyManagementListShowAPI = new AllergyManagementListShowAPI();
            allergyManagementListShowAPI.data.query.user_id = userId;
            allergyManagementListShowAPI.data.query.keyword = currentKeyword;
            allergyManagementListShowAPI.data.query.sort = currentSort;
            allergyManagementListShowAPI.data.query.counter = String.valueOf(counter);
            allergyManagementListShowAPI.data.query.flag = Constants.FLAG_LOAD;

            AllergyManagementListShowAPIFunc allergyManagementListShowAPIFunc = new AllergyManagementListShowAPIFunc(getActivity());
            allergyManagementListShowAPIFunc.setDelegate(AllergyManagementFragment.this);
            allergyManagementListShowAPIFunc.execute(allergyManagementListShowAPI);
            isLoading = true;
        }
    }

    @Override
    public void addItem() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.allergy_popup);
        dialog.setCanceledOnTouchOutside(false);

        final CustomFontEditText causative = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_causative_et);
        final CustomFontRadioButton drugTypeYes = (CustomFontRadioButton) dialog.findViewById(R.id.allergy_popup_drugtype_rb_yes);
        final CustomFontEditText reaction = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_reaction_et);
        final CustomFontEditText firstExp = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_firsttime_et);
        CustomFontButton cancelBtn = (CustomFontButton) dialog.findViewById(R.id.allergy_popup_cancel_btn);
        CustomFontButton addBtn = (CustomFontButton) dialog.findViewById(R.id.allergy_popup_add_btn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
        mAwesomeValidation.setContext(getActivity());
        mAwesomeValidation.addValidation(causative, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));
        mAwesomeValidation.addValidation(reaction, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAwesomeValidation.validate()) {
                    String causativeString = causative.getText().toString();
                    String drugTypeString = drugTypeYes.isChecked() ? "yes" : "no";
                    String reactionString = reaction.getText().toString();
                    String firstExpString = firstExp.getText().toString();

                    AllergyManagementModel allergy = new AllergyManagementModel();
                    allergy.setAgent(causativeString);
                    allergy.setDrug(drugTypeString);
                    allergy.setReaction(reactionString);
                    allergy.setFirst_experience(firstExpString);
                    allergy.setProgress_status("1");
                    allergyManagementAdapter.addSingle(allergy);

                    AllergyManagementCreateSubmitAPI allergyManagementCreateSubmitAPI = new AllergyManagementCreateSubmitAPI();
                    allergyManagementCreateSubmitAPI.data.query.user_id = userId;
                    allergyManagementCreateSubmitAPI.data.query.allergy_agent = causativeString;
                    allergyManagementCreateSubmitAPI.data.query.allergy_is_drug = drugTypeString;
                    allergyManagementCreateSubmitAPI.data.query.allergy_reaction = reactionString;
                    allergyManagementCreateSubmitAPI.data.query.allergy_first_experience = firstExpString;

                    AllergyManagementCreateSubmitAPIFunc allergyManagementCreateSubmitAPIFunc = new AllergyManagementCreateSubmitAPIFunc(getActivity());
                    allergyManagementCreateSubmitAPIFunc.setDelegate(AllergyManagementFragment.this);
                    allergyManagementCreateSubmitAPIFunc.execute(allergyManagementCreateSubmitAPI);

                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_add, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_layout_add_btn);
        ImageView addBtn = (ImageView) MenuItemCompat.getActionView(searchItem);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.allergy_popup);
                dialog.setCanceledOnTouchOutside(false);

                final CustomFontEditText causative = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_causative_et);
                final CustomFontRadioButton drugTypeYes = (CustomFontRadioButton) dialog.findViewById(R.id.allergy_popup_drugtype_rb_yes);
                final CustomFontEditText reaction = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_reaction_et);
                final CustomFontEditText firstExp = (CustomFontEditText) dialog.findViewById(R.id.allergy_popup_firsttime_et);
                CustomFontButton cancelBtn = (CustomFontButton) dialog.findViewById(R.id.allergy_popup_cancel_btn);
                CustomFontButton addBtn = (CustomFontButton) dialog.findViewById(R.id.allergy_popup_add_btn);

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final AwesomeValidation mAwesomeValidation = new AwesomeValidation(UNDERLABEL);
                mAwesomeValidation.setContext(getActivity());
                mAwesomeValidation.addValidation(causative, RegexTemplate.NOT_EMPTY, getString(R.string.causative_agent_empty));
                mAwesomeValidation.addValidation(reaction, RegexTemplate.NOT_EMPTY, getString(R.string.reaction_empty));
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAwesomeValidation.validate()) {
                            String causativeString = causative.getText().toString();
                            String drugTypeString = drugTypeYes.isChecked() ? "yes" : "no";
                            String reactionString = reaction.getText().toString();
                            String firstExpString = firstExp.getText().toString();

                            AllergyManagementModel allergy = new AllergyManagementModel();
                            allergy.setAgent(causativeString);
                            allergy.setDrug(drugTypeString);
                            allergy.setReaction(reactionString);
                            allergy.setFirst_experience(firstExpString);
                            allergy.setProgress_status("1");
                            allergyManagementAdapter.addSingle(allergy);

                            AllergyManagementCreateSubmitAPI allergyManagementCreateSubmitAPI = new AllergyManagementCreateSubmitAPI();
                            allergyManagementCreateSubmitAPI.data.query.user_id = userId;
                            allergyManagementCreateSubmitAPI.data.query.allergy_agent = causativeString;
                            allergyManagementCreateSubmitAPI.data.query.allergy_is_drug = drugTypeString;
                            allergyManagementCreateSubmitAPI.data.query.allergy_reaction = reactionString;
                            allergyManagementCreateSubmitAPI.data.query.allergy_first_experience = firstExpString;

                            AllergyManagementCreateSubmitAPIFunc allergyManagementCreateSubmitAPIFunc = new AllergyManagementCreateSubmitAPIFunc(getActivity());
                            allergyManagementCreateSubmitAPIFunc.setDelegate(AllergyManagementFragment.this);
                            allergyManagementCreateSubmitAPIFunc.execute(allergyManagementCreateSubmitAPI);

                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onFinishAllergyManagementShow(ResponseAPI responseAPI) {
        if (this.isVisible()){
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if(responseAPI.status_code == 200) {
                Gson gson = new Gson();
                AllergyManagementListShowAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementListShowAPI.class);
                if (output.data.status.code.equals("200")) {
                    allergyManagementAdapter.setFailLoad(false);
                    // If refresh, clear adapter and reset the counter
                    if (output.data.query.flag.equals(Constants.FLAG_REFRESH)){
                        allergyManagementAdapter.clearList();
                        counter = 0;
                    }
                    allergyManagementAdapter.addList(output.data.results.allergy_list);
                    lastItemCounter = output.data.results.allergy_list.size();
                    counter += output.data.results.allergy_list.size();

                    if (lastItemCounter > 0){
                        allergyManagementAdapter.addSingle(null);
                    }
                } else {
                    allergyManagementAdapter.setFailLoad(true);
                    Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } else if(responseAPI.status_code == 504) {
                allergyManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            } else if(responseAPI.status_code == 401 ||
                    responseAPI.status_code == 505) {
                ((MainActivity)getActivity()).forceLogout();
            } else {
                allergyManagementAdapter.setFailLoad(true);
                Toast.makeText(getActivity(), getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFinishAllergyManagementCreateSubmit(ResponseAPI responseAPI) {
        if (this.isVisible()){
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if(responseAPI.status_code == 200) {
                Gson gson = new Gson();
                AllergyManagementCreateSubmitAPI output = gson.fromJson(responseAPI.status_response, AllergyManagementCreateSubmitAPI.class);
                if (output.data.status.code.equals("200")) {
                    allergyManagementAdapter.updateItem(output.data.results.allergyManagementModel);
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
}
