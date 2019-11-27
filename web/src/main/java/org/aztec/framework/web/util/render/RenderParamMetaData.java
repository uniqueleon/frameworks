package org.aztec.framework.web.util.render;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;

/**
 * 页面渲染数据元数据
 * @author 01390615
 *
 */
public class RenderParamMetaData {
    /**
     * 页面前缀
     */
    private String prefix;
    /**
     * 参数名称
     */
    private String name;
    /**
     * 数据源.支持 枚举,自定义,别名
     */
    private DataSourceType sourceType;
    /**
     * 枚举类名，或自定义数据源类名
     */
    private String className;
    /**
     * 暂未用到，预留
     */
    private List<String> attrs;
    /**
     * 别名
     */
    private String alias;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(DataSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<String> attrs) {
        this.attrs = attrs;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public RenderParamMetaData(String prefix, String name, DataSourceType sourceType, String... params) {
        super();
        this.prefix = prefix;
        this.name = name;
        this.sourceType = sourceType;
        switch (sourceType) {
        case ENUM:
            this.className = params[0];
            break;
        case ALIAS:
            this.alias = params[0];
            break;
        case CUSTOMERIZED:
            this.className = params[0];
            break;
        default :
            break;
        }
    }

    public RenderParamMetaData(String prefix, String name, List<String> attrs) {
        super();
        this.prefix = prefix;
        this.name = name;
        this.sourceType = DataSourceType.CONF;
        this.attrs = attrs;
    }

    public static List<RenderParamMetaData> buildEnumMetaData( String prefix,
            List<String> names,List<Class<? extends Enum>> enumClasses) {
        List<RenderParamMetaData> metaDatas = Lists.newArrayList();
        if (CollectionUtils.isEmpty(enumClasses)) {
            throw new IllegalArgumentException("Should provide enum classes!");
        }
        if (CollectionUtils.isEmpty(names)) {
            throw new IllegalArgumentException("Should provide param names!");
        }
        if (enumClasses.size() != names.size()) {
            throw new IllegalArgumentException("Enum class size doesn't match meta name size");
        }
        for (int i = 0; i < enumClasses.size(); i++) {
            Class<? extends Enum> enumCls = enumClasses.get(i);
            String name = names.get(i);
            RenderParamMetaData metaData = new RenderParamMetaData(prefix, name, DataSourceType.ENUM,
                    enumCls.getName());
            metaDatas.add(metaData);
        }
        return metaDatas;
    }
    
    public static List<RenderParamMetaData> buildCustomerizedParamMetaData(String prefix,
            List<String> names,List<String> beanNames){

        List<RenderParamMetaData> metaDatas = Lists.newArrayList();
        if (CollectionUtils.isEmpty(beanNames)) {
            throw new IllegalArgumentException("Should provide beanNames!");
        }
        if (CollectionUtils.isEmpty(names)) {
            throw new IllegalArgumentException("Should provide param names!");
        }
        if (names.size() != beanNames.size()) {
            throw new IllegalArgumentException("Parameter size doesn't match bean names size!");
        }
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            String beanName = beanNames.get(i);
            RenderParamMetaData metaData = new RenderParamMetaData(prefix, name, DataSourceType.CUSTOMERIZED,
                    beanName);
            metaDatas.add(metaData);
        }
        return metaDatas;
    }
    
    public static List<RenderParamMetaData> buildAliasParamMetaData(String prefix,
            List<String> names,List<String> alias){
        List<RenderParamMetaData> metaDatas = Lists.newArrayList();
        if (CollectionUtils.isEmpty(alias)) {
            throw new IllegalArgumentException("Should provide param alias!");
        }
        if (CollectionUtils.isEmpty(names)) {
            throw new IllegalArgumentException("Should provide param names!");
        }
        if (names.size() != alias.size()) {
            throw new IllegalArgumentException("Parameter size doesn't match alias size!");
        }
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            String alia = alias.get(i);
            RenderParamMetaData metaData = new RenderParamMetaData(prefix, name, DataSourceType.ALIAS,
                    alia);
            metaDatas.add(metaData);
        }
        return metaDatas;
    }
}
