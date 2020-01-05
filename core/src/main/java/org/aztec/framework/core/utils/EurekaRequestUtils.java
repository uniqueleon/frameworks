package org.aztec.framework.core.utils;

import java.util.Map;

import com.google.api.client.util.Maps;

public class EurekaRequestUtils {

    public EurekaRequestUtils() {
        // TODO Auto-generated constructor stub
    }
    
    

    public static void sendServiceUpRequest(String appName,String instanceId) throws ApiException{
        //String theUrl = "http://eureka-server:8890/eureka-server/service-registry/instance-status";
        String theUrl = "http://eureka-server:8890/eureka/apps/" + appName + "/" +instanceId+ "/status?value=UP";
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Content-type", "application/json");
        String requestBody = "";
        String response = HttpRequestUtils.invokePut(theUrl, Maps.newHashMap(), headers, Maps.newHashMap(), requestBody);
        System.out.println(response);
    }
    
    public static void sendServiceDownRequest(String appName,String instanceId) throws ApiException{
        //String theUrl = "http://eureka-server:8890/eureka-server/service-registry/instance-status";
        String theUrl = "http://eureka-server:8890/eureka/apps/" + appName + "/" +instanceId + "/status?value=OUT_OF_SERVICE";
        Map<String,String> headers = Maps.newHashMap();
        headers.put("Content-type", "application/json");
        String requestBody = "";
        String response = HttpRequestUtils.invokePut(theUrl, Maps.newHashMap(), headers, Maps.newHashMap(), requestBody);
        System.out.println(response);
    }
    
    public static void sendServiceDeleteRequest(String appName,String instanceId) throws ApiException {
        String theUrl = "http://eureka-server:8890/eureka/apps/" + appName + "/" +instanceId;
        String response = HttpRequestUtils.invokeDelete(theUrl);
        System.out.println(response);
    }


}
