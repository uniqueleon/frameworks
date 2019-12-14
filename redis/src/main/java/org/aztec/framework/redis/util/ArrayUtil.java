package org.aztec.framework.redis.util;

public class ArrayUtil {

    public static boolean isEmpty(Object... objects){
        if(objects == null || objects.length == 0){
            return true;
        }
        return false;
    }
}
