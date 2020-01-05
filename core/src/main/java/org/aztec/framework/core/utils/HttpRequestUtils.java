
package org.aztec.framework.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class HttpRequestUtils {

    private static final long DATA_ACCEPT_INTERVAL = 1000;
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);
    private static final String pathParamRegex = "\\{[\\w+|_|-]+\\}";

    public static void main(String[] args) {
        String url = "add/{param1}/sub/{param2}";
        Map<String, String> params = new HashMap<String, String>();
        params.put("param1", "1");
        params.put("param2", "2");
        System.out.println(getParameterizedUrl(params, url));
    }

    private static String getParameterizedUrl(Map<String, String> pathParams, String originUrl) {
        String retUrl = originUrl;
        if (pathParams == null || pathParams.size() == 0)
            return retUrl;
        for (String key : pathParams.keySet()) {
            retUrl = retUrl.replaceAll("\\{" + key + "\\}", pathParams.get(key));
        }
        return retUrl;
    }


    /**
     * 调用Get方法类型的远程Web service
     * 
     * @param theUrl
     *            web service的URL(统一资源定位符)
     * @param retDataCls
     *            返回值的数据类型
     * @param entityCls
     *            实体类型
     * @param pathParams
     *            路径参数，以字符串键，值对的Map对象形式传入，支持多个对象，以英文逗号隔开。
     * @param headers
     *            头信息参数
     * @param parameters
     *            一般参数
     * @return 调用结果
     * @throws ClientProtocolException 
     * @throws IOException
     *             出现网络异常时抛出
     * @throws UnsupportedOperationException 
     * @throws HttpException
     *             与Http协议相关的异常抛出
     * @throws UnsupportedEncodingException
     *             当使用不存在的编码方式时抛出，因为默认使用的是UTF-8编码，理论上不会出现这个异常
     * @throws ApiException
     *             调用API接口产生的异常，通常都会把所有可以截获的异常归类为这一异常。
     */
    /*public static <T, E> T invokeGet(String theUrl,Class<T> retDataCls, Class<E> entityCls,
            Map<String, String> pathParams, Map<String, String> headers, Map<String, String> parameters) throws ApiException{
        return invokeGet(theUrl, retDataCls, entityCls, pathParams, headers, parameters,null,null);
    }*/
    
    public static File download(String theUrl,String targetPath) throws ClientProtocolException, IOException, UnsupportedOperationException{
        HttpGet getMethod = new HttpGet(theUrl);
        CloseableHttpResponse response= HttpClients.createDefault().execute(getMethod);
        File targetFile = new File(targetPath);
        if(!targetFile.exists()){
            targetFile.createNewFile();
        }
        InputStream input = response.getEntity().getContent();
        byte[] bytes = IOUtils.toByteArray(input);
        FileOutputStream fos = new FileOutputStream(targetPath);
        fos.write(bytes);
        fos.flush();
        return targetFile;
    }
    
    public static String invokeGet(String theUrl,
            Map<String, String> pathParams, Map<String, String> headers, Map<String, String> parameters,String proxyHost,Integer proxyPort)
            throws Exception {
        try {
            HttpGet getMethod = new HttpGet(getParameterizedUrl(pathParams, theUrl));
            if (headers != null) {
                for (String headerName : headers.keySet()) {
                    getMethod.addHeader(headerName, headers.get(headerName));
                }
            }
            //getMethod.addHeader("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
            // getMethod.setRequestHeader("charset", "utf-8");
            CloseableHttpResponse response= HttpClients.createDefault().execute(getMethod);
            
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
    
    

    private static <T, E> T getJsonData(String jsonData, Class<T> retType, Class<E> entityCls) throws Exception {
        List datas = new ArrayList();
        if (jsonData.startsWith("{")) {
            return JsonUtils.getClazz(jsonData, retType);
        } else if (jsonData.startsWith("[")) {
            List<E> beans = (List<E>) JsonUtils.getList(jsonData, entityCls);
            return (T) beans;
        }
        return (T) datas;
    }
    
    private static <T, E> T getXmlData(String xmlData,Class<T> retType, Class<E> entityCls){
        return null;
    }

    private static String parseParamsMap2String(Map<String, String> paramMap) {
        if (paramMap == null)
            return null;
        StringBuilder paramString = new StringBuilder();
        for (String paramName : paramMap.keySet()) {
            if (paramMap.get(paramName) == null)
                continue;
            if (!paramString.toString().isEmpty()) {
                paramString.append("&");
            }
            paramString.append(paramName + "=" + paramMap.get(paramName));
        }
        return paramString.toString();
    }

    /**
     * 调用Get方法类型的远程Web service
     * 
     * @param theUrl
     *            web service的URL(统一资源定位符)
     * @param retDataCls
     *            返回值的数据类型
     * @param entityCls
     *            实体类型
     * @param pathParams
     *            路径参数，以字符串键，值对的Map对象形式传入，支持多个对象，以英文逗号隔开。
     * @param headers
     *            头信息参数
     * @param parameters
     *            一般参数
     * @return 调用结果
     * @throws IOException
     *             出现网络异常时抛出
     * @throws HttpException
     *             与Http协议相关的异常抛出
     * @throws UnsupportedEncodingException
     *             当使用不存在的编码方式时抛出，因为默认使用的是UTF-8编码，理论上不会出现这个异常
     * @throws ApiException
     *             调用API接口产生的异常，通常都会把所有可以截获的异常归类为这一异常。
     */
    public static String invokePut(String theUrl, 
            Map<String, String> pathParams, Map<String, String> headers, Map<String, String> parameters,String requestBody)
            throws ApiException {
        try {
            HttpPut put = new HttpPut(getParameterizedUrl(pathParams, theUrl));
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                put.addHeader(entry.getKey(), entry.getValue());
            }
            if(requestBody != null && !requestBody.isEmpty()) {
                put.setEntity(EntityBuilder.create().setText(requestBody).build());
            }
            else {
                List<NameValuePair> pairs = Lists.newArrayList();
                for(String name : parameters.keySet()){
                    pairs.add(new BasicNameValuePair(name, parameters.get(name)));
                }
                put.setEntity(EntityBuilder.create().setParameters(pairs).build());
            }
            CloseableHttpResponse response = HttpClients.createDefault().execute(put);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        }
    }
    
    public static String invokeDelete(String theUrl)
            throws ApiException {
        try {
            HttpDelete delete = new HttpDelete(theUrl);
            
            CloseableHttpResponse response = HttpClients.createDefault().execute(delete);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        }
    }
    

    /*private static <T, E> T getResultData(HttpMethod method, Class<T> retDataCls, Class<E> entityCls) throws Exception {
        
        if(retDataCls.equals(InputStream.class))
            return (T) method.getResponseBodyAsStream();
        String jsonStr = new String(method.getResponseBody(), "UTF-8");
        if (jsonStr == null || jsonStr.equals(""))
            throw new ApiException("No data has been receive from remote API Server!");
        if(retDataCls.equals(String.class)){
            return (T) jsonStr;
        }
        T resultData = null;
        try {
            resultData = (T) getJsonData(jsonStr, retDataCls, entityCls);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultData;
    }*/

    /**
     * 调用Post方法类型的远程Web service
     * 
     * @param theUrl
     *            web service的URL(统一资源定位符)
     * @param retDataCls
     *            返回值的数据类型
     * @param entityCls
     *            实体类型
     * @param pathParams
     *            路径参数，以字符串键，值对的Map对象形式传入，支持多个对象，以英文逗号隔开。
     * @param headers
     *            头信息参数
     * @param parameters
     *            一般参数
     * @return 调用结果
     * @throws IOException
     *             出现网络异常时抛出
     * @throws HttpException
     *             与Http协议相关的异常抛出
     * @throws UnsupportedEncodingException
     *             当使用不存在的编码方式时抛出，因为默认使用的是UTF-8编码，理论上不会出现这个异常
     * @throws ApiException
     *             调用API接口产生的异常，通常都会把所有可以截获的异常归类为这一异常。
     */
    public static String invokePost(String theUrl, 
            Map<String, String> pathParams, Map<String, String> headers, Map<String, String> parameters
            ,String requestBody)
            throws ApiException {
        try {
            HttpPost post = new HttpPost(getParameterizedUrl(pathParams, theUrl));
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                post.addHeader(entry.getKey(), entry.getValue());
            }
            if(requestBody != null && !requestBody.isEmpty()) {
                post.setEntity(EntityBuilder.create().setText(requestBody).build());
            }
            else {
                List<NameValuePair> pairs = Lists.newArrayList();
                for(String name : parameters.keySet()){
                    pairs.add(new BasicNameValuePair(name, parameters.get(name)));
                }
                post.setEntity(EntityBuilder.create().setParameters(pairs).build());
            }
            CloseableHttpResponse response = HttpClients.createDefault().execute(post);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        }
    }
    
    /*public static <T, E> T uploadFile(String theUrl, Class<T> retDataCls, Class<E> entityCls,
            Map<String, String> pathParams, Map<String, String> headers, Map<String, String> parameters
            ,Map<String,File> files)
            throws ApiException {
        try {
            PostMethod post = new PostMethod(getParameterizedUrl(pathParams, theUrl));
            post.setRequestHeader("content-type", "multipart/form-data;boundary=" + HttpMethodParams.MULTIPART_BOUNDARY);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                post.setRequestHeader(entry.getKey(), entry.getValue());
            }

            HttpMethodParams hmp = post.getParams();
            List<Part> parts = new ArrayList<>();
            for(String paramKey : parameters.keySet()){
                parts.add(new StringPart(paramKey, parameters.get(paramKey)));
            }
            for(String fileKey : files.keySet()){
                parts.add(new FilePart(fileKey, files.get(fileKey)));
            }
            MultipartRequestEntity mpr = new MultipartRequestEntity(parts.toArray(new Part[parts.size()]), hmp);
            post.setRequestEntity(mpr);
            HttpClient httpclient = new HttpClient();
            int result = httpclient.executeMethod(post);
            return getResultData(post, retDataCls, entityCls);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        }
    }*/

    /**
     * 调用Delete方法类型的远程Web service
     * 
     * @param theUrl
     *            web service的URL(统一资源定位符)
     * @param retDataCls
     *            返回值的数据类型
     * @param entityCls
     *            实体类型
     * @param pathParams
     *            路径参数，以字符串键，值对的Map对象形式传入，支持多个对象，以英文逗号隔开。
     * @param headers
     *            头信息参数
     * @param parameters
     *            一般参数
     * @return 调用结果
     * @throws IOException
     *             出现网络异常时抛出
     * @throws HttpException
     *             与Http协议相关的异常抛出
     * @throws UnsupportedEncodingException
     *             当使用不存在的编码方式时抛出，因为默认使用的是UTF-8编码，理论上不会出现这个异常
     * @throws ApiException
     *             调用API接口产生的异常，通常都会把所有可以截获的异常归类为这一异常。
     */
    /*public static <T, E> T invokeDelete(String theUrl, Class<T> retDataCls, Class<E> entityCls,
            Map<String, String> pathParams, Map<String, String> headers, Map<String, String> parameters)
            throws ApiException {
        try {
            DeleteMethod delete = new DeleteMethod(getParameterizedUrl(pathParams, theUrl));
            delete.setRequestHeader("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                delete.setRequestHeader(entry.getKey(), entry.getValue());
            }
            delete.setRequestHeader("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
            // getMethod.setRequestHeader("charset", "utf-8");
            delete.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
            HttpMethodParams params = new HttpMethodParams();
            if (parameters != null) {
                for (String paramName : parameters.keySet()) {
                    params.setParameter(paramName, parameters.get(paramName));
                }
            }
            delete.setParams(new HttpMethodParams());
            List<NameValuePair> paires = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                paires.add(new NameValuePair(param.getKey(), param.getValue()));
            }
            delete.setQueryString(paires.toArray(new NameValuePair[paires.size()]));
            HttpClient httpclient = new HttpClient();
            int result = httpclient.executeMethod(delete);
            return getResultData(delete, retDataCls, entityCls);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(e.getMessage(), e);
        }
    }*/
    
    

}