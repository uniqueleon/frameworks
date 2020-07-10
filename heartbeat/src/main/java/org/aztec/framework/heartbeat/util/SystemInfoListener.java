package org.aztec.framework.heartbeat.util;

public interface SystemInfoListener {

    public boolean trigger(ComputerInfo comInfo);
    
    public void notify(ComputerInfo comInfo);
}
