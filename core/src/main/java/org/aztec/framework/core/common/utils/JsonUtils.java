package org.aztec.framework.core.common.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @author KingsHunter
 * @createDate May 30th,2016
 *
 */
public final class JsonUtils {

    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    public static String obj2String(Object object) {
        if (object != null)
            return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
        return null;
    }

    public static <T> T getClazz(String text, Class<T> clazz) {
        if (StringUtils.isBlank(text))
            return null;
        try {
            T t = JSON.parseObject(text, clazz);
            return t;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> List<T> getList(String text, Class<T> clazz) {
        if (StringUtils.isBlank(text))
            return null;
        return JSON.parseArray(text, clazz);
    }

    public static JSONObject getJSONObject(String text) {
        if (StringUtils.isBlank(text))
            return null;
        return JSON.parseObject(text);
    }

}
