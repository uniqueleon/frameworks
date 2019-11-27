package org.aztec.framework.api.rest.entity;

/**
 * 页面渲染的参数
 * 
 * @author tansonlam
 * @create 2016年12月30日
 * 
 */
public class RenderParam {
    /**
     * 值
     */
    private String value;
    /**
     * 显示内容
     */
    private String label;
    /**
     * 扩展字段
     */
    private String extend;

    public RenderParam(String text, String value) {
        super();
        this.value = value;
        this.label = text;
    }

    public RenderParam(String text, String value, String extend) {
        super();
        this.value = value;
        this.label = text;
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

    public void setValue(String value) {
        this.value = value;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

}
