package org.aztec.framework.core.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;

/**
 * 
 * @author KingsHunter
 * @createDate May 31st,2016
 *
 */
public final class CopyUtils {

    public static void copy(Object dest, Object orig) {
        try {
            // 注册转换器，允许BeanUtils.copyProperties时sql.Timestamp类型的值允许为空
            ConvertUtils.register(new SqlTimestampConverter(null), Timestamp.class);
            BeanUtils.copyProperties(dest, orig);
        } catch (IllegalAccessException e) {
            // e.printStackTrace();
        } catch (InvocationTargetException e) {
            // e.printStackTrace();
        }
    }

    /**
     * 将origin类的List映射为dest类的List
     * 
     * @param originList
     *            原始类列表
     * @param destClass
     *            目标类class
     */
    public static <K, T> List<K> copyOriginListToDestList(List<T> originList, Class<K> destClass) {
        int size = originList.size();
        List<K> destList = new ArrayList<K>(size);
        for (int i = 0; i < size; ++i) {
            K k;
            try {
                k = destClass.newInstance();
                copy(k, originList.get(i));
                destList.add(k);
            } catch (InstantiationException | IllegalAccessException e) {
                //
            }
        }

        return destList;
    }

}
