package com.med.fast.customevents;

/**
 * Created by Kevin Murvie on 4/29/2017. FM
 */

public class DeleteConfirmEvent {
    public String deletionId;
    public DeleteConfirmEvent(String deletionId){
        this.deletionId = deletionId;
    }
}
