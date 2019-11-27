package org.aztec.framework.api.rest.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 缂栬緫椤甸潰灏佽鐨勮繑鍥炰俊鎭紝鍖呮嫭淇敼鐨刡ean鍜岄〉闈㈤渶瑕佹覆鏌撶殑鍙傛暟
 * 
 * @author tansonlam
 * @create 2016骞�12鏈�30鏃�
 * 
 */
public class EditFormBean<T> implements Serializable {

    private static final long serialVersionUID = -1728998943900910794L;
    private Map<String, List<RenderParam>> renderParams = Maps.newHashMap();
    private T resultBean;

    public EditFormBean(RenderParamContext renderParamContext) {
        super();
        this.renderParams = renderParamContext.getRenderParams();
    }

    public EditFormBean(RenderParamContext renderParamContext, T editData) {
        super();
        this.renderParams = renderParamContext.getRenderParams();
        this.resultBean = editData;
    }

    public Map<String, List<RenderParam>> getRenderParams() {
        return renderParams;
    }

    public void setRenderParams(Map<String, List<RenderParam>> renderParams) {
        this.renderParams = renderParams;
    }

    public T getData() {
        return resultBean;
    }

    public void setData(T data) {
        this.resultBean = data;
    }
}
