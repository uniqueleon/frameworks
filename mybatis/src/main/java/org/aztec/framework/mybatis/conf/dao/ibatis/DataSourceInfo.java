package org.aztec.framework.mybatis.conf.dao.ibatis;

public class DataSourceInfo {

    private String name;
    private String url;
    private String driver;
    private String user;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DataSourceInfo(String name, String url, String driver, String user, String password) {
        super();
        this.name = name;
        this.url = url;
        this.driver = driver;
        this.user = user;
        this.password = password;
    }
}
