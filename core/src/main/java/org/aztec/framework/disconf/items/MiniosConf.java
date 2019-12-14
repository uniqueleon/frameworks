package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@Service
@Scope("singleton")
@DisconfFile(filename="minio_conf.properties")
public class MiniosConf {

    private String url;
    private String appKey;
    private String appSecret;
    private String bucket;
    private String downloadTimeout = "5";
    public String getUrl() {
        return url;
    }

    @DisconfFileItem(name="minio.url",associateField="url")
    public void setUrl(String url) {
        this.url = url;
    }

    @DisconfFileItem(name="app.key",associateField="appKey")
    public String getAppKey() {
        return appKey;
    }
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @DisconfFileItem(name="app.secret",associateField="appSecret")
    public String getAppSecret() {
        return appSecret;
    }
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
    @DisconfFileItem(name="bucket",associateField="bucket")
    public String getBucket() {
        return bucket;
    }
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @DisconfFileItem(name="downloadTimeout",associateField="downloadTimeout")
    public String getDownloadTimeout() {
        return downloadTimeout;
    }

    public void setDownloadTimeout(String downloadTimeout) {
        this.downloadTimeout = downloadTimeout;
    }

    
}
