package org.aztec.framework.web.util.render;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aztec.framework.api.rest.entity.RenderParam;
import org.aztec.framework.api.rest.entity.RenderParamContext;
import org.aztec.framework.web.util.UI_ElementRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component("BaseRender")
public class BaseRender implements UI_ElementRender, BeanFactoryAware {

    private BeanFactory bf;

    @Autowired
    // @Qualifier("messageSource")
    private MessageSource i18nMsg;

    public static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

    private static final Logger LOG = LoggerFactory.getLogger(BaseRender.class);

    private static final String DATA_SOURCE_SUFFIX = ".data_source";
    private static final String PARAMS_SUFFIX = ".params";
    private static final String ATTRIBUTES_SUFFIX = ".attrs";
    private static final String CLASS_SUFFIX = ".clz";
    private static final String ALIAS_SUFFIX = ".alias";
    private static final String EXTEND_SUFFIX = ".extend";
    
    private static final String[] ENUM_LABLE_FIELDS = new String[]{"code","name","label"};
    private static final String[] ENUM_VALUE_FIELDS = new String[]{"name","value","text"};
    private static final String[] ENUM_EXTEND_FIELDS = new String[]{"extend"};
    
    
    private static Map<String, RenderParamMetaData> metaDataCache = Maps.newConcurrentMap();

    @Override
    public RenderParamContext renderByConf(String prefix) {
        // TODO Auto-generated method stub
        RenderParamContext renderParamContext = new RenderParamContext();
        String allParams = i18nMsg.getMessage(prefix + PARAMS_SUFFIX, new String[] {}, DEFAULT_LOCALE);
        String[] paramArr = allParams.split(",");
        for (String param : paramArr) {
            try {
                renderParamContext.addParam(param, getRenderParamByConf(prefix + "." + param));
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        return renderParamContext;
    }

    public List<RenderParam> getRenderParamByConf(String eleName) throws ClassNotFoundException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        // String eleName = prefix + "." + name;
        String value = i18nMsg.getMessage(eleName + DATA_SOURCE_SUFFIX, new String[] {}, DEFAULT_LOCALE);

        DataSourceType dsType = DataSourceType.getSourceType(value);
        List<RenderParam> renderParams = Lists.newArrayList();
        switch (dsType) {
        case CONF:
            String attributes = i18nMsg.getMessage(eleName + ATTRIBUTES_SUFFIX, new String[] {}, DEFAULT_LOCALE);
            if (attributes != null && !attributes.isEmpty()) {
                String[] attrs = attributes.split(",");
                for (String attr : attrs) {
                    String attrValue = i18nMsg.getMessage(eleName + "." + attr, new String[] {}, DEFAULT_LOCALE);
                    String extValue = "";
                    try {
                        extValue = i18nMsg.getMessage(eleName + "." + attr + EXTEND_SUFFIX, new String[] {},
                                DEFAULT_LOCALE);
                    } catch (NoSuchMessageException e) {
                        // TO DO NOTING
                    }
                    RenderParam param = new RenderParam(attr, attrValue, extValue);
                    renderParams.add(param);
                }
            }
            break;
        case ENUM:
            String className = i18nMsg.getMessage(eleName + CLASS_SUFFIX, new String[] {}, DEFAULT_LOCALE);
            return loadRenderParamByEnum(className);
        case CUSTOMERIZED:
            loadParamFromBeanFactory(eleName);
            break;
        case ALIAS:
            String alias = i18nMsg.getMessage(eleName + ALIAS_SUFFIX, new String[] {}, DEFAULT_LOCALE);
            return getRenderParamByConf(alias);
        }
        return renderParams;
    }

    public String getEumnData(Object obj, Class enumCls, String fieldName) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

        Method invMethod = enumCls.getDeclaredMethod("get" + StringUtils.capitalize(fieldName));
        invMethod.setAccessible(true);
        Object retObject = invMethod.invoke(obj);
        return retObject.toString();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // TODO Auto-generated method stub
        this.bf = beanFactory;
    }

    public static enum TestEnum {

        EN("1", "dd", "dd"), DIS("2", "dd", "dd");
        private String label;
        private String value;
        private String extend;

        private TestEnum(String label, String vale, String extend) {
            this.label = label;
            this.value = vale;
            this.extend = extend;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String vale) {
            this.value = vale;
        }

        public String getExtend() {
            return extend;
        }

        public void setExtend(String extend) {
            this.extend = extend;
        }

    }

    @Override
    public RenderParamContext renderByMetaData(List<RenderParamMetaData> metaDatas) {
        RenderParamContext renderParamContext = new RenderParamContext();
        if(!CollectionUtils.isEmpty(metaDatas)){
            metaDatas.stream().forEach(metaData -> {
                try {
                    renderParamContext.addParam(metaData.getName(), getRenderParam(metaData));
                } catch (Exception e) {
                    LOG.error(e.getMessage(),e);
                }
            });
        }
        return renderParamContext;
    }

    public List<RenderParam> loadRenderParamByEnum(String className)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, ClassNotFoundException {
        List<RenderParam> renderParams = Lists.newArrayList();
        Class enumCls = Class.forName(className);
        if (enumCls.isEnum()) {
            Object[] enumObjs = enumCls.getEnumConstants();

            for (Object enumObj : enumObjs) {
                RenderParam param = new RenderParam(getDataFromEnum(enumObj, ENUM_LABLE_FIELDS),
                        getDataFromEnum(enumObj, ENUM_VALUE_FIELDS), getDataFromEnum(enumObj, ENUM_EXTEND_FIELDS));
                renderParams.add(param);
            }
        }
        return renderParams;
    }
    
    private String getDataFromEnum(Object obj,String[] fieldArr){
        for(String labelField : fieldArr){
            try {
                return getEumnData(obj, obj.getClass(), labelField);
            } catch (Exception e) {
                
            }
        }
        return "";
    }
    
    
    public List<RenderParam> loadParamFromBeanFactory(String beanName){
        CustomerizeElementRander customerRender = bf.getBean(beanName, CustomerizeElementRander.class);
        if (customerRender != null) {
            return customerRender.getRenderParam();
        }
        return Lists.newArrayList();
    }
    
    private static String getMetaDataID(RenderParamMetaData metaData){
        return metaData.getPrefix() + "." + metaData.getName();
    }
    
    public static void cacheMetaData(RenderParamMetaData metaData){
        metaDataCache.put(getMetaDataID(metaData), metaData);
    }

    public List<RenderParam> getRenderParam(RenderParamMetaData metaData) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
        switch (metaData.getSourceType()) {
        case ENUM:
            return loadRenderParamByEnum(metaData.getClassName());
        case CUSTOMERIZED:
            return loadParamFromBeanFactory(metaData.getClassName());
        case ALIAS:
            if(metaDataCache.containsKey(metaData.getAlias())){
                return getRenderParam(metaDataCache.get(metaData.getAlias()));
            }
            else 
                throw new UnsupportedOperationException(
                        "Metadata[" + metaData.getAlias() + "] no found!Please ensure the meta data has bean loaded before or has bean cached!");
        default:
            throw new UnsupportedOperationException(
                    "Unsupport data source[" + metaData.getSourceType().name() + "] by using meta data!");
        }
    }

}
