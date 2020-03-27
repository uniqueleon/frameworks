package org.aztec.framework.api;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.aztec.framework.api.BasicException.BasicErrorCodes;

public class Asserts {

    
    public static <T extends BasicException>  void fieldNotNull(Object targetObj,String[] fieldNames,String[] errorCodes,T t) throws T{
        if(targetObj == null || fieldNames == null || fieldNames.length == 0){
            t.setErrorCode(BasicErrorCodes.ILLEAGAL_ARGUMENTS);
            throw t;
        }
        Class targetCls = targetObj.getClass();
        for(int i = 0;i < fieldNames.length;i++){
            try {
                String fieldName = fieldNames[i];
                Method method = targetCls.getDeclaredMethod("get" + StringUtils.capitalize(fieldName));
                method.setAccessible(true);
                Object fieldObj = method.invoke(targetObj);
                if(fieldObj == null){
                    t.setErrorCode(errorCodes[i]);
                    throw t;
                }
            } catch (BasicException te){
                throw te;
            } catch (Exception e) {
                t.setErrorCode(BasicErrorCodes.FIELD_NOT_FOUND);
                throw t;
            }
            
        }
    }
}
