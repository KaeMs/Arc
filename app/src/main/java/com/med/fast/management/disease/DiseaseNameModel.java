package com.med.fast.management.disease;

import android.support.annotation.NonNull;

import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

/**
 * Created by Kevin Murvie on 7/19/2017. FM
 */

public class DiseaseNameModel implements SortedListAdapter.ViewModel {
    private final int id;
    private final String name;
    private final boolean selected;

    public DiseaseNameModel(int id, String name, boolean selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public <T> boolean isSameModelAs(@NonNull T t) {
        return false;
    }

    @Override
    public <T> boolean isContentTheSameAs(@NonNull T t) {
        return false;
    }
}
