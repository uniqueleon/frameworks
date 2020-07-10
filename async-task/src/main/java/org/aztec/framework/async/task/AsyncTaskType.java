package org.aztec.framework.async.task;

public enum AsyncTaskType {

    IMPORT(0),EXPORT(1),CLOSED(2);
    
    
    private int dbCode;

    private AsyncTaskType(int dbCode) {
        this.dbCode = dbCode;
    }
    
    
}
