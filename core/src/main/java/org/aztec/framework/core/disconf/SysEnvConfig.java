package org.aztec.framework.core.disconf;

import org.springframework.stereotype.Component;

@Component
public class SysEnvConfig implements DisconfConnectionConfig {

    @Override
    public String getWebUrl() {
        return System.getProperty("disconf.conf_server_host");
    }

    @Override
    public String getAppName() {
        return System.getProperty("disconf.app");
    }

    @Override
    public String getEnv() {
        String evn = System.getProperty("disconf.env");
        return evn != null ? evn : "online";
    }

    @Override
    public String getVersion() {
        String version = System.getProperty("disconf.version");
        return version != null ? version : "1_0_0";
    }

    @Override
    public String getDownloadDir() {
        String downloadUrl = System.getProperty("disconf.user_define_download_dir");
        return downloadUrl != null ? downloadUrl : "disconf/download";
    }

    @Override
    public boolean isRemoteEnabled() {
        String evnConfig = System.getProperty("disconf.enable.remote.conf");
        return evnConfig != null ? Boolean.parseBoolean(evnConfig) : true;
    }

    @Override
    public String getZkConnectUrl() {
        String url =  System.getProperty("disconf.zk");
        return url != null ? url : "zk-host" ;
    }

}
