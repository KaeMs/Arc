package com.med.fast.management.disease;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import com.med.fast.FastBaseActivity;
import com.med.fast.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kevin Murvie on 7/17/2017. FM
 */

public class SearchTest extends FastBaseActivity {
    private static final String[] MOVIES = new String[]{
            "Pirates of Carribean", "Mr Bean", "Mr Dean", "Game of Thrones", "The Walking Dead"
    };

    final Comparator<DiseaseModelTest> alphabeticalComparator = new Comparator<DiseaseModelTest>() {
        @Override
        public int compare(DiseaseModelTest a, DiseaseModelTest b) {
            return a.getName().compareTo(b.getName());
        }
    };

    @BindView(R.id.search_test_sv)
    SearchView searchView;
    @BindView(R.id.search_test_rv)
    RecyclerView recyclerView;
    private DiseaseSearchTestAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_test);
//        CommonTvInscrollBinding mBinding = DataBindingUtil.setContentView(this, R.layout.search_test);

        mAdapter = new DiseaseSearchTestAdapter(this, alphabeticalComparator);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        final List<DiseaseModelTest> mModels = new ArrayList<>();
        for (String movie : MOVIES) {
            mModels.add(new DiseaseModelTest(mModels.size(), movie));
        }
        mAdapter.edit().replaceAll(mModels).commit();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                final List<DiseaseModelTest> filteredModelList = filter(mModels, query);
                mAdapter.edit().replaceAll(filteredModelList).commit();
                recyclerView.scrollToPosition(0);
                return true;
            }
        });
    }

    private static List<DiseaseModelTest> filter(List<DiseaseModelTest> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<DiseaseModelTest> filteredModelList = new ArrayList<>();
        for (DiseaseModelTest model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

}
