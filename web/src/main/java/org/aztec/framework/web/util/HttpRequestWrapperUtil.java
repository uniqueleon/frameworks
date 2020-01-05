package org.aztec.framework.web.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestWrapperUtil {

    public static String getBodyString(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            ContentCachingRequestWrapper requestWapper = null;
            if (request instanceof ContentCachingRequestWrapper) {
                requestWapper = (ContentCachingRequestWrapper) request;
                String body = new String(requestWapper.getBody(), request.getCharacterEncoding());
                return body;
            }
            else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return sb.toString();
    }
}