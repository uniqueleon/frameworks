package org.aztec.framework.mybatis.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.aztec.framework.mybatis.exceptions.UnexpectedException;

/**
 * custom time format
 * 
 * @author 01390620
 *
 */
public final class CustomTimeFormatUtils {

    /**
     * time format
     * 
     * @param timeInMills
     * @param pattern
     * @return
     */
    public static String getTimeFormat(long timeInMills, String pattern) {
        if (timeInMills <= 0)
            throw new UnexpectedException("timeInMills illegal");
        return DateFormatUtils.format(timeInMills, pattern);
    }

    public static int getYyyyMmDdInt(long timeInMills) {
        return Integer.valueOf(getNoSeparationCharacterYyyyMmDdStr(timeInMills));
    }

    public static String getNormalCharacterYyyyMmDdStr(long timeInMills) {
        return getTimeFormat(timeInMills, "yyyy-MM-dd");
    }

    public static String getNoSeparationCharacterYyyyMmDdStr(long timeInMills) {
        return getTimeFormat(timeInMills, "yyyyMMdd");
    }

    public static String getNormalCharacterYyyyMmDdHhMmSsStr(long timeInMills) {
        return getTimeFormat(timeInMills, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getNormalCharacterYyyyMmDdHhMmSsSssStr(long timeInMills) {
        return getTimeFormat(timeInMills, "yyyy-MM-dd HH:mm:ss,SSS");
    }

    public static String getNoSeparationCharacterYyyyMmDdHhMmSsSssStr(long timeInMills) {
        return getTimeFormat(timeInMills, "yyyyMMddHHmmssSSS");
    }

    public static long getyyyyMMddHHmmssSSSLong(long timeInMills) {
        return Long.valueOf(getNoSeparationCharacterYyyyMmDdHhMmSsSssStr(timeInMills));
    }

}
