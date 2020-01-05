package org.aztec.framework.web.interceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.aztec.framework.web.util.HttpRequestWrapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class EntrypointLogInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger("entrypoint");

    private static final String[] BANNERS = new String[] {
            "#######################REQUEST[%s]########################\n",
            "#######################RESPONSE[%s]########################\n",
            "#######################COMPLETE[%s]########################\n", };
    private static final String LOG_PREFIX = "[ENTRYPOINT]:";
    private static final String LINE_SPLITOR = "\n";

    private static ThreadLocal<String> localTraceID = new ThreadLocal<String>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        localTraceID.set(RandomStringUtils.randomAlphabetic(16));
        StringBuilder sb = new StringBuilder(getBanner(0));
        sb.append(getRequestInfo(request));
        sb.append(getRequestHeaderInfo(request));
        sb.append(getRequestBody(request));
        sb.append(getRequestForm(request));
        sb.append(getBanner(0));
        LOG.info(sb.toString());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private String getBanner(int index) {
        return String.format(BANNERS[index], localTraceID.get());
    }

    private String getRequestInfo(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder("[REQUEST_INFOS]:" + LINE_SPLITOR);
        sb.append("REQUEST_URI:" + request.getRequestURI() + LINE_SPLITOR);
        sb.append("CONTENT_TYPE:" + request.getContentType() + LINE_SPLITOR);
        sb.append("CONTENT_LENGTH:" + request.getContentLengthLong() + LINE_SPLITOR);
        sb.append("REQUEST_METHOD:" + request.getMethod() + LINE_SPLITOR);
        sb.append("CONTEXT_PATH:" + request.getContextPath() + LINE_SPLITOR);
        sb.append("REMOTE_ADDRESS:" + request.getRemoteAddr() + LINE_SPLITOR);
        sb.append("REMOTE_HOST:" + request.getRemoteHost() + LINE_SPLITOR);
        sb.append("REMOTE_PORT:" + request.getRemotePort() + LINE_SPLITOR);
        sb.append("CHARACTER_ENCODING:" + request.getCharacterEncoding() + LINE_SPLITOR);
        return sb.toString();
    }

    private String getRequestHeaderInfo(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder("[REQUEST_HEADERS]:" + LINE_SPLITOR);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            sb.append(headerName + "=" + request.getHeader(headerName) + LINE_SPLITOR);
        }
        return sb.toString();
    }

    private String getRequestBody(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder("[REQUEST_BODY]:" + LINE_SPLITOR);
        try {
            sb.append(HttpRequestWrapperUtil.getBodyString(request) + LINE_SPLITOR);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String getRequestForm(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder("[REQUEST_FROM]:" + LINE_SPLITOR);
        Map<String, String[]> requestMap = request.getParameterMap();
        for (Entry<String, String[]> requestEntry : requestMap.entrySet()) {
            sb.append(requestEntry.getKey() + "=" + Arrays.toString(requestEntry.getValue()) + LINE_SPLITOR);
        }
        return sb.toString();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub

        StringBuilder sb = new StringBuilder(getBanner(1));
        sb.append(getResponseInfo(response));
        sb.append(getResponseHeaderInfo(response));
        sb.append(getBanner(1));
        LOG.info(sb.toString());
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    private String getResponseInfo(HttpServletResponse response) {

        StringBuilder sb = new StringBuilder("[RESPONSE_INFO]:" + LINE_SPLITOR);
        sb.append("CONTENT_TYPE:" + response.getContentType() + LINE_SPLITOR);
        sb.append("STATUS:" + response.getStatus() + LINE_SPLITOR);
        sb.append("ENCODING:" + response.getCharacterEncoding() + LINE_SPLITOR);
        return sb.toString();
    }

    private String getResponseHeaderInfo(HttpServletResponse response) {
        StringBuilder sb = new StringBuilder("[RESPONSE_HEADERS]:" + LINE_SPLITOR);
        for (String headerName : response.getHeaderNames()) {
            sb.append(headerName + "=" + response.getHeader(headerName) + LINE_SPLITOR);
        }
        return sb.toString();
    }

    private String getReponseBody(HttpServletResponse response) {

        StringBuilder sb = new StringBuilder("[RESPONSE_BODY]:" + LINE_SPLITOR);

        return sb.toString();
    }

    // public String

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        StringBuilder sb = new StringBuilder(getBanner(2));
        sb.append(getPrintStack(ex));
        sb.append(getBanner(2));
        LOG.info(sb.toString());
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private String getPrintStack(Exception e) {
        if (e != null) {

            StringBuilder sb = new StringBuilder(
                    "[EXCEPTION]" + e.getClass().getName() + ":" + e.getMessage() + LINE_SPLITOR);
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append(ste.getClassName() + ": " + ste.getMethodName() + " (" + ste.getFileName() + " line:"
                        + ste.getLineNumber() + ")" + LINE_SPLITOR);
            }
            return sb.toString();
        }
        return "";
    }

}
