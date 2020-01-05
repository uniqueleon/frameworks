package org.aztec.framework.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author tanson lam
 * @creation 2016��8��12��
 * 
 */
public class ReflectUtil {

    /**
     * �����ȡ����ָ���������Ƶ�ֵ
     * 
     * @param obj
     * @param fieldName
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object getReflectValue(Object obj, String fieldName)
            throws IllegalArgumentException, IllegalAccessException {
        Field field = getReflectField(obj.getClass(), fieldName);
        if (field == null)
            return null;
        field.setAccessible(true);
        return field.get(obj);
    }

    public static Field getReflectField(Class<?> clazz, String fieldName) {
        if (clazz == null || clazz.equals(Object.class) || clazz.isPrimitive() || StringUtils.isEmpty(fieldName))
            return null;
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
        } catch (SecurityException e) {
        }
        if (field == null) {
            field = getReflectField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    public static Boolean hasRelflectField(Class<?> clazz, String fieldName) {
        if (clazz == null || clazz.equals(Object.class) || clazz.isPrimitive() || StringUtils.isEmpty(fieldName))
            return null;
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
        } catch (SecurityException e) {
        }
        if (field == null) {
            field = getReflectField(clazz.getSuperclass(), fieldName);
        }
        return field != null;
    }

    public static Class<?> getInnerClass(Class<?> clazz, String innerClassName) throws ClassNotFoundException {
        Class<?>[] classes = clazz.getDeclaredClasses();
        for (Class<?> innerClass : classes) {
            if (innerClass.getName().contains(innerClassName)) {
                return innerClass;
            }
        }
        throw new ClassNotFoundException("can not found innerClassName[" + innerClassName + "] in ");
    }

    /**
     * ��ȡһ��������о�̬�ڲ��� ��Щ�ڲ���ʵ����interfaceClass�ӿڻ�����interfaceClassΪ����
     * 
     * @param clazz
     * @param interfaceClass
     * @return
     */
    public static <T> List<Class<T>> getStaticInnerClass(Class<?> clazz, final Class<T> interfaceClass) {
        List<Class<T>> result = new ArrayList<Class<T>>();
        Class<?>[] classes = clazz.getDeclaredClasses();
        for (Class<?> innerClass : classes) {
            int mod = innerClass.getModifiers();
            String modifier = Modifier.toString(mod);
            if (modifier.contains("static") && interfaceClass.isAssignableFrom(innerClass)) {
                result.add((Class<T>) innerClass);
            }
        }

        return result;
    }

}
