package org.aztec.framework.web.socket;

public enum DestinationType {

    BROADCAST(1),MULTIPLE(2),SINGLE(3);
    
    private int code;

    public int getCode() {
        return code;
    }
    
    private DestinationType(int code) {
        this.code = code;
    }
    
    public static DestinationType getType(int code){
        for(DestinationType type : DestinationType.values()){
            if(type.getCode() == code){
                return type;
            }
        }
        return null;
    }
    
}
