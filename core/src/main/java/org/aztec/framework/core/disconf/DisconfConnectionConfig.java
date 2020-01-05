package org.aztec.framework.core.disconf;

public interface DisconfConnectionConfig {

    public String getWebUrl();
    public String getAppName();
    public String getEnv();
    public String getVersion();
    public String getDownloadDir();
    public boolean isRemoteEnabled();
    public String getZkConnectUrl();
}
