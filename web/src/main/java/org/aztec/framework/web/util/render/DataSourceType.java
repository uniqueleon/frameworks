package org.aztec.framework.web.util.render;

/**
 * 渲染参数数据源
 * @author 01390615
 *
 */
public enum DataSourceType{
    /**
     * 从properties文件读取
     */
    CONF("conf"),
    /**
     * 自定义数据源
     */
    CUSTOMERIZED("cust"),
    /**
     * 别名读取
     */
    ALIAS("alias"),
    /**
     * 枚举类
     */
    ENUM("enum");
    
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private DataSourceType(String value) {
        this.value = value;
    }
    
    public static DataSourceType getSourceType(String configValue){
        for(DataSourceType sType : DataSourceType.values()){
            if(sType.getValue().equals(configValue)){
                return sType;
            }
        }
        return null;
    }
}
