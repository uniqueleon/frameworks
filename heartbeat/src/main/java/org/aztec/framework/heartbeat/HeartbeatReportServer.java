package org.aztec.framework.heartbeat;

import org.aztec.framework.heartbeat.impl.HeartbeatChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class HeartbeatReportServer implements ApplicationRunner{
    
    @Autowired
    HeartbeatChecker checker;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO Auto-generated method stub
        checker.start();
    }

}
