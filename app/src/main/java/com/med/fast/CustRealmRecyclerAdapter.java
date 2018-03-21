package com.med.fast;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import com.med.fast.management.DeleteConfirmIntf;

import org.greenrobot.eventbus.EventBus;

import io.realm.OrderedRealmCollection;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by KM on 11/13/2017. KSM
 */

public abstract class CustRealmRecyclerAdapter<T extends RealmModel, S extends RecyclerView.ViewHolder>
        extends RealmRecyclerViewAdapter<T, S> {

    public CustRealmRecyclerAdapter(@Nullable OrderedRealmCollection<T> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    public CustRealmRecyclerAdapter (@Nullable OrderedRealmCollection<T> data , boolean autoUpdate, boolean enableEventbus){
        super(data, autoUpdate);
        this.enableEventbus = enableEventbus;
    }

    public CustRealmRecyclerAdapter (@Nullable OrderedRealmCollection<T> data, DeleteConfirmIntf deleteConfirmIntf){
        super(data, true);
        this.deleteConfirmIntf = deleteConfirmIntf;
    }

    public CustRealmRecyclerAdapter (@Nullable OrderedRealmCollection<T> data){
        super(data, true);
        this.enableEventbus = false;
    }

    public RealmQuery<T> realmQuery;

    /*public void filterResults(List<String> fieldFilter, List<String> queryFilter) {
        if (realmQuery != null){
            for (int i = 0; i < fieldFilter.size(); i++){
                String field = fieldFilter.get(i);
                String query = queryFilter.get(i);
//                query = query == null ? null : query.toLowerCase().trim();
                if (TextUtils.isEmpty(field) ||
                        TextUtils.isEmpty(query)) {
                    // Ignored
                } else if (field.equals(SortSpinnerItems.CUSTOMER_NAME.getId())) {
                    if (query.contains(" ")) {
                        String[] salesName = query.split(" ");
                        if (salesName.length > 1) {
                            realmQuery.like(field, "*" + salesName[0] + "*", Case.INSENSITIVE)
                                    .findAll()
                                    .where()
                                    .like(SortSpinnerItems.CUSTOMER_NAME.getId(), "*" + salesName[1] + "*", Case.INSENSITIVE)
                                    .findAll()*//*Sorted(field, Sort.DESCENDING)*//*;
                        } else {
                            realmQuery.like(field, "*" + query + "*", Case.INSENSITIVE)
                                    .or()
                                    .like(SortSpinnerItems.CUSTOMER_NAME.getId(), "*" + query + "*", Case.INSENSITIVE)
                                    .findAll()*//*Sorted(field, Sort.DESCENDING)*//*;
                        }
                    } else {
                        realmQuery.like(field, "*" + query + "*", Case.INSENSITIVE)
                                .findAll()*//*Sorted(field, Sort.DESCENDING)*//*;
                    }
                } else if (field.equals(SortSpinnerItems.FIRST_NAME_SALESMAN.getId())) {
                    if (query.contains(" ")) {
                        String[] salesName = query.split(" ");
                        if (salesName.length > 1) {
                            realmQuery.like(field, "*" + salesName[0] + "*", Case.INSENSITIVE)
                                    .findAll()
                                    .where()
                                    .like(SortSpinnerItems.LAST_NAME_SALESMAN.getId(), "*" + salesName[1] + "*", Case.INSENSITIVE)
                                    .findAll()*//*Sorted(field, Sort.DESCENDING)*//*;
                        } else {
                            realmQuery.like(field, "*" + query + "*", Case.INSENSITIVE)
                                    .or()
                                    .like(SortSpinnerItems.LAST_NAME_SALESMAN.getId(), "*" + query + "*", Case.INSENSITIVE)
                                    .findAll()*//*Sorted(field, Sort.DESCENDING)*//*;
                        }
                    } else {
                        realmQuery.like(field, "*" + query + "*", Case.INSENSITIVE)
                                .findAll()*//*Sorted(field, Sort.DESCENDING)*//*;
                    }
                } else if (field.equals(SortSpinnerItems.FIRST_NAME_SALES.getId())) {
                    if (query.contains(" ")) {
                        String[] salesName = query.split(" ");
                        if (salesName.length > 1) {
                            realmQuery.like(field, "*" + salesName[0] + "*", Case.INSENSITIVE)
                                    .findAll()
                                    .where()
                                    .like(SortSpinnerItems.LAST_NAME_SALES.getId(), "*" + salesName[1] + "*", Case.INSENSITIVE)
                                    .findAll()*//*Sorted(field, Sort.DESCENDING)*//*;
                        } else {
                            realmQuery.like(field, "*" + query + "*", Case.INSENSITIVE)
                                    .or()
                                    .like(SortSpinnerItems.LAST_NAME_SALES.getId(), "*" + query + "*", Case.INSENSITIVE)
                                    .findAll()*//*Sorted(field, Sort.DESCENDING)*//*;
                        }
                    } else {
                        realmQuery.like(field, "*" + query + "*", Case.INSENSITIVE)
                                .findAll()*//*Sorted(field, Sort.DESCENDING)*//*;
                    }
                } else if (field.equals(SortSpinnerItems.REMAIN_SO.getId()) ||
                        field.equals(SortSpinnerItems.REMAIN_ON_DELIVERED.getId()) ||
                        field.equals(SortSpinnerItems.UNIT_PRICE.getId()) ||
                        field.equals(SortSpinnerItems.REMAIN_LOADED.getId()) ||
                        field.equals(SortSpinnerItems.TOTAL_NETT_SALES_IDR.getId()) ||
                        field.equals(SortSpinnerItems.TOTAL_TARGET_SALES.getId()) ||
                        field.equals(SortSpinnerItems.REMAIN_TARGET_SALES.getId())) {
                    try {
                        realmQuery.equalTo(field, CalcUtils.stringToDouble(query));
                    } catch (Exception ignored){

                    }
                } else if (field.equals(SortSpinnerItems.DATE.getId()) ||
                        field.equals(SortSpinnerItems.DUE_DATE.getId())) {
                    *//*try {
                        String dateFormatted = DateUtils.getFormattedDateDashForAPI(query);
                        realmQuery.equalTo(field, dateFormatted, Case.INSENSITIVE);
                    } catch (Exception e){
                        realmQuery.equalTo(field, query, Case.INSENSITIVE);
                    }*//*
                    try {
                        realmQuery.equalTo(field, query, Case.INSENSITIVE);
                    } catch (Exception ignored) {

                    }
                } else if (field.equals(SortSpinnerItems.TRANSACTION_DATE.getId()) ||
                        field.equals(SortSpinnerItems.EXPIRED_DATE.getId())) {
                    try {
                        realmQuery.equalTo(field, query, Case.INSENSITIVE);
                    } catch (Exception ignored){
//                        realmQuery.equalTo(field, query, Case.INSENSITIVE);
                    }
                } else if (field.equals(SortSpinnerItems.DEAL_STEP_LEVEL.getId())) {
                    realmQuery.equalTo(field, query, Case.INSENSITIVE);
                } else if (field.equals(SortSpinnerItems.CUSTOMER_ID.getId())) {
                    realmQuery.beginsWith(field, query, Case.INSENSITIVE);
                } else if (field.equals(SortSpinnerItems.MONTH_SALES.getId()) ||
                                field.equals(SortSpinnerItems.YEAR_SALES.getId())) {
                    realmQuery.equalTo(field, CalcUtils.stringToInt(query));
                } else {
                    try {
                        realmQuery.contains(field, query, Case.INSENSITIVE);
                    } catch (Exception ignored){

                    }
                }
            }
            updateData(realmQuery.findAll());
        }
    }*/

    private boolean enableEventbus = false;
    public DeleteConfirmIntf deleteConfirmIntf;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (enableEventbus) if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (enableEventbus)
            if (EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().unregister(this);
    }

    public void forceEventbusUnregistration(){
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void createDeleteDialog(Context context, String title, final String deletionId){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        EventBus.getDefault().post(new DeleteConfirmEvent(deletionId));
                        deleteConfirmIntf.onDeleteConfirm(deletionId);
                    }
                })
                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    protected void setItemDecoration(RecyclerView recyclerView, RecyclerView.ItemDecoration itemDecoration){
        recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(itemDecoration);
    }
}
