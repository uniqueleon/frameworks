package org.aztec.framework.heartbeat;

public enum ServerNodeStatus {

    UP(1),DOWN(-1),DISCONNECT(2);
    
    private int dbcode;

    public int getDbcode() {
        return dbcode;
    }

    public void setDbcode(int dbcode) {
        this.dbcode = dbcode;
    }

    private ServerNodeStatus(int dbcode) {
        this.dbcode = dbcode;
    }
    
    
}
