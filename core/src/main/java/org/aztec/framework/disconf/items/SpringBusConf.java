package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@Service
@Scope("singleton")
@DisconfFile(filename="spring_bus_conf.properties")
public class SpringBusConf {

    private String host;
    private String port;
    private String user;
    private String password;
    private String connectionNum;

    @DisconfFileItem(name="host",associateField="host")
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }

    @DisconfFileItem(name="port",associateField="port")
    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }

    @DisconfFileItem(name="user",associateField="user")
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    @DisconfFileItem(name="password",associateField="password")
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @DisconfFileItem(name="connectionNum",associateField="connectionNum")
    public String getConnectionNum() {
        return connectionNum;
    }
    public void setConnectionNum(String connectionNum) {
        this.connectionNum = connectionNum;
    }
    
    
    
}
