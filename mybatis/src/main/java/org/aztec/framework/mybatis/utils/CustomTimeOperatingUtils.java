package org.aztec.framework.mybatis.utils;

import java.time.Clock;

public class CustomTimeOperatingUtils {

    public static long currentTimeInMills() {
        return Clock.systemUTC().millis();
    }

    public static long currentTimeInSeconds() {
        return currentTimeInMills() / 1000;
    }

}
