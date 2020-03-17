package org.aztec.framework.core.disconf;

import java.io.IOException;
import java.util.Properties;

import org.aztec.framework.core.FrameworkLogger;
import org.springframework.stereotype.Component;

/**
 * 使用系统参数来进行disconf环境配置
 * @author liming
 *
 */
@Component
public class SysEnvConfig implements DisconfConnectionConfig {
    
    static {
        try {
            Properties properties = new Properties();
            properties.load(SysEnvConfig.class.getResourceAsStream("/disconf.properties"));
            String webUrl = System.getProperty("disconf.conf_server_host");
            if(webUrl == null && properties.containsKey("conf_server_host")){
                System.setProperty("disconf.conf_server_host",properties.getProperty("conf_server_host"));
            }
            String appName = System.getProperty("disconf.app");
            if(appName == null  && properties.containsKey("app")){
                System.setProperty("disconf.app",properties.getProperty("app"));
            }
            String evn = System.getProperty("disconf.env");
            if(evn == null   && properties.containsKey("env")){
                System.setProperty("disconf.env",properties.getProperty("env"));
            }
            String version = System.getProperty("disconf.version");
            if(version == null  && properties.containsKey("version")){

                System.setProperty("disconf.version",properties.getProperty("version"));
            }
            String downloadDir =  System.getProperty("disconf.user_define_download_dir");
            if(downloadDir == null && properties.containsKey("download_url")){

                System.setProperty("disconf.user_define_download_dir",properties.getProperty("download_url"));
            }
            String enabled =  System.getProperty("disconf.enable.remote.conf");
            if(enabled == null && properties.containsKey("enable.remote.conf")){

                System.setProperty("disconf.enable.remote.conf",properties.getProperty("enable.remote.conf"));
            }
            String zkHost = System.getProperty("disconf.zk");
            if(zkHost == null && properties.containsKey("zk_host")){
                System.setProperty("disconf.zk",properties.getProperty("zk_host"));
            }
        } catch (IOException e) {
            FrameworkLogger.error(e);
        }
    }

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
        return url != null ? url : "zk-host:2181" ;
    }

}