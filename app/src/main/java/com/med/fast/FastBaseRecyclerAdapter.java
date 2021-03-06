package com.med.fast;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import com.med.fast.customevents.DeleteConfirmEvent;
import com.med.fast.management.DeleteConfirmIntf;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Kevin Murvie on 4/21/2017. FM
 */

public abstract class FastBaseRecyclerAdapter extends RecyclerView.Adapter {

    private boolean enableEventbus = false;
    public DeleteConfirmIntf deleteConfirmIntf;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (enableEventbus) if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
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

    public FastBaseRecyclerAdapter (boolean enableEventbus){
        this.enableEventbus = enableEventbus;
    }

    public FastBaseRecyclerAdapter (DeleteConfirmIntf deleteConfirmIntf){
        this.deleteConfirmIntf = deleteConfirmIntf;
    }

    public FastBaseRecyclerAdapter (){
        this.enableEventbus = false;
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
