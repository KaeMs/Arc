package com.med.fast.management.disease;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

/**
 * Created by GMG-Developer on 7/17/2017.
 */

public class DiseaseModelTest implements SortedListAdapter.ViewModel {
    private final int id;
    private final String name;

    public DiseaseModelTest(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> boolean isSameModelAs(@NonNull T t) {
        if (t instanceof DiseaseModelTest) {
            final DiseaseModelTest diseaseModelTest = (DiseaseModelTest) t;
            return diseaseModelTest.getId() == id;
        }
        return false;
    }

    @Override
    public <T> boolean isContentTheSameAs(@NonNull T item) {
        if (item instanceof DiseaseModelTest) {
            final DiseaseModelTest other = (DiseaseModelTest) item;
            return name.equals(other.name) && name.equals(other.name);
        }
        return false;
    }
}
